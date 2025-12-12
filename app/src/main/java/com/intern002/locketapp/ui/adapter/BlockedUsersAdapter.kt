package com.intern002.locketapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.intern002.locketapp.data.model.Friend
import com.intern002.locketapp.databinding.ItemBlockedUserBinding

class BlockedUsersAdapter(
    private val onUnblockClicked: (Friend) -> Unit
) : ListAdapter<Friend, BlockedUsersAdapter.BlockedUserViewHolder>(BlockedUserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockedUserViewHolder {
        val binding = ItemBlockedUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BlockedUserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BlockedUserViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user)
    }

    inner class BlockedUserViewHolder(private val binding: ItemBlockedUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: Friend) {
            binding.tvUsername.text = user.username

            if (!user.avatarUrl.isNullOrEmpty()) {
                binding.ivAvatar.isVisible = true
                binding.tvAvatarInitial.isVisible = false
                Glide.with(itemView.context).load(user.avatarUrl).into(binding.ivAvatar)
            } else {
                binding.ivAvatar.isVisible = false
                binding.tvAvatarInitial.isVisible = true
                binding.tvAvatarInitial.text = user.username.firstOrNull()?.toString()?.uppercase() ?: ""
            }

            binding.btnUnblock.setOnClickListener {
                onUnblockClicked(user)
            }
        }
    }

    class BlockedUserDiffCallback : DiffUtil.ItemCallback<Friend>() {
        override fun areItemsTheSame(oldItem: Friend, newItem: Friend): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Friend, newItem: Friend): Boolean {
            return oldItem == newItem
        }
    }
}
