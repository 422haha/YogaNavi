package com.ssafy.yoganavi.ui.homeUI.teacher.teacherDetail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ssafy.yoganavi.R
import com.ssafy.yoganavi.data.source.dto.teacher.TeacherData
import com.ssafy.yoganavi.databinding.FragmentTeacherDetailBinding
import com.ssafy.yoganavi.ui.core.BaseFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TeacherDetailFragment :
    BaseFragment<FragmentTeacherDetailBinding>(FragmentTeacherDetailBinding::inflate) {

    private val viewModel: TeacherDetailViewModel by viewModels()
    private val args by navArgs<TeacherDetailFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: adapter 두개 세팅
        initListener()
        initCollect()
        getTeacherDetail()
    }

    private fun initListener() {
        with(binding) {
            btnReserve.setOnClickListener {
                findNavController().navigate(R.id.action_teacherDetailFragment_to_teacherReservationFragment)
            }
        }
    }

    private fun initCollect() {

    }

    private fun getTeacherDetail() = viewModel.getTeacherDetail(args.userId, ::bindData)
    private suspend fun bindData(data: TeacherData) = withContext(Dispatchers.Main) {
        // TODO: data로 주입받은 값으로 binding 변수들에 할당해서 세팅해주기
    }
}
