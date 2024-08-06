package com.ssafy.yoganavi.ui.homeUI.myPage.profile.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.snackbar.Snackbar
import com.ssafy.yoganavi.databinding.DialogModifyBinding
import com.ssafy.yoganavi.ui.utils.PASSWORD_DIFF
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ModifyDialog(
    private val navigateToModifyFragment: () -> Unit,
    private val checkPassword: suspend (String) -> Boolean,
) : DialogFragment() {

    private var _binding: DialogModifyBinding? = null
    private val binding get() = _binding!!
    private val dialogScope = CoroutineScope(Dispatchers.Main + Job())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogModifyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListener()
    }

    fun initListener() = with(binding) {
        btnGoModify.setOnClickListener {
            val password = tiePw.text.toString()

            dialogScope.launch {
                val isCorrect = checkPassword(password)

                if (isCorrect) {
                    dismiss()
                    navigateToModifyFragment()
                } else {
                    Snackbar.make(binding.root, PASSWORD_DIFF, Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }
}
