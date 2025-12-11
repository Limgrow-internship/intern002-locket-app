package com.intern002.locketapp.ui.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.intern002.locketapp.R
import com.intern002.locketapp.data.model.Friend
import com.intern002.locketapp.databinding.ItemFriendBinding
import com.intern002.locketapp.ui.viewmodel.friends.FriendshipStatus

class FriendsAdapter : ListAdapter<Friend, FriendsAdapter.FriendViewHolder>(FriendDiffCallback()) {

    var onItemClickListener: ((Friend) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val binding = ItemFriendBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FriendViewHolder(binding, onItemClickListener)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val friend = getItem(position)
        holder.bind(friend)
    }

    class FriendViewHolder(
        private val binding: ItemFriendBinding,
        private val onItemClickListener: ((Friend) -> Unit)?
    ) : RecyclerView.ViewHolder(binding.root) {

        private var currentFriend: Friend? = null

        init {
            itemView.setOnClickListener {
                currentFriend?.let {
                    onItemClickListener?.invoke(it)
                }
            }
        }

        fun bind(friend: Friend) {
            currentFriend = friend
            binding.tvUsername.text = friend.username
            binding.tvDiscriminator.text = "#${friend.discriminator}"

            if (!friend.avatarUrl.isNullOrEmpty()) {
                Glide.with(itemView.context)
                    .load(friend.avatarUrl)
                    .placeholder(createInitialDrawable(itemView.context, friend.username))
                    .error(createInitialDrawable(itemView.context, friend.username))
                    .into(binding.ivAvatar)
            } else {
                binding.ivAvatar.setImageDrawable(createInitialDrawable(itemView.context, friend.username))
            }

            if (friend.isUpdating) {
                binding.pbItemLoading.visibility = View.VISIBLE
                binding.ivStatus.visibility = View.GONE
            } else {
                binding.pbItemLoading.visibility = View.GONE
                val statusIcon = when (friend.status) {
                    FriendshipStatus.FRIEND -> R.drawable.ic_friend
                    FriendshipStatus.NOT_FRIEND -> R.drawable.ic_add_friend
                    FriendshipStatus.PENDING_INCOMING, FriendshipStatus.PENDING_OUTGOING -> R.drawable.ic_invited
                    FriendshipStatus.BLOCKED -> R.drawable.ic_block_friend
                    FriendshipStatus.SELF -> 0
                }

                if (statusIcon != 0) {
                    binding.ivStatus.setImageResource(statusIcon)
                    binding.ivStatus.visibility = View.VISIBLE
                } else {
                    binding.ivStatus.visibility = View.INVISIBLE
                }
            }
        }

        private fun createInitialDrawable(context: Context, name: String): BitmapDrawable {
            val size = 150
            val bitmap = createBitmap(size, size)
            val canvas = Canvas(bitmap)

            val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = ContextCompat.getColor(context, R.color.grey_dark)
            }
            canvas.drawCircle(size / 2f, size / 2f, size / 2f, backgroundPaint)

            val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.WHITE
                textSize = size / 2f
                textAlign = Paint.Align.CENTER
            }

            val initial = if (name.isNotEmpty()) name.first().uppercase() else ""
            val yPos = (canvas.height / 2f) - ((textPaint.descent() + textPaint.ascent()) / 2f)
            canvas.drawText(initial, canvas.width / 2f, yPos, textPaint)

            return bitmap.toDrawable(context.resources) as BitmapDrawable
        }
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
