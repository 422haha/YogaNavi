package com.ssafy.yoganavi.ui.homeUI.teacher.teacherReservation

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.ssafy.yoganavi.databinding.FragmentTeacherReservationBinding
import com.ssafy.yoganavi.ui.core.BaseFragment


class TeacherReservationFragment :
    BaseFragment<FragmentTeacherReservationBinding>(FragmentTeacherReservationBinding::inflate) {

    private val args by navArgs<TeacherReservationFragmentArgs>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
//        initListener()
    }

    private fun initView() = with(binding) {
        val circularProgressDrawable = CircularProgressDrawable(binding.root.context).apply {
            strokeWidth = 5f
            centerRadius = 30f
        }
        circularProgressDrawable.start()

        Glide.with(binding.root)
            .load(args.teacherSmallProfile)
            .placeholder(circularProgressDrawable)
            .into(ivProfile)
        tvTeacherNickname.text = args.teacherName
        if (args.hashtags.isNotBlank()) {
            tvHashtag.text = args.hashtags
            tvHashtag.isVisible = true
        } else tvHashtag.isVisible = false
    }
}
