package com.ssafy.yoganavi.ui.core

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import com.ssafy.yoganavi.data.source.dto.home.EmptyData
import com.ssafy.yoganavi.ui.loginUI.find.FindFragment
import com.ssafy.yoganavi.ui.loginUI.join.JoinFragment
import com.ssafy.yoganavi.ui.loginUI.login.LoginFragment

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setEmptyView(EmptyData(false))
    }

    override fun onPause() {
        super.onPause()
        if (activity == null || requireActivity() !is MainActivity) return
        (requireActivity() as MainActivity).setBottomNavClickable(false)
    }

    override fun onResume() {
        super.onResume()
        if (activity == null || requireActivity() !is MainActivity) return
        (requireActivity() as MainActivity).setBottomNavClickable(true)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    fun showSnackBar(msg: String) = Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
    fun showSnackBar(@StringRes msgResId: Int) {
        val message = getString(msgResId)
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    fun setToolbar(
        isBottomNavigationVisible: Boolean,
        title: String,
        canGoBack: Boolean,
        menuItem: String? = null,
        menuListener: (() -> Unit)? = null,
        isEmptyView: Boolean = false
    ) {
        if (activity == null || requireActivity() !is MainActivity) return

        activityViewModel.setMainEvent(
            isBottomNavigationVisible = isBottomNavigationVisible,
            title = title,
            canGoBack = canGoBack,
            menuItem = menuItem,
            menuListener = menuListener
        )
        setEmptyView(EmptyData(isEmptyView))
    }

    fun setMenuItemAvailable(isAvailable: Boolean) {
        if (activity == null || requireActivity() !is MainActivity) return
        (requireActivity() as MainActivity).setMenuItemAvailable(isAvailable)
    }

    fun logout() {
        if (this is LoginFragment || this is FindFragment || this is JoinFragment) return

        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    fun hideKeyboard() {
        if (activity != null && requireActivity().currentFocus != null) {
            val inputManager: InputMethodManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

            inputManager.hideSoftInputFromWindow(
                requireActivity().currentFocus?.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    fun checkEmptyList(list: List<Any>, emptyString: String) {
        if (activity == null || requireActivity() !is MainActivity) return

        if (list.isEmpty()) setEmptyView(EmptyData(true, emptyString))
        else setEmptyView(EmptyData(false, emptyString))
    }

    fun setEmptyView(emptyData: EmptyData) =
        activityViewModel.setEmptyView(emptyData)
}
