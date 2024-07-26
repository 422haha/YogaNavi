package com.ssafy.yoganavi.ui.homeUI.teacher.teacherList

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.yoganavi.R
import com.ssafy.yoganavi.databinding.FragmentTeacherListBinding
import com.ssafy.yoganavi.ui.core.BaseFragment
import com.ssafy.yoganavi.ui.homeUI.myPage.notice.NoticeFragmentDirections
import com.ssafy.yoganavi.ui.homeUI.teacher.teacherList.Teacher.TeacherAdapter
import com.ssafy.yoganavi.ui.homeUI.teacher.teacherList.TeacherListFragmentDirections.Companion.actionTeacherListFragmentToTeacherDetailFragment
import com.ssafy.yoganavi.ui.utils.TEACHER

class TeacherListFragment :
    BaseFragment<FragmentTeacherListBinding>(FragmentTeacherListBinding::inflate) {

    private val viewModel: TeacherListViewModel by viewModels()
    private val adapter by lazy { TeacherAdapter(::navigateToTeacherFragment) }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbar(true, TEACHER, false)

        binding.rvTeacherList.adapter = adapter
        binding.rvTeacherList.addItemDecoration(
            DividerItemDecoration(
                context,
                LinearLayoutManager.VERTICAL
            )
        )

        initListener()
    }

    fun initListener() {
        binding.ivFilter.setOnClickListener {
            findNavController().navigate(R.id.action_teacherListFragment_to_filterFragment)
        }

    }
    private fun navigateToTeacherFragment(userId : Int = -1) {
        val directions = TeacherListFragmentDirections
            .actionTeacherListFragmentToTeacherDetailFragment(userId)
        findNavController().navigate(directions)
    }
}