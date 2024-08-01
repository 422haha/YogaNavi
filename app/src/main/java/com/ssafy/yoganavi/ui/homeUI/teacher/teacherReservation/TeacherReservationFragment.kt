package com.ssafy.yoganavi.ui.homeUI.teacher.teacherReservation

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.ssafy.yoganavi.R
import com.ssafy.yoganavi.databinding.FragmentTeacherReservationBinding
import com.ssafy.yoganavi.ui.core.BaseFragment
import com.ssafy.yoganavi.ui.homeUI.teacher.teacherReservation.availableList.AvailableAdapter
import com.ssafy.yoganavi.ui.utils.RESERVE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TeacherReservationFragment :
    BaseFragment<FragmentTeacherReservationBinding>(FragmentTeacherReservationBinding::inflate) {

    private val args by navArgs<TeacherReservationFragmentArgs>()
    private val viewModel: TeacherReservationViewModel by viewModels()
    private val availableAdapter by lazy {
        AvailableAdapter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar(false,RESERVE,true)
        initView()
        binding.rvAvailableClass.adapter = availableAdapter
        initListener()
        initCollect()
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
            tvSelectClassMethod.startAnimation(
                AnimationUtils.loadAnimation(
                    context,
                    R.anim.slide_up
                )
            )
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
            viewModel.getAvailableClass(args.teacherId, 0)
            visibleAvailableClass()
        }
        rbOneToMulti.setOnClickListener {
            viewModel.getAvailableClass(args.teacherId, 1)
            visibleAvailableClass()
        }
    }

    fun initCollect() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch {
                viewModel.availableList.collectLatest {
                    availableAdapter.submitList(it)
                }
            }
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
