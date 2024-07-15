package com.ssafy.yoganavi.ui.homeUI.teacher.teacherList

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.yoganavi.databinding.FragmentTeacherListBinding
import com.ssafy.yoganavi.ui.core.BaseFragment

class TeacherListFragment :
    BaseFragment<FragmentTeacherListBinding>(FragmentTeacherListBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding.rvTeacherList){
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

            // 구분선
            val dividerItemDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
            addItemDecoration(dividerItemDecoration)
        }
    }
}
