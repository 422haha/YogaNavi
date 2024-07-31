package com.ssafy.yoganavi.ui.homeUI.teacher.teacherDetail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ssafy.yoganavi.R
import com.ssafy.yoganavi.data.source.dto.teacher.TeacherData
import com.ssafy.yoganavi.data.source.dto.teacher.TeacherDetailData
import com.ssafy.yoganavi.databinding.FragmentTeacherDetailBinding
import com.ssafy.yoganavi.ui.core.BaseFragment
import com.ssafy.yoganavi.ui.homeUI.teacher.teacherDetail.teacherDetail.TeacherDetailAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class TeacherDetailFragment : BaseFragment<FragmentTeacherDetailBinding>(
    FragmentTeacherDetailBinding::inflate
) {
    private val viewModel: TeacherDetailViewModel by viewModels()
    private val args by navArgs<TeacherDetailFragmentArgs>()
    private val teacherDetailAdapter by lazy {
        TeacherDetailAdapter(goReserve = ::goReserve, navigateToLectureDetailFragment = ::navigateToLectureDetailFragment)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar(false, "강사", true)
        binding.rvTeacherDetail.adapter = teacherDetailAdapter
        getTeacherDetail()
    }

    private fun getTeacherDetail() = viewModel.getTeacherDetail(args.userId, ::bindData)
    private suspend fun bindData(data: TeacherDetailData) = withContext(Dispatchers.Main) {
        val itemList: MutableList<TeacherDetailItem> = mutableListOf()
        val header = TeacherData(
            teacherName = data.teacherName,
            teacherId = data.teacherId,
            content = "기본에 충실한 하타요가를 가르칩니다.\n todo) content 생성창 만들기",
            teacherProfile = data.teacherProfile ?: "",
            teacherSmallProfile = data.teacherSmallProfile ?: "",
            hashtags = data.hashtags,
            liked = data.liked,
            likes = data.likes
        )
        itemList.add(TeacherDetailItem.Header(header))
        //강의 추가
        if (data.teacherRecorded.isNotEmpty())
            itemList.add(TeacherDetailItem.LectureItem(data.teacherRecorded))
        if (data.teacherNotice.isNotEmpty()) {
            val header2 = TeacherData(
                teacherName = "공지사항",
                teacherId = 0,
                content = "",
                teacherProfile = "",
                teacherSmallProfile = "",
                hashtags = arrayListOf(),
                liked = false,
                likes = 0
            )
            itemList.add(TeacherDetailItem.Header(header2))
        }
        data.teacherNotice.forEach {
            itemList.add(TeacherDetailItem.NoticeItem(it))
        }
        teacherDetailAdapter.submitList(itemList)
    }

    private fun goReserve(teacherId: Int) {
        val directions = TeacherDetailFragmentDirections.actionTeacherDetailFragmentToTeacherReservationFragment(teacherId)
        findNavController().navigate(directions)
    }
    private fun navigateToLectureDetailFragment(recordedId: Long=-1) {
        val directions = TeacherDetailFragmentDirections.actionTeacherDetailFragmentToLectureDetailFragment(recordedId)
        findNavController().navigate(directions)
    }
}