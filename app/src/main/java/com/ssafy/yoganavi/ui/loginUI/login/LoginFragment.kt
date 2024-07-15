package com.ssafy.yoganavi.ui.loginUI.login

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.ssafy.yoganavi.databinding.FragmentLoginBinding
import com.ssafy.yoganavi.ui.core.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {
    private val viewModel: LoginViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            val email = tieId.text.toString()
            val password = tiePassword.text.toString()
            btnLogin.setOnClickListener { viewModel.login(email, password) }
        }
    }
}
