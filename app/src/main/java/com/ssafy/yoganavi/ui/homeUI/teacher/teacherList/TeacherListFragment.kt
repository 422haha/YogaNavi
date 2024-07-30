package com.ssafy.yoganavi.ui.homeUI.teacher.teacherList

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.yoganavi.data.source.teacher.FilterData
import com.ssafy.yoganavi.databinding.FragmentTeacherListBinding
import com.ssafy.yoganavi.ui.core.BaseFragment
import com.ssafy.yoganavi.ui.homeUI.teacher.teacherList.teacher.TeacherAdapter
import com.ssafy.yoganavi.ui.utils.TEACHER
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TeacherListFragment :
    BaseFragment<FragmentTeacherListBinding>(FragmentTeacherListBinding::inflate) {

    private val viewModel: TeacherListViewModel by viewModels()
    private val noticeAdapter by lazy {
        TeacherAdapter(
            ::navigateToTeacherFragment,
            ::teacherLikeToggle
        )
    }
    private val args by navArgs<TeacherListFragmentArgs>()
    private var filter = FilterData()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbar(true, TEACHER, false)
        filter = args.filter ?: FilterData()
        if (args.isInit) {
            viewModel.setIsInit()
            binding.ivFilter.isVisible = true
            binding.ivFilterSet.isVisible = false
        } else {
            viewModel.setIsntInit()
            binding.ivFilter.isVisible = false
            binding.ivFilterSet.isVisible = true
        }
        binding.rvTeacherList.adapter = noticeAdapter
        binding.rvTeacherList.addItemDecoration(
            DividerItemDecoration(
                context,
                LinearLayoutManager.VERTICAL
            )
        )
        binding.svSearch.setQuery(viewModel.getSearchKeyword(), false)
        initListener()
        initCollect()
        if (args.sorting == 0) {
            viewModel.setSorting(0, filter)
            binding.rbRecent.isChecked = true
        } else {
            viewModel.setSorting(1, filter)
            binding.rbPopular.isChecked = true
        }
    }

    fun initListener() {
        binding.lyFilter.setOnClickListener {
            val directions = TeacherListFragmentDirections
                .actionTeacherListFragmentToFilterFragment(
                    args.filter,
                    viewModel.getIsInit(),
                    viewModel.getSorting()
                )
            findNavController().navigate(directions)
        }
        binding.svSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.setSearchKeyword(filter, query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                onQueryTextSubmit(newText)
                return false
            }
        })
        binding.rbRecent.setOnClickListener {
            viewModel.setSorting(0, filter)
        }
        binding.rbPopular.setOnClickListener {
            viewModel.setSorting(1, filter)
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

    private fun teacherLikeToggle(teacherId: Int = -1) {
        viewModel.teacherLikeToggle(filter, teacherId)
    }
}