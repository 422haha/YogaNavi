package com.ssafy.yoganavi.ui.homeUI.myPage.notice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssafy.yoganavi.R
import com.ssafy.yoganavi.databinding.FragmentNoticeBinding
import com.ssafy.yoganavi.ui.core.BaseFragment

class NoticeFragment : BaseFragment<FragmentNoticeBinding>(FragmentNoticeBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity()
        initListener()
        initRecyclerView()
    }

    private fun initListener() {
        with(binding) {
            floatingActionButton.setOnClickListener {
                findNavController().navigate(R.id.action_noticeFragment_to_registerNoticeFragment)
            }
        }
    }
    private fun initRecyclerView(){
        val itemList = java.util.ArrayList<NoticeItem>()
        itemList.add(NoticeItem("https://cdn.inflearn.com/public/files/posts/657d0bce-45a2-4977-9520-4490639c95b6/image.png","요구르드","2022-02-03","늦지마세요~"))
        itemList.add(NoticeItem("https://cdn.inflearn.com/public/files/posts/657d0bce-45a2-4977-9520-4490639c95b6/image.png","요구르드","2022-02-03","늦지마세요~"))
        itemList.add(NoticeItem("https://cdn.inflearn.com/public/files/posts/657d0bce-45a2-4977-9520-4490639c95b6/image.png","요구르드","2022-02-03","늦지마세요~"))
        itemList.add(NoticeItem("https://cdn.inflearn.com/public/files/posts/657d0bce-45a2-4977-9520-4490639c95b6/image.png","요구르드","2022-02-03","늦지마세요~"))
        val noticeAdapter = NoticeAdapter(itemList)
        noticeAdapter.notifyDataSetChanged()

        binding.rvMyList.adapter = noticeAdapter
        binding.rvMyList.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
    }
}

class NoticeAdapter(val itemList:ArrayList<NoticeItem>) : RecyclerView.Adapter<NoticeAdapter.NoticeViewHolder>(){
    override fun onCreateViewHolder( parent: ViewGroup, viewType: Int): NoticeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_notice,parent,false)
        return NoticeViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        holder.bindItems(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.count()
    }
    inner class NoticeViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        fun bindItems(item:NoticeItem){
            val ivProfile = itemView.findViewById<ImageView>(R.id.iv_profile)
            val tvTeacherNickname = itemView.findViewById<TextView>(R.id.tv_teacher_nickname)
            val tvDate = itemView.findViewById<TextView>(R.id.tv_date)
            val tvEditBtn = itemView.findViewById<TextView>(R.id.tv_editBtn)
            val tvDeleteBtn = itemView.findViewById<TextView>(R.id.tv_deleteBtn)
            val tvContent = itemView.findViewById<TextView>(R.id.tv_content)

            Glide.with(itemView.context)
                .load(item.ivProfile)
                .circleCrop()
                .into(ivProfile)
            tvTeacherNickname.text = item.tvTeacherNickname
            tvDate.text = item.tvDate
            tvContent.text = item.tvContent
            tvEditBtn.setOnClickListener {
                findNavController(itemView).navigate(R.id.action_noticeFragment_to_registerNoticeFragment)
            }
            tvDeleteBtn.setOnClickListener {
                // TODO: delete구현
            }
        }
    }
}

data class NoticeItem(
    val ivProfile: String,
    val tvTeacherNickname: String,
    val tvDate: String,
    val tvContent: String
)