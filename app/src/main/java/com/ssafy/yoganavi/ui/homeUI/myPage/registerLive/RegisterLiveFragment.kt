package com.ssafy.yoganavi.ui.homeUI.myPage.registerLive

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import androidx.core.view.get
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.ssafy.yoganavi.R
import com.ssafy.yoganavi.databinding.FragmentRegisterLiveBinding
import com.ssafy.yoganavi.ui.core.BaseFragment
import com.ssafy.yoganavi.ui.utils.END
import com.ssafy.yoganavi.ui.utils.MODIFY_LIVE
import com.ssafy.yoganavi.ui.utils.REGISTER
import com.ssafy.yoganavi.ui.utils.REGISTER_LIVE
import com.ssafy.yoganavi.ui.utils.START
import com.ssafy.yoganavi.ui.utils.formatDotDate
import com.ssafy.yoganavi.ui.utils.formatTime
import com.ssafy.yoganavi.ui.utils.formatZeroDate
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class RegisterLiveFragment :
    BaseFragment<FragmentRegisterLiveBinding>(FragmentRegisterLiveBinding::inflate) {

    private val args: RegisterLiveFragmentArgs by navArgs()

    private val viewModel: RegisterLiveViewModel by viewModels()

    private lateinit var startDatePickerDialog: DatePickerDialog
    private lateinit var endDatePickerDialog: DatePickerDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (args.liveId != -1)
            setModifyInfo()

        initListener()

        setToolbar(isBottomNavigationVisible = false,
            title = if (args.liveId == -1) REGISTER_LIVE else MODIFY_LIVE,
            canGoBack = true,
            menuItem = REGISTER,
            menuListener = {
                viewModel.liveLectureData.apply {
                    with(binding) {
                        liveTitle = etTitle.text.toString()
                        liveContent = etContent.text.toString()

                        // TODO 가능 요일 선택(백앤드 배열 or string 결정 후)
                        availableDay = ""
                        maxLiveNum = 1
                    }
                }

                viewModel.createLive()

                findNavController().popBackStack()
            })
    }

    private fun setModifyInfo() {
        viewModel.getLive(args.liveId) {
            with(binding) {
                with(viewModel) {
                    etTitle.setText(liveLectureData.liveTitle)
                    etContent.setText(liveLectureData.liveContent)

                    // TODO: 가능 요일 선택(백앤드 배열 or string 결정 후)

                    tieStart.setText(formatDotDate(liveLectureData.startDate))
                    tieEnd.setText(formatDotDate(liveLectureData.endDate))

                    btnStart.text = formatTime(liveLectureData.startTime)
                    btnEnd.text = formatTime(liveLectureData.endTime)

                    val size = resources.getStringArray(R.array.maxnum_array).size
                    if (liveLectureData.maxLiveNum in 1..size)
                        spMaxNum[liveLectureData.maxLiveNum - 1]
                }
            }
        }
    }

    private fun initListener() {
        with(binding) {
            tieStart.setOnClickListener { showCalendar(START) }

            tieEnd.setOnClickListener { showCalendar(END) }

            btnStart.setOnClickListener { showTimePicker(START) }

            btnEnd.setOnClickListener { showTimePicker(END) }
        }
    }

    private fun showCalendar(state: Int) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        if (state == START) {
            startDatePickerDialog = DatePickerDialog(
                requireContext(),
                R.style.MySpinnerDatePickerStyle,
                { _, sYear, sMonth, sDay ->
                    calendar.set(sYear, sMonth, sDay)

                    with(viewModel.liveLectureData) {
                        binding.tieStart.setText("$sYear.${sMonth + 1}.$sDay")
                        startDate = calendar.timeInMillis

                        if (endDate < startDate && endDate == -1L) {
                            binding.tieEnd.setText("$sYear.${sMonth + 1}.$sDay")
                            endDate = calendar.timeInMillis
                        }
                    }
                }, year, month, day
            ).apply { show() }
        } else if (state == END) {
            endDatePickerDialog = DatePickerDialog(
                requireContext(),
                R.style.MySpinnerDatePickerStyle,
                { _, sYear, sMonth, sDay ->
                    calendar.set(sYear, sMonth, sDay)

                    binding.tieEnd.setText("$sYear.${sMonth + 1}.$sDay")
                    viewModel.liveLectureData.endDate = calendar.timeInMillis
                }, year, month, day
            ).apply {
                datePicker.minDate = viewModel.liveLectureData.startDate
                show()
            }
        }
    }

    private fun showTimePicker(state: Int) {
        val title: String = if (state == START) "시작" else "종료"

        val materialTimePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
            .setTitleText("$title 시간")
            .setNegativeButtonText("취소")
            .setPositiveButtonText("확인")
            .setTheme(R.style.CustomMaterialTimePickerTheme)
            .build()

        materialTimePicker.addOnPositiveButtonClickListener {
            val timeStr: String = formatZeroDate(materialTimePicker.hour, materialTimePicker.minute)
            val pickTime: Long =
                ((materialTimePicker.hour * 3600) + (materialTimePicker.minute * 60)).toLong()

            if (state == START) {
                binding.btnStart.text = timeStr
                viewModel.liveLectureData.startTime = pickTime
            } else if (state == END) {
                binding.btnEnd.text = timeStr
                viewModel.liveLectureData.endTime = pickTime
            }
        }

        materialTimePicker.show(childFragmentManager, "fragment_tag")
    }
}
