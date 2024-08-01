package com.ssafy.yoganavi.ui.homeUI.teacher.teacherReservation

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.ssafy.yoganavi.R
import com.ssafy.yoganavi.databinding.FragmentTeacherReservationBinding
import com.ssafy.yoganavi.ui.core.BaseFragment
import com.ssafy.yoganavi.ui.homeUI.teacher.teacherReservation.availableList.AvailableAdapter
import com.ssafy.yoganavi.ui.utils.RESERVATION
import com.ssafy.yoganavi.ui.utils.RESERVE
import com.ssafy.yoganavi.ui.utils.convertLongToCalendarDay
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
        AvailableAdapter(::visibleCalendar)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar(false, RESERVE, true, RESERVATION) {
            // TODO: 조건 체크, 널 값 없이 예약되도록
            findNavController().navigate(R.id.action_teacherReservationFragment_to_homeFragment)
        }
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
        preAnimation()
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

    private fun preAnimation() = with(binding) {
        tvSelectClassMethod.isVisible = false
        rgClass.isVisible = false
        tvAvailableClass.isVisible = false
        lyRvAvailableClass.isVisible = false
        tvSelectTerm.isVisible = false
        calendar.isVisible = false
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
            lyRvAvailableClass.apply {
                isVisible = true
                startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_up))
            }
        }
    }

    private fun visibleCalendar(startDate: Long, endDate: Long) = with(binding) {
        lifecycleScope.launch {
            tvSelectTerm.apply {
                isVisible = true
                startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_up))
            }
            calendar.visibility = View.INVISIBLE
            delay(500)
            calendar.apply {
                isVisible = true
                startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in))
            }
            calendar.state().edit().setMinimumDate(convertLongToCalendarDay(startDate))
            calendar.state().edit().setMinimumDate(convertLongToCalendarDay(endDate))
        }
    }
}
