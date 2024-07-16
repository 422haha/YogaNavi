package com.ssafy.yoganavi.ui.homeUI.myPage.registerVideo

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.ssafy.yoganavi.R
import com.ssafy.yoganavi.databinding.CustomChapterViewBinding

class CustomChapterView : ConstraintLayout {
    constructor(context: Context) : super(context){
        initView()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context,attrs){
        initView()
    }
    private val binding : CustomChapterViewBinding by lazy{
        CustomChapterViewBinding.bind(
            LayoutInflater.from(context).inflate(R.layout.custom_chapter_view,this,false)
        )
    }
    private fun initView(){
        addView(binding.root)
    }
}