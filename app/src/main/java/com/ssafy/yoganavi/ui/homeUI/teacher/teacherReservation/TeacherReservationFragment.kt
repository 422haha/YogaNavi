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
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.ssafy.yoganavi.R
import com.ssafy.yoganavi.databinding.FragmentTeacherReservationBinding
import com.ssafy.yoganavi.ui.core.BaseFragment
import com.ssafy.yoganavi.ui.homeUI.teacher.teacherReservation.availableList.AvailableAdapter
import com.ssafy.yoganavi.ui.utils.END
import com.ssafy.yoganavi.ui.utils.NOTHING
import com.ssafy.yoganavi.ui.utils.ONE_TO_MULTI
import com.ssafy.yoganavi.ui.utils.ONE_TO_ONE
import com.ssafy.yoganavi.ui.utils.PICK_DATE
import com.ssafy.yoganavi.ui.utils.RESERVATION
import com.ssafy.yoganavi.ui.utils.RESERVE
import com.ssafy.yoganavi.ui.utils.SELECT_CLASS
import com.ssafy.yoganavi.ui.utils.START
import com.ssafy.yoganavi.ui.utils.toCalendarDay
import com.ssafy.yoganavi.ui.utils.toLong
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
    private var saveStartDate: CalendarDay? = null
    private var saveEndDate: CalendarDay? = null
    private var liveId = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar(false, RESERVE, true, RESERVATION) {
            if (!(binding.rbOneToOne.isChecked || binding.rbOneToMulti.isChecked)) {
                showSnackBar(SELECT_CLASS)
            } else if (binding.tvNothing.isVisible) {
                showSnackBar(NOTHING)
            } else if (saveStartDate == null || saveEndDate == null) {
                showSnackBar(PICK_DATE)
            } else {
                viewModel.registerLive(
                    liveId,
                    saveStartDate?.toLong(START),
                    saveEndDate?.toLong(END),
                    ::navigateToSchedule
                )
            }
        }
        initView()
        binding.rvAvailableClass.adapter = availableAdapter
        initListener()
        initCollect()
    }

    private fun navigateToSchedule() {
        findNavController().navigate(R.id.action_teacherReservationFragment_to_homeFragment)
    }

    private fun initView() = with(binding) {
        val circularProgressDrawable = CircularProgressDrawable(binding.root.context).apply {
            strokeWidth = 5f
            centerRadius = 30f
        }
        circularProgressDrawable.start()

        if (args.teacherSmallProfile.isBlank()) {
            binding.ivProfile.setImageResource(R.drawable.profilenull)
        } else {
            Glide.with(binding.root)
                .load(args.teacherSmallProfile)
                .placeholder(circularProgressDrawable)
                .into(ivProfile)
        }
        tvTeacherNickname.text = args.teacherName
        if (args.hashtags.isNotBlank()) {
            tvHashtag.text = args.hashtags
            tvHashtag.isVisible = true
        } else tvHashtag.isVisible = false

        preAnimation()
        showRbMethod()
    }

    private fun showRbMethod() = with(binding) {
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
            clickMethod(ONE_TO_ONE)
        }
        rbOneToMulti.setOnClickListener {
            clickMethod(ONE_TO_MULTI)
        }
    }

    private fun clickMethod(method: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getAvailableClass(args.teacherId, method)
            availableAdapter.selectedPosition = -1
        }
        visibleAvailableClass()
    }

    fun initCollect() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.availableList.collectLatest {
                availableAdapter.submitList(it)
                if (it.isNotEmpty()) {
                    binding.rvAvailableClass.isVisible = true
                    binding.tvNothing.isVisible = false
                } else {
                    binding.rvAvailableClass.isVisible = false
                    binding.tvNothing.isVisible = true
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
            saveStartDate = null
            saveEndDate = null
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

    private fun visibleCalendar(startDate: Long, endDate: Long, lectureId: Int) = with(binding) {
        viewLifecycleOwner.lifecycleScope.launch {
            liveId = lectureId
            calendar.clearSelection()
            calendar.state().edit()
                .setMinimumDate(Math.max(startDate, System.currentTimeMillis()).toCalendarDay())
                .setMaximumDate(endDate.toCalendarDay())
                .commit()
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
        }
        calendar.setOnRangeSelectedListener { _, dates ->
            handleRangeSelection(dates)
        }

        calendar.setOnDateChangedListener { _, date, _ ->
            handleDateSelection(date)
        }
    }

    private fun handleRangeSelection(dates: List<CalendarDay>) {
        if (dates.isNotEmpty()) {
            val startDate = dates.first()
            val endDate = dates.last()
            saveStartDate = startDate
            saveEndDate = endDate
        }
    }

    private fun handleDateSelection(date: CalendarDay) {
        if (saveStartDate == null) {
            saveStartDate = date
        } else {
            saveStartDate = null
            saveEndDate = null
        }
    }
}
