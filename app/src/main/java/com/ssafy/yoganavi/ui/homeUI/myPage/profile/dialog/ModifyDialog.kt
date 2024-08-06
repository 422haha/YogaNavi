package com.ssafy.yoganavi.ui.homeUI.myPage.profile.dialog

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.ssafy.yoganavi.R
import com.ssafy.yoganavi.databinding.DialogModifyBinding
import com.ssafy.yoganavi.ui.utils.WRONG_PASSWORD
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

    override fun onStart() {
        super.onStart()
        val width = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 330f, context?.resources?.displayMetrics
        ).toInt()
        dialog?.window?.apply {
            setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
            setBackgroundDrawableResource(R.drawable.rounded_dialog_background)
        }
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
                    tilPw.error = WRONG_PASSWORD
                }
            }
        }
    }
}
