package com.ssafy.yoganavi.ui.homeUI.myPage.registerLive

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.ssafy.yoganavi.databinding.FragmentRegisterLiveBinding
import com.ssafy.yoganavi.ui.core.BaseFragment
import com.ssafy.yoganavi.ui.utils.MODIFY_LIVE
import com.ssafy.yoganavi.ui.utils.REGISTER
import com.ssafy.yoganavi.ui.utils.REGISTER_LIVE
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterLiveFragment :
    BaseFragment<FragmentRegisterLiveBinding>(FragmentRegisterLiveBinding::inflate) {

    private val args: RegisterLiveFragmentArgs by navArgs()

    private val viewModel: RegisterLiveViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(args.liveId != -1) {
            viewModel.getLive(args.liveId)
        }

        setToolbar(isBottomNavigationVisible = false,
            title = if(args.liveId == -1) REGISTER_LIVE else MODIFY_LIVE,
            canGoBack = true,
            menuItem = REGISTER,
            menuListener = { viewModel.createLive() })
    }
}
