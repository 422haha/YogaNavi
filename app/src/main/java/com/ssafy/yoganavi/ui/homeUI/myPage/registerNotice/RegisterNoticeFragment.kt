package com.ssafy.yoganavi.ui.homeUI.myPage.registerNotice

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ssafy.yoganavi.R
import com.ssafy.yoganavi.databinding.FragmentRegisterNoticeBinding
import com.ssafy.yoganavi.ui.core.BaseFragment
import com.ssafy.yoganavi.ui.utils.MANAGEMENT_INSERT
import com.ssafy.yoganavi.ui.utils.MANAGEMENT_UPDATE
import com.ssafy.yoganavi.ui.utils.REGISTER
import com.ssafy.yoganavi.ui.utils.getImagePath
import com.ssafy.yoganavi.ui.utils.loadImageSequentially
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class RegisterNoticeFragment :
    BaseFragment<FragmentRegisterNoticeBinding>(FragmentRegisterNoticeBinding::inflate) {
    private val args by navArgs<RegisterNoticeFragmentArgs>()
    private val viewModel: RegisterNoticeViewModel by viewModels()
    private val imageUriLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode != Activity.RESULT_OK) return@registerForActivityResult
            val imageUri = result.data?.data ?: return@registerForActivityResult

            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                val (imagePath, miniPath) = getImagePath(requireContext(), imageUri)
                viewModel.addImage(imagePath, miniPath)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivPhoto.visibility = View.GONE
        binding.btnDeletePhoto.visibility = View.GONE
        binding.btnAddPhoto.visibility = View.VISIBLE

        if (args.articleId != -1) {
            viewModel.getNotice(args.articleId)
            setToolbar(false, MANAGEMENT_UPDATE, true, REGISTER) {
                val notice = binding.etNotice.text.toString()

                if (notice.isBlank()) showSnackBar(R.string.notice_info)
                else viewModel.updateNotice(notice, ::goBackStack)
            }
        } else {
            setToolbar(false, MANAGEMENT_INSERT, true, REGISTER) {
                val notice = binding.etNotice.text.toString()

                if (notice.isBlank()) showSnackBar(R.string.notice_info)
                else viewModel.insertNotice(notice, ::goBackStack)
            }
        }
        initCollect()
        initListener()
    }

    private fun initListener() {
        binding.btnAddPhoto.setOnClickListener {
            saveEditText()
            addPhoto()
        }
        binding.btnDeletePhoto.setOnClickListener {
            saveEditText()
            viewModel.removeImage()
        }
    }

    private fun saveEditText() = viewLifecycleOwner.lifecycleScope.launch {
        viewModel.setContent(binding.etNotice.text.toString())
    }

    private fun initCollect() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.notice.collectLatest { notice ->
                binding.etNotice.setText(notice.content)
                if (notice.imageUrl?.isNotBlank() == true && notice.imageUrlSmall?.isNotBlank() == true) {
                    binding.ivPhoto.loadImageSequentially(notice.imageUrl, notice.imageUrlSmall)
                    binding.ivPhoto.visibility = View.VISIBLE
                    binding.btnDeletePhoto.visibility = View.VISIBLE
                    binding.btnAddPhoto.visibility = View.GONE
                } else if (notice.imageUrlPath.isNotBlank()) {
                    binding.ivPhoto.setImageURI(notice.imageUrlPath.toUri())
                    binding.ivPhoto.visibility = View.VISIBLE
                    binding.btnDeletePhoto.visibility = View.VISIBLE
                    binding.btnAddPhoto.visibility = View.GONE
                    binding.ivPhoto.viewTreeObserver.addOnGlobalLayoutListener(
                        object : ViewTreeObserver.OnGlobalLayoutListener {
                            override fun onGlobalLayout() {
                                binding.ivPhoto.viewTreeObserver.removeOnGlobalLayoutListener(this)
                                binding.scRegisterNotice.post {
                                    binding.scRegisterNotice.smoothScrollTo(
                                        0,
                                        binding.scRegisterNotice.getChildAt(0).bottom
                                    )
                                }
                            }
                        }
                    )
                } else {
                    binding.ivPhoto.visibility = View.INVISIBLE
                    binding.btnDeletePhoto.visibility = View.GONE
                    binding.btnAddPhoto.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun addPhoto() {
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }
        imageUriLauncher.launch(intent)
    }

    private suspend fun goBackStack() = withContext(Dispatchers.Main) {
        findNavController().popBackStack()
    }
}