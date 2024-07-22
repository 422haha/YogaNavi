package com.ssafy.yoganavi.ui.core

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import com.ssafy.yoganavi.ui.loginUI.find.FindFragment
import com.ssafy.yoganavi.ui.loginUI.join.JoinFragment
import com.ssafy.yoganavi.ui.loginUI.login.LoginFragment
import com.ssafy.yoganavi.ui.utils.MANAGEMENT_NOTICE

typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

abstract class BaseFragment<VB : ViewBinding>(private val inflate: Inflate<VB>) : Fragment() {
    private var _binding: VB? = null
    protected val binding: VB get() = _binding!!
    private val activityViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = inflate.invoke(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    fun showSnackBar(msg: String) = Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
    fun setToolbar(
        isBottomNavigationVisible: Boolean,
        title: String,
        canGoBack: Boolean,
        menuItem: String? = null,
        menuListener: (() -> Unit)? = null
    ) {
        if (!(this is LoginFragment || this is FindFragment || this is JoinFragment)) {
            activityViewModel.setMainEvent(isBottomNavigationVisible, title, canGoBack, menuItem, menuListener)
        }
    }
}
