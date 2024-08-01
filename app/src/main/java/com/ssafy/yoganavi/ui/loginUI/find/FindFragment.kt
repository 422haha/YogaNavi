package com.ssafy.yoganavi.ui.loginUI.find

import android.os.Bundle
import android.os.CountDownTimer
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
import com.ssafy.yoganavi.ui.utils.TIME_OUT
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class FindFragment : BaseFragment<FragmentFindBinding>(FragmentFindBinding::inflate) {
    private val viewModel: FindViewModel by viewModels()
    private val timer = object : CountDownTimer(TIME_OUT * 60, 1000) {
        override fun onTick(time: Long) {
            val secondsRemaining = time / 1000
            val minutes = secondsRemaining / 60
            val seconds = secondsRemaining % 60
            binding.tvTime.text = String.format(Locale.KOREA, "%02d:%02d", minutes, seconds)
        }

        override fun onFinish() {
            binding.tvTime.text = END_TIME
            binding.btnCheck.isEnabled = false
        }
    }

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
            viewModel.findPasswordEvent.collectLatest {
                when (it) {
                    is FindEvent.SendEmailSuccess -> sendEmailSuccess(it)
                    is FindEvent.CheckEmailSuccess -> checkEmailSuccess(it)
                    is FindEvent.RegisterPasswordSuccess -> registerPasswordSuccess(it)
                    is FindEvent.Error -> error(it.message)
                }
            }
        }
    }

    private fun sendEmailSuccess(data: FindEvent<Unit>) {
        binding.btnCheck.isEnabled = true
        binding.btnSignup.isEnabled = false
        showSnackBar(data.message)
        timer.start()
    }

    private fun checkEmailSuccess(data: FindEvent<Unit>) {
        binding.btnCheck.isEnabled = false
        binding.btnSignup.isEnabled = true
        showSnackBar(data.message)
        timer.cancel()
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
