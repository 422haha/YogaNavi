package com.ssafy.yoganavi.ui.homeUI.teacher.teacherList

import android.os.Bundle
import android.view.View
import android.widget.ImageView
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
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.yoganavi.data.source.dto.teacher.FilterData
import com.ssafy.yoganavi.databinding.FragmentTeacherListBinding
import com.ssafy.yoganavi.ui.core.BaseFragment
import com.ssafy.yoganavi.ui.homeUI.teacher.teacherList.teacher.TeacherAdapter
import com.ssafy.yoganavi.ui.utils.EMPTY_TEACHER
import com.ssafy.yoganavi.ui.utils.POPULAR
import com.ssafy.yoganavi.ui.utils.RECENT
import com.ssafy.yoganavi.ui.utils.TEACHER
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TeacherListFragment : BaseFragment<FragmentTeacherListBinding>(
    FragmentTeacherListBinding::inflate
) {

    private val viewModel: TeacherListViewModel by viewModels()
    private val teacherAdapter by lazy {
        TeacherAdapter(
            navigateToRegisterTeacherFragment = ::navigateToTeacherFragment,
            teacherLikeToggle = ::teacherLikeToggle,
            loadS3Image = ::loadS3Image
        ).apply {
            registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                    binding.rvTeacherList.scrollToPosition(0)
                    super.onItemRangeChanged(positionStart, itemCount)
                }
            })
        }
    }

    private val args by navArgs<TeacherListFragmentArgs>()
    private var filter = FilterData()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
        binding.rvTeacherList.adapter = teacherAdapter
        binding.rvTeacherList.addItemDecoration(
            DividerItemDecoration(
                context,
                LinearLayoutManager.VERTICAL
            )
        )
        binding.svSearch.setQuery(viewModel.getSearchKeyword(), false)
        initListener()
        initCollect()
        if (viewModel.teacherList.value.isEmpty()) {
            when (args.sorting) {
                RECENT -> {
                    viewLifecycleOwner.lifecycleScope.launch {
                        viewModel.setSorting(RECENT, filter)
                    }
                }
                POPULAR -> {
                    viewLifecycleOwner.lifecycleScope.launch {
                        viewModel.setSorting(POPULAR, filter)
                    }
                }
                else -> {
                    viewLifecycleOwner.lifecycleScope.launch {
                        viewModel.setSorting(viewModel.sorting.value, filter)
                        if (viewModel.sorting.value == RECENT) {
                            binding.rbRecent.isChecked = true
                        } else {
                            binding.rbPopular.isChecked = true
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        setToolbar(true, TEACHER, false)
    }

    fun initListener() = with(binding) {
        lyFilter.setOnClickListener {
            val directions = TeacherListFragmentDirections
                .actionTeacherListFragmentToFilterFragment(
                    filter,
                    viewModel.getIsInit(),
                    viewModel.sorting.value
                )
            findNavController().navigate(directions)
        }
        svSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.setSearchKeyword(filter, query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                onQueryTextSubmit(newText)
                return false
            }
        })
        rgAlign.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                rbRecent.id -> viewLifecycleOwner.lifecycleScope.launch {
                    if (viewModel.sorting.value != RECENT) {
                        if (viewModel.teacherList.value.isNotEmpty()) {
                            viewModel.setSorting(RECENT, filter)
                        }
                    }
                }

                rbPopular.id -> viewLifecycleOwner.lifecycleScope.launch {
                    if (viewModel.sorting.value != POPULAR) {
                        if (viewModel.teacherList.value.isNotEmpty()) {
                            viewModel.setSorting(POPULAR, filter)
                        }
                    }
                }
            }
        }
    }

    fun initCollect() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch {
                viewModel.teacherList.collectLatest { teacherList ->
                    checkEmptyList(teacherList, EMPTY_TEACHER)
                    teacherAdapter.submitList(teacherList)
                }
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

    private fun loadS3Image(imageView: ImageView, key: String) = viewModel.loadS3Image(
        imageView, key
    )
}