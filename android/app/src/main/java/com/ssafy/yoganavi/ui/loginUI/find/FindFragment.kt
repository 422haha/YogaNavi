package com.ssafy.yoganavi.ui.loginUI.find

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.ssafy.yoganavi.databinding.FragmentFindBinding
import com.ssafy.yoganavi.ui.core.BaseFragment
import com.ssafy.yoganavi.ui.utils.END_TIME
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
            binding.btnSend.isEnabled = false
            hideKeyboard()

            val email = binding.tieId.text.toString()
            viewModel.sendEmail(email)
        }

        binding.btnCheck.setOnClickListener {
            hideKeyboard()

            val email = binding.tieId.text.toString()
            val number = binding.tieCn.text.toString().toIntOrNull()
            viewModel.checkAuthEmail(email, number)
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
            collectFindEvent()
            collectTimer()
        }
    }

    private fun CoroutineScope.collectFindEvent() = launch {
        viewModel.findPasswordEvent.collectLatest {
            when (it) {
                is FindEvent.SendEmailSuccess -> sendEmailSuccess(it)
                is FindEvent.CheckEmailSuccess -> checkEmailSuccess(it)
                is FindEvent.RegisterPasswordSuccess -> registerPasswordSuccess(it)
                is FindEvent.Error -> error(it.message)
            }
            binding.btnSend.isEnabled = true
        }
    }

    private fun CoroutineScope.collectTimer() = launch {
        viewModel.timeFlow.collect { remainTime ->
            if (remainTime == END_TIME) binding.btnCheck.isEnabled = false
            binding.tvTime.text = remainTime
        }
    }

    private fun sendEmailSuccess(data: FindEvent<Unit>) {
        binding.btnCheck.isEnabled = true
        binding.btnSignup.isEnabled = false
        showSnackBar(data.message)
        viewModel.timerStart()
    }

    private fun checkEmailSuccess(data: FindEvent<Unit>) {
        binding.btnCheck.isEnabled = false
        binding.btnSignup.isEnabled = true
        showSnackBar(data.message)
        viewModel.timerEnd()
    }

    private fun registerPasswordSuccess(data: FindEvent<Unit>) {
        showSnackBar(data.message)
        findNavController().popBackStack()
    }

    private fun error(message: String?) = message?.let {
        showSnackBar(it)
    }

    private fun initPasswordWatcher() = binding.tiePwAgain.addTextChangedListener {
        if (it.toString() != binding.tiePw.text.toString()) {
            binding.tiePwAgain.error = PASSWORD_DIFF
        } else {
            binding.tiePwAgain.error = null
        }
    }
}
