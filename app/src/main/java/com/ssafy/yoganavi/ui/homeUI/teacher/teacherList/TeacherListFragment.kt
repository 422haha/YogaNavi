package com.ssafy.yoganavi.ui.homeUI.teacher.teacherList

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.yoganavi.R
import com.ssafy.yoganavi.data.source.teacher.FilterData
import com.ssafy.yoganavi.databinding.FragmentTeacherListBinding
import com.ssafy.yoganavi.ui.core.BaseFragment
import com.ssafy.yoganavi.ui.homeUI.teacher.teacherList.Teacher.TeacherAdapter
import com.ssafy.yoganavi.ui.utils.TEACHER
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

class TeacherListFragment :
    BaseFragment<FragmentTeacherListBinding>(FragmentTeacherListBinding::inflate) {

    private val viewModel: TeacherListViewModel by viewModels()
    private val noticeAdapter by lazy { TeacherAdapter(::navigateToTeacherFragment) }
    private val args by navArgs<TeacherListFragmentArgs>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbar(true, TEACHER, false)

        binding.rvTeacherList.adapter = noticeAdapter
        binding.rvTeacherList.addItemDecoration(
            DividerItemDecoration(
                context,
                LinearLayoutManager.VERTICAL
            )
        )
        Timber.d("싸피 arguments : ${args.filter ?: FilterData()}")
        initListener()
//        initCollect()
    }

    fun initListener() {
        binding.ivFilter.setOnClickListener {
            val directions = TeacherListFragmentDirections
                .actionTeacherListFragmentToFilterFragment(args.filter)
            findNavController().navigate(directions)
        }
    }

    fun initCollect() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.teacherList.collectLatest {
                noticeAdapter.submitList(it)
            }
        }
    }

    private fun navigateToTeacherFragment(userId: Int = -1) {
        val directions = TeacherListFragmentDirections
            .actionTeacherListFragmentToTeacherDetailFragment(userId)
        findNavController().navigate(directions)
    }
}