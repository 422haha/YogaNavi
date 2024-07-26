package com.ssafy.yoganavi.ui.homeUI.myPage.modify

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.ssafy.yoganavi.data.repository.response.DetailResponse
import com.ssafy.yoganavi.data.source.mypage.Profile
import com.ssafy.yoganavi.databinding.FragmentModifyBinding
import com.ssafy.yoganavi.ui.core.BaseFragment
import com.ssafy.yoganavi.ui.homeUI.myPage.modify.hashtag.HashTagAdapter
import com.ssafy.yoganavi.ui.utils.MODIFY
import com.ssafy.yoganavi.ui.utils.NO_AUTH
import com.ssafy.yoganavi.ui.utils.PASSWORD_DIFF
import com.ssafy.yoganavi.ui.utils.getImagePath
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class ModifyFragment : BaseFragment<FragmentModifyBinding>(FragmentModifyBinding::inflate) {
    private val viewModel: ModifyViewModel by viewModels()
    private val hashTagAdapter by lazy { HashTagAdapter(::deleteHashTag) }
    private val imageUriLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode != Activity.RESULT_OK) return@registerForActivityResult
            val imageUri = result.data?.data ?: return@registerForActivityResult

            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                val imagePath = getImagePath(requireContext(), imageUri, 10)
                if (imagePath.isBlank()) return@launch
                val uri = Uri.parse(imagePath)

                withContext(Dispatchers.Main) {
                    binding.ivIcon.setImageURI(uri)
                    viewModel.setThumbnail(path = imagePath)
                }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar(false, MODIFY, true)

        initAdapter()
        initListener()
        getProfile()
        initCollect()
    }

    private fun deleteHashTag(index: Int) = viewModel.deleteHashTag(index)

    private fun initAdapter() = with(binding) {
        val manager = FlexboxLayoutManager(requireContext()).apply {
            flexWrap = FlexWrap.WRAP
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.FLEX_START
        }

        rvHashTag.apply {
            layoutManager = manager
            adapter = hashTagAdapter
        }
    }

    private fun getProfile() = viewModel.getProfile(::bindData)

    private suspend fun bindData(data: Profile) = withContext(Dispatchers.Main) {
        with(binding) {
            tieNn.setText(data.nickname)

            if (data.imageUrl?.isNotBlank() == true) {
                Glide.with(requireContext())
                    .load(data.imageUrl)
                    .into(ivIcon)
            }

            if (data.teacher) {
                tvHashtag.visibility = View.VISIBLE
                tilHashTag.visibility = View.VISIBLE
            }
        }
    }

    private fun initCollect() = viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.hashtagList.collectLatest { hashtagList ->
                hashTagAdapter.submitList(hashtagList)
            }
        }
    }

    private fun initListener() = with(binding) {
        cvIcon.setOnClickListener {
            addThumbnail()
        }

        check.setOnClickListener {
            val nickname = tieNn.text?.toString() ?: ""
            val password = tiePw.text?.toString() ?: ""
            viewModel.modifyProfile(nickname, password) { event ->
                when (event) {
                    is DetailResponse.AuthError -> showSnackBar(NO_AUTH)
                    is DetailResponse.Error -> showSnackBar(event.message)
                    is DetailResponse.Success -> moveToBackStack()
                }
            }
        }

        tiePw.addTextChangedListener { editText ->
            val text = editText?.toString() ?: ""
            val pwAgain = tiePwAgain.text?.toString() ?: ""
            check.isEnabled = !(text.isNotBlank() && text != pwAgain)
        }

        tiePwAgain.addTextChangedListener { text ->
            val password = tiePw.text.toString()
            if (password != text.toString()) {
                tiePwAgain.error = PASSWORD_DIFF
                check.isEnabled = false
            } else {
                tiePwAgain.error = null
                check.isEnabled = true
            }
        }

        tieHashTag.setOnEditorActionListener { _, actionId, _ ->
            hideKeyboard()
            if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                val text = tieHashTag.text?.toString() ?: ""
                tieHashTag.text?.clear()
                if (text.isBlank()) return@setOnEditorActionListener true

                viewModel.addHashTag(text.trim())
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }

    private fun moveToBackStack() = viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
        findNavController().popBackStack()
    }

    private fun addThumbnail() {
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }
        imageUriLauncher.launch(intent)
    }

    private fun hideKeyboard() {
        if (activity != null && requireActivity().currentFocus != null) {
            val inputManager: InputMethodManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

            inputManager.hideSoftInputFromWindow(
                requireActivity().currentFocus?.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }
}
