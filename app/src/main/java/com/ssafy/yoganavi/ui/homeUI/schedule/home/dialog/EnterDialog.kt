package com.ssafy.yoganavi.ui.homeUI.schedule.home.dialog

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.TypedValue
import android.view.ViewGroup
import com.ssafy.yoganavi.R
import com.ssafy.yoganavi.databinding.DialogEnterBinding
import com.ssafy.yoganavi.ui.utils.NO_BROADCAST
import com.ssafy.yoganavi.ui.utils.loadImageSequentially

class EnterDialog(
    context: Context,
    private val smallImageUri: String?,
    private val imageUri: String?,
    private val title: String,
    private val content: String,
    private val isOnAir: Boolean,
    private val okCallback: () -> Unit,
) : AlertDialog(context) {

    private lateinit var binding: DialogEnterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DialogEnterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
    }

    private fun initViews() = with(binding) {
        setCancelable(true)

        val width = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 330f, context.resources.displayMetrics
        ).toInt()

        window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)

        window?.setBackgroundDrawableResource(R.drawable.rounded_dialog_background)

        with(binding) {
            if (!imageUri.isNullOrBlank() && !smallImageUri.isNullOrBlank()) {
                ivProfile.loadImageSequentially(smallImageUri, imageUri)
            }

            tvTitle.text = title
            tvContent.text = content
            tvContent.movementMethod = ScrollingMovementMethod()
            btnEnter.isEnabled = isOnAir

            if (!isOnAir) btnEnter.text = NO_BROADCAST
        }

        btnEnter.setOnClickListener {
            okCallback()
            dismiss()
        }

        ivCancel.setOnClickListener { dismiss() }
    }
}
