package com.ssafy.yoganavi.ui.homeUI.myPage.registerNotice

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.ssafy.yoganavi.databinding.FragmentRegisterNoticeBinding
import com.ssafy.yoganavi.ui.core.BaseFragment
import com.ssafy.yoganavi.ui.utils.MANAGEMENT_INSERT
import com.ssafy.yoganavi.ui.utils.MANAGEMENT_UPDATE
import com.ssafy.yoganavi.ui.utils.REGISTER
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class RegisterNoticeFragment :
    BaseFragment<FragmentRegisterNoticeBinding>(FragmentRegisterNoticeBinding::inflate) {
    private val args by navArgs<RegisterNoticeFragmentArgs>()
    private var imageUri: Uri? = null
    private val viewModel: RegisterNoticeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.divider5.visibility = View.GONE
        binding.ivPhoto.visibility = View.GONE
        binding.ivCancel2.visibility = View.GONE
        binding.btnAddPhoto.visibility = View.VISIBLE
        if (args.articleId != -1) {
            // TODO: 수정. 값 가져와서 띄워주기

            viewModel.getNotice(args.articleId)
            initCollect()
            setToolbar(false, MANAGEMENT_UPDATE, true, REGISTER) { Timber.d("수정 구현 ㄱㄱ!") }
        } else {
            setToolbar(false, MANAGEMENT_INSERT, true, REGISTER) { Timber.d("등록 구현 ㄱㄱ!") }
        }
        initListener()
    }

    private fun initListener() {
        binding.btnAddPhoto.setOnClickListener {
            //갤러리에서 사진 선택
            openGallery()

        }

        binding.ivCancel2.setOnClickListener {
            viewModel.removeImage()
//            Glide.with(binding.root)
//                .clear(binding.ivPhoto)
//            binding.ivCancel2.visibility = View.GONE
//            binding.ivPhoto.visibility = View.GONE
//            binding.btnAddPhoto.visibility = View.VISIBLE
        }
    }

    private fun initCollect() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.notice.collectLatest {
                val notice = it
                binding.etNotice.setText(notice.content)
                if (notice.imageUrl.isNotBlank()) {
                    //사진 유효성검사도 해야하나?
                    Glide.with(requireActivity())
                        .load(notice.imageUrl)
                        .into(binding.ivPhoto)
                    Log.d("TAG싸피", "initCollect: ${notice.imageUrl}")
                    binding.ivPhoto.visibility = View.VISIBLE
                    binding.ivCancel2.visibility = View.VISIBLE
                    binding.btnAddPhoto.visibility = View.GONE
                } else {
                    binding.ivPhoto.visibility = View.GONE
                    binding.ivCancel2.visibility = View.GONE
                    binding.btnAddPhoto.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun openGallery() {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        pickImageLauncher.launch(gallery)
//        //가져왔으면
        binding.ivPhoto.visibility = View.VISIBLE
        binding.ivCancel2.visibility = View.VISIBLE
    }

    private val pickImageLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data?.data ?: return@registerForActivityResult
                viewModel.addImage(data.toString())
//                data?.data?.let {
//                    imageUri = it
//                    binding.ivPhoto.setImageURI(imageUri)
//                    Log.d("TAG싸피", "initCollect: ${imageUri}")
//                }
            }
        }
}