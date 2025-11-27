package com.intern002.locketapp.ui.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.intern002.locketapp.R
import com.intern002.locketapp.data.model.Friend
import com.intern002.locketapp.databinding.ItemFriendBinding

class FriendsAdapter : ListAdapter<Friend, FriendsAdapter.FriendViewHolder>(FriendDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val binding = ItemFriendBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FriendViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val friend = getItem(position)
        holder.bind(friend)
    }

    inner class FriendViewHolder(private val binding: ItemFriendBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(friend: Friend) {
            binding.tvUsername.text = friend.username
            binding.tvDiscriminator.text = "#${friend.discriminator}"

            if (!friend.avatarUrl.isNullOrEmpty()) {
                Glide.with(itemView.context)
                    .load(friend.avatarUrl)
                    .placeholder(R.drawable.avatar_placeholder)
                    .error(R.drawable.avatar_placeholder)
                    .into(binding.ivAvatar)
            } else {
                binding.ivAvatar.setImageDrawable(createInitialDrawable(itemView.context, friend.username))
            }
        }
    }


    private fun createInitialDrawable(context: Context, name: String): BitmapDrawable {
        val size = 150
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        backgroundPaint.color = ContextCompat.getColor(context, R.color.grey_dark)
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, backgroundPaint)

        // Draw text
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        textPaint.color = Color.WHITE
        textPaint.textSize = size / 2f // Font size
        textPaint.textAlign = Paint.Align.CENTER

        val initial = if (name.isNotEmpty()) name.first().uppercase() else ""
        
        val yPos = (canvas.height / 2f) - ((textPaint.descent() + textPaint.ascent()) / 2f)

        canvas.drawText(initial, canvas.width / 2f, yPos, textPaint)

        return BitmapDrawable(context.resources, bitmap)
    }

    class FriendDiffCallback : DiffUtil.ItemCallback<Friend>() {
        override fun areItemsTheSame(oldItem: Friend, newItem: Friend): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Friend, newItem: Friend): Boolean {
            return oldItem == newItem
        }
    }
}
