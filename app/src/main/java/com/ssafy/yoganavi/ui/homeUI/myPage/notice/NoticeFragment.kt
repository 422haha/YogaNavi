package com.ssafy.yoganavi.ui.homeUI.myPage.notice

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.ssafy.yoganavi.R
import com.ssafy.yoganavi.data.source.dto.notice.NoticeData
import com.ssafy.yoganavi.databinding.FragmentNoticeBinding
import com.ssafy.yoganavi.ui.core.BaseFragment
import com.ssafy.yoganavi.ui.homeUI.myPage.notice.notices.NoticeAdapter
import com.ssafy.yoganavi.ui.utils.MANAGEMENT_NOTICE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NoticeFragment : BaseFragment<FragmentNoticeBinding>(FragmentNoticeBinding::inflate) {

    private val viewModel: NoticeViewModel by viewModels()
    private val noticeAdapter by lazy { NoticeAdapter(::navigateToNoticeFragment, ::noticeDelete) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar(false, MANAGEMENT_NOTICE, true)

        binding.rvMyList.adapter = noticeAdapter
        initCollect()
        initListener()
        viewModel.getNoticeAll()
    }

    private fun initListener() {
        with(binding) {
            floatingActionButton.setOnClickListener {
                findNavController().navigate(R.id.action_noticeFragment_to_registerNoticeFragment)
            }
        }
    }

    private fun initCollect() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.noticeList.collectLatest {
                checkEmptyList(it)
                noticeAdapter.submitList(it)
            }
        }
    }

    private fun navigateToNoticeFragment(articleId: Int = -1) {
        val directions = NoticeFragmentDirections
            .actionNoticeFragmentToRegisterNoticeFragment(articleId)
        findNavController().navigate(directions)
    }

    private fun noticeDelete(noticeData: NoticeData) {
        viewModel.deleteNotice(noticeData.articleId)
    }

}