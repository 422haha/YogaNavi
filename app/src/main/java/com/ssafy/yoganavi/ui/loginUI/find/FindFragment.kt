package com.ssafy.yoganavi.ui.loginUI.find

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ssafy.yoganavi.data.ApiResponse
import com.ssafy.yoganavi.databinding.FragmentFindBinding
import com.ssafy.yoganavi.ui.core.BaseFragment
import com.ssafy.yoganavi.ui.utils.PASSWORD_DIFF
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FindFragment : BaseFragment<FragmentFindBinding>(FragmentFindBinding::inflate) {
    private val viewModel: FindViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initPasswordWatcher()
        initCollect()
        initListener()
    }

    private fun initListener() {
        binding.btnSend.setOnClickListener {
            hideKeyboard()

            val email = binding.tieId.text.toString()
            viewModel.findPassword(email)
        }

        binding.btnCheck.setOnClickListener {
            hideKeyboard()

            val number = binding.tieCn.text.toString().toIntOrNull()
            viewModel.checkAuthEmail(number)
        }

        binding.btnSignup.setOnClickListener {
            val email = binding.tieId.text.toString()
            val password = binding.tiePw.text.toString()
            val passwordAgain = binding.tiePwAgain.text.toString()
            viewModel.registerNewPassword(email, password, passwordAgain)
        }
    }

    private fun initCollect() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            collectFindPasswordEvent()
            collectCheckPasswordEvent()
            collectRegisterPasswordEvent()
        }
    }

    private fun CoroutineScope.collectFindPasswordEvent() = launch {
        viewModel.findPasswordEvent.collectLatest {
            if (it is ApiResponse.Success) {
                showSnackBar(it.data?.message.toString())
                binding.btnCheck.isEnabled = true
                binding.btnSignup.isEnabled = false
            } else {
                showSnackBar(it.message.toString())
            }
        }
    }

    private fun CoroutineScope.collectCheckPasswordEvent() = launch {
        viewModel.checkPasswordEvent.collectLatest {
            if (it is ApiResponse.Success) {
                showSnackBar(it.data?.message.toString())
                binding.btnCheck.isEnabled = false
                binding.btnSignup.isEnabled = true

            } else {
                showSnackBar(it.message.toString())
            }
        }
    }

    private fun CoroutineScope.collectRegisterPasswordEvent() = launch {
        viewModel.registerPasswordEvent.collectLatest {
            if (it is ApiResponse.Success) showSnackBar(it.data?.message.toString())
            else showSnackBar(it.message.toString())
        }
    }

    private fun initPasswordWatcher() = binding.tiePwAgain.addTextChangedListener {
        if (it.toString() != binding.tiePw.text.toString()) {
            binding.tiePwAgain.error = PASSWORD_DIFF
        } else {
            binding.tiePwAgain.error = null
        }
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
