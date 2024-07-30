package com.ssafy.yoganavi.ui.homeUI.lecture.lectureVideo

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.ssafy.yoganavi.databinding.FragmentLectureVideoBinding
import com.ssafy.yoganavi.ui.core.BaseFragment
import timber.log.Timber

class LectureVideoFragment : BaseFragment<FragmentLectureVideoBinding>(
    FragmentLectureVideoBinding::inflate
) {
    private val args by navArgs<LectureVideoFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d(args.uriList.joinToString())
    }
}
