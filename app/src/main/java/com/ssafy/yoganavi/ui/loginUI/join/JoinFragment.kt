package com.ssafy.yoganavi.ui.loginUI.join

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.ssafy.yoganavi.databinding.FragmentJoinBinding
import com.ssafy.yoganavi.ui.core.BaseFragment
import com.ssafy.yoganavi.ui.utils.END_TIME
import com.ssafy.yoganavi.ui.utils.MAX_NAME
import com.ssafy.yoganavi.ui.utils.NAME_IS_MAX
import com.ssafy.yoganavi.ui.utils.PASSWORD_DIFF
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class JoinFragment : BaseFragment<FragmentJoinBinding>(FragmentJoinBinding::inflate) {
    private val viewModel: JoinViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initPasswordWatcher()
        initCollect()
        initListener()
    }

    private fun initListener() {
        binding.tvTeacher.setOnClickListener {
            binding.tvTeacher.toggle()
        }

        binding.btnSend.setOnClickListener {
            hideKeyboard()

            val email = binding.tieId.text.toString()
            viewModel.registerEmail(email)
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
            val nickname = binding.tieNn.text.toString()
            val isTeacher = binding.tvTeacher.isChecked
            viewModel.signUp(email, password, passwordAgain, nickname, isTeacher)
        }

        binding.tieNn.addTextChangedListener { newText ->
            if (newText.toString().length > MAX_NAME) {
                binding.tieNn.error = NAME_IS_MAX
                binding.btnSignup.isEnabled = false
            }else{
                binding.tieNn.error = null
                binding.btnSignup.isEnabled = true
            }
        }
    }

    private fun initCollect() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            collectJoinEvent()
            collectTimer()
        }
    }

    private fun CoroutineScope.collectJoinEvent() = launch {
        viewModel.joinEvent.collectLatest {
            when (it) {
                is JoinEvent.RegisterEmailSuccess -> registerEmailSuccess(it)
                is JoinEvent.CheckEmailSuccess -> checkEmailSuccess(it)
                is JoinEvent.SignUpSuccess -> signUpSuccess(it)
                is JoinEvent.Error -> error(it.message)
            }
        }
    }

    private fun CoroutineScope.collectTimer() = launch {
        viewModel.timeFlow.collect { remainTime ->
            if (remainTime == END_TIME) binding.btnCheck.isEnabled = false
            binding.tvTime.text = remainTime
        }
    }

    private fun registerEmailSuccess(data: JoinEvent<Unit>) {
        binding.btnCheck.isEnabled = true
        binding.btnSignup.isEnabled = false
        showSnackBar(data.message)
        viewModel.timerStart()
    }

    private fun checkEmailSuccess(data: JoinEvent<Unit>) {
        binding.btnCheck.isEnabled = false
        binding.btnSignup.isEnabled = true
        showSnackBar(data.message)
        viewModel.timerEnd()
    }

    private fun signUpSuccess(data: JoinEvent<Unit>) {
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
