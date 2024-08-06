package com.ssafy.yoganavi.ui.homeUI.myPage.profile.dialog

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import com.ssafy.yoganavi.databinding.DialogModifyBinding

class ModifyDialog(
    context: Context,
    private val checkPassword: (String) -> Unit,
) : AlertDialog(context) {
    lateinit var binding: DialogModifyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogModifyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() = with(binding) {
        setCancelable(true)

        tiePw.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
            }
        }

        btnGoModify.setOnClickListener {
            val password = binding.tiePw.text.toString()
            checkPassword(password)

        }
    }
}