package com.intern002.locketapp.ui.screen.edit

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.intern002.locketapp.R
import com.intern002.locketapp.data.remote.model.friend.FriendUserResponse
import com.intern002.locketapp.databinding.ItemFriendSelectBinding

class FriendsSelectAdapter(
    private var list: ArrayList<FriendItem>
) : RecyclerView.Adapter<FriendsSelectAdapter.FriendViewHolder>() {
    inner class FriendViewHolder(val binding: ItemFriendSelectBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        return FriendViewHolder(
            ItemFriendSelectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val item = list[position]

        holder.binding.tvName.text = item.name

        if (item.isAllButton) {
            holder.binding.imgAvatar.setImageResource(R.drawable.ic_user_group)
            holder.binding.imgAvatar.setPadding(10, 10, 10, 10)
        } else {
            holder.binding.imgAvatar.setPadding(0, 0, 0, 0)
            Glide.with(holder.itemView.context)
                .load(item.avatarUrl)
                .placeholder(R.drawable.avt_sample)
                .into(holder.binding.imgAvatar)
        }

        if (item.isSelected) {
            holder.binding.viewBorder.setBackgroundResource(R.drawable.bg_ring_blue)
            holder.binding.tvName.alpha = 1.0f
            holder.binding.viewBorder.alpha = 1.0f
        } else {
            holder.binding.viewBorder.setBackgroundResource(R.drawable.bg_ring_gray)
            holder.binding.tvName.alpha = 0.6f
            holder.binding.viewBorder.alpha = 0.5f
        }

        holder.itemView.setOnClickListener {
            handleSelection(position)
        }
    }

    private fun handleSelection(position: Int) {
        val clickedItem = list[position]

        if (clickedItem.isAllButton) {
            if (!clickedItem.isSelected) {
                // 1. Bỏ chọn tất cả bọn khác
                list.forEach { it.isSelected = false }
                // 2. Chọn nút All
                clickedItem.isSelected = true
                notifyDataSetChanged()
            }
        } else {
            clickedItem.isSelected = !clickedItem.isSelected

            if (clickedItem.isSelected) {
                list.firstOrNull { it.isAllButton }?.isSelected = false
            }

            val isAnySelected = list.any { !it.isAllButton && it.isSelected }
            if (!isAnySelected) {
                list.firstOrNull { it.isAllButton }?.isSelected = true
            }
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int = list.size

    fun getSelectedFriends(): List<FriendItem> {
        val allBtn = list.firstOrNull { it.isAllButton }
        if (allBtn?.isSelected == true) {
            return list.filter { !it.isAllButton }
        } else {
            return list.filter { it.isSelected && !it.isAllButton }
        }
    }

    fun updateData(newList: List<FriendUserResponse>) {
        val newItems = newList.map { user ->
            FriendItem(
                id = user.id,
                name = user.username,
                avatarUrl = user.avatarUrl
            )
        }

        val allItems = ArrayList<FriendItem>()
        allItems.add(FriendItem("0", "All", isSelected = true, isAllButton = true))
        allItems.addAll(newItems)

        this.list = allItems
        notifyDataSetChanged()
    }
}