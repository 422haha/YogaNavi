package com.ssafy.yoganavi.ui.loginUI.login

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ssafy.yoganavi.data.ApiResponse
import com.ssafy.yoganavi.databinding.FragmentLoginBinding
import com.ssafy.yoganavi.ui.core.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {
    private val viewModel: LoginViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initCollect()
        initListener()
    }

    private fun initListener() {
        binding.btnLogin.setOnClickListener {
            val email = binding.tieId.text.toString()
            val password = binding.tiePassword.text.toString()
            viewModel.login(email, password)
        }
    }

    private fun initCollect() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.loginEvent.collectLatest {
                if(it is ApiResponse.Success) showSnackBar(it.data?.message.toString())
                else showSnackBar(it.message.toString())
            }
        }
    }
}
