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
import com.ssafy.yoganavi.ui.utils.CERTIFICATION
import com.ssafy.yoganavi.ui.utils.CHECK
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

            if (btn == CHECK) viewModel.registerEmail(email)
            else if (btn == CERTIFICATION) viewModel.checkAuthEmail(number)
        }

        binding.btnSignup.setOnClickListener {
            val email = binding.tieId.text.toString()
            val password = binding.tiePw.text.toString()
            val passwordAgain = binding.tiePwAgain.text.toString()
            val nickname = binding.tieNn.text.toString()
            val isTeacher = binding.tvTeacher.isChecked
            viewModel.signUp(email, password, passwordAgain, nickname, isTeacher)
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
                    if (it is ApiResponse.Success) {
                        showSnackBar(it.data?.message.toString())
                        binding.btnCheck.isEnabled = false
                        binding.btnSignup.isEnabled = true
                    } else {
                        showSnackBar(it.message.toString())
                    }
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
            if (response is ApiResponse.Success && logic == CHECK) {
                binding.btnCheck.text = CERTIFICATION
            }
        }
}
