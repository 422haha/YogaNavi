package com.ssafy.yoganavi.ui.homeUI.myPage.registerNotice

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.ssafy.yoganavi.R
import com.ssafy.yoganavi.databinding.FragmentRegisterNoticeBinding
import com.ssafy.yoganavi.ui.core.BaseFragment
import com.ssafy.yoganavi.ui.utils.MANAGEMENT_INSERT
import com.ssafy.yoganavi.ui.utils.MANAGEMENT_UPDATE
import com.ssafy.yoganavi.ui.utils.REGISTER
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivPhoto.visibility = View.GONE
        binding.ivCancel.visibility = View.GONE
        binding.btnAddPhoto.visibility = View.VISIBLE

        if (args.articleId != -1) {
            viewModel.getNotice(args.articleId)
            setToolbar(false, MANAGEMENT_UPDATE, true, REGISTER) {
                val notice = binding.etNotice.text.toString()
                if (notice.isBlank()) {
                    showSnackBar(R.string.notice_info)
                } else {
                    viewModel.updateNotice(notice) {
                        goBackStack()
                    }
                }
            }
        } else {
            setToolbar(false, MANAGEMENT_INSERT, true, REGISTER) {
                val notice = binding.etNotice.text.toString()
                if (notice.isBlank()) {
                    showSnackBar(R.string.notice_info)
                } else {
                    viewModel.insertNotice(notice) {
                        goBackStack()
                    }
                }
            }
        }
        initCollect()
        initListener()
    }

    private fun initListener() {
        binding.btnAddPhoto.setOnClickListener {
            saveEditText()
            openGallery()
        }
        binding.ivCancel.setOnClickListener {
            saveEditText()
            viewModel.removeImage()
        }
    }

    private fun saveEditText() = viewLifecycleOwner.lifecycleScope.launch {
        viewModel.setContent(binding.etNotice.text.toString())
    }

    private fun initCollect() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.notice.collectLatest {
                val notice = it
                binding.etNotice.setText(notice.content)
                if (notice.imageUrl.isNotBlank()) {
                    Glide.with(requireActivity())
                        .load(notice.imageUrl)
                        .into(binding.ivPhoto)
                    binding.ivPhoto.visibility = View.VISIBLE
                    binding.ivCancel.visibility = View.VISIBLE
                    binding.btnAddPhoto.visibility = View.GONE
                } else {
                    binding.ivPhoto.visibility = View.GONE
                    binding.ivCancel.visibility = View.GONE
                    binding.btnAddPhoto.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun openGallery() {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        pickImageLauncher.launch(gallery)
    }

    private val pickImageLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data?.data ?: return@registerForActivityResult
                viewModel.addImage(data.toString())
                binding.ivPhoto.visibility = View.VISIBLE
                binding.ivCancel.visibility = View.VISIBLE
                binding.etNotice.setText(viewModel.notice.value.content)
            }
        }

    private suspend fun goBackStack() = withContext(Dispatchers.Main) {
        findNavController().popBackStack()
    }
}