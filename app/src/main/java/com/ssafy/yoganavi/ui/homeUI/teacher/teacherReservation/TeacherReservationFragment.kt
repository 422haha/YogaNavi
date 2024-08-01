package com.ssafy.yoganavi.ui.homeUI.teacher.teacherReservation

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.ssafy.yoganavi.R
import com.ssafy.yoganavi.databinding.FragmentTeacherReservationBinding
import com.ssafy.yoganavi.ui.core.BaseFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class TeacherReservationFragment :
    BaseFragment<FragmentTeacherReservationBinding>(FragmentTeacherReservationBinding::inflate) {

    private val args by navArgs<TeacherReservationFragmentArgs>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
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
        tvSelectClassMethod.isVisible = false
        rgClass.isVisible = false
        lifecycleScope.launch {
            delay(300)
            tvSelectClassMethod.isVisible = true
            tvSelectClassMethod.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_up))
            delay(500)
            rgClass.isVisible = true
            rbOneToMulti.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_up))
            rbOneToOne.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_up))
        }
        tvAvailableClass.isVisible = false
        lyRvAvailableClass.isVisible = false
        tvSelectTerm.isVisible = false
        calendar.isVisible = false
    }

    private fun initListener() = with(binding) {
        rbOneToOne.setOnClickListener {
            visibleAvailableClass()
        }
        rbOneToMulti.setOnClickListener {
            visibleAvailableClass()
        }
    }

    private fun visibleAvailableClass() = with(binding) {
        lifecycleScope.launch {
            tvSelectTerm.isVisible = false
            calendar.isVisible = false
            tvAvailableClass.isVisible = false
            lyRvAvailableClass.isVisible = false
            tvAvailableClass.apply {
                isVisible = true
                startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_up))
            }
            delay(500)
            // TODO: 이거 rv 처리
            lyRvAvailableClass.apply {
                isVisible = true
                startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_up))
            }
        }
    }

}
