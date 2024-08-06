package com.ssafy.yoganavi.ui.homeUI.myPage.profile.dialog

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.LifecycleCoroutineScope
import com.google.android.material.snackbar.Snackbar
import com.ssafy.yoganavi.databinding.DialogModifyBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class ModifyDialog (
    context: Context,
    private val navigateToModifyFragment:()->Unit,
    private val checkPassword: suspend (String)->Boolean?,
    private val showSnackBar:(String)->Unit,
):AlertDialog(context) {
    private lateinit var binding:DialogModifyBinding
    private val dialogScope = CoroutineScope(Dispatchers.Main + Job())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogModifyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }
    private fun initView()=with(binding){
        setCancelable(true)

        tiePw.setOnFocusChangeListener { v,hasFocus ->
            if(hasFocus){
                keyBordShow()
            }
        }

        btnGoModify.setOnClickListener {
            val password = binding.tiePw.text.toString()
            dialogScope.launch {
                val isPasswordCorrect = checkPassword(password)
                if (isPasswordCorrect!=null) { // 비밀번호가 맞으면
                    if(isPasswordCorrect==true){
                        navigateToModifyFragment()
                        dismiss()
                    }
                    else {
                        showSnackBar("비밀번호가 틀렸습니다.")
                    }
                }
            }
        }
    }
    private fun keyBordShow() {
        window?.let { window?.decorView?.let { it1 ->
            WindowInsetsControllerCompat(it,
                it1
            ).show(WindowInsetsCompat.Type.ime())
        } }
    }
}