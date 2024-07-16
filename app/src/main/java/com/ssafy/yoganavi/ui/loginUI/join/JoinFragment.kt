package com.ssafy.yoganavi.ui.loginUI.join

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ssafy.yoganavi.data.ApiResponse
import com.ssafy.yoganavi.data.source.signup.SignUpResponse
import com.ssafy.yoganavi.databinding.FragmentJoinBinding
import com.ssafy.yoganavi.ui.core.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class JoinFragment : BaseFragment<FragmentJoinBinding>(FragmentJoinBinding::inflate) {
    private val viewModel: JoinViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        test()
        initCollect()
        initListener()
    }

    private fun test() {
        with(binding) {
            tieId.setText("csjune99@naver.com")
            tiePw.setText("1234")
            tiePwAgain.setText("1234")
            tieNn.setText("닉네임")
        }
    }

    private fun initListener() {
        binding.tvTeacher.setOnClickListener {
            binding.tvTeacher.toggle()
        }

        binding.btnCheck.setOnClickListener {
            val email = binding.tieId.text.toString()
            val number = binding.tieCn.text.toString().toIntOrNull()
            val btn = binding.btnCheck.text.toString()

            if (btn == "확인") viewModel.registerEmail(email)
            else if (btn == "인증") viewModel.checkAuthEmail(number)
        }

        binding.btnSignup.setOnClickListener {
            val email = binding.tieId.text.toString()
            val password = binding.tiePw.text.toString()
            val nickname = binding.tieNn.text.toString()
            val isTeacher = binding.tvTeacher.isChecked
            viewModel.signUp(email, password, nickname, isTeacher)
        }
    }

    private fun initCollect() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch {
                viewModel.registerEmailEvent.collectLatest {
                    setLogic(it, binding.btnCheck.text.toString())
                    if (it is ApiResponse.Success) showSnackBar(it.data?.message.toString())
                    else showSnackBar(it.message.toString())
                }
            }

            launch {
                viewModel.checkEmailEvent.collectLatest {
                    setLogic(it, binding.btnCheck.text.toString())
                    if (it is ApiResponse.Success) showSnackBar(it.data?.message.toString())
                    else showSnackBar(it.message.toString())
                }
            }

            launch {
                viewModel.signUpEvent.collectLatest {
                    if (it is ApiResponse.Success) showSnackBar(it.data?.message.toString())
                    else showSnackBar(it.message.toString())
                }
            }
        }
    }

    private suspend fun setLogic(response: ApiResponse<SignUpResponse>, logic: String) =
        withContext(Dispatchers.Main) {
            if (response is ApiResponse.Success) {
                binding.btnCheck.text = if (logic == "확인") {
                    "인증"
                } else {
                    "확인"
                }
            }
        }
}
