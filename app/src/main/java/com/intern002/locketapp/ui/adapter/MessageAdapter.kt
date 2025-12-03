package com.intern002.locketapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.intern002.locketapp.R
import com.intern002.locketapp.data.model.Message
import com.intern002.locketapp.databinding.ItemMessageImageSentBinding
import com.intern002.locketapp.databinding.ItemMessageReceivedBinding
import com.intern002.locketapp.databinding.ItemMessageSentBinding

class MessageAdapter(
    private var messages: MutableList<Message>,
    private var currentUserId: String,
    private val recipientAvatarUrl: String?,
    private val recipientName: String?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_SENT_TEXT = 1
        private const val VIEW_TYPE_SENT_IMAGE = 2
        private const val VIEW_TYPE_RECEIVED_TEXT = 3
        private const val VIEW_TYPE_RECEIVED_IMAGE = 4
    }

    fun setCurrentUserId(newUserId: String) {
        if (currentUserId != newUserId) {
            currentUserId = newUserId
            notifyDataSetChanged()
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return if (message.senderId == currentUserId) {
            if (message.messageType == "image") VIEW_TYPE_SENT_IMAGE else VIEW_TYPE_SENT_TEXT
        } else {
            if (message.messageType == "image") VIEW_TYPE_RECEIVED_IMAGE else VIEW_TYPE_RECEIVED_TEXT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_SENT_TEXT -> {
                val binding = ItemMessageSentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                SentTextViewHolder(binding)
            }
            VIEW_TYPE_SENT_IMAGE -> {
                val binding = ItemMessageImageSentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                SentImageViewHolder(binding)
            }
            VIEW_TYPE_RECEIVED_TEXT -> {
                val binding = ItemMessageReceivedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ReceivedTextViewHolder(binding)
            }
            VIEW_TYPE_RECEIVED_IMAGE -> {
                val binding = ItemMessageReceivedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ReceivedImageViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        when (holder) {
            is SentTextViewHolder -> holder.bind(message)
            is SentImageViewHolder -> holder.bind(message)
            is ReceivedTextViewHolder -> holder.bind(message, recipientAvatarUrl, recipientName)
            is ReceivedImageViewHolder -> holder.bind(message, recipientAvatarUrl, recipientName)
        }
    }

    override fun getItemCount(): Int = messages.size

    fun setMessages(newMessages: List<Message>) {
        val diffCallback = MessageDiffCallback(this.messages, newMessages)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.messages.clear()
        this.messages.addAll(newMessages)
        diffResult.dispatchUpdatesTo(this)
    }

    class SentTextViewHolder(private val binding: ItemMessageSentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            binding.tvMessageBody.text = message.content
        }
    }

    class SentImageViewHolder(private val binding: ItemMessageImageSentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            Glide.with(itemView.context).load(message.imageUrl).into(binding.ivMessageImage)
        }
    }

    class ReceivedTextViewHolder(private val binding: ItemMessageReceivedBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message, avatarUrl: String?, name: String?) {
            binding.tvMessageBody.text = message.content
            binding.tvMessageBody.isVisible = true
            binding.cvImageContainer.isVisible = false
            updateAvatar(avatarUrl, name)
        }

        private fun updateAvatar(avatarUrl: String?, name: String?) {
            if (!avatarUrl.isNullOrEmpty()) {
                binding.ivAvatar.isVisible = true
                binding.tvAvatarLetter.isVisible = false
                Glide.with(itemView.context).load(avatarUrl).into(binding.ivAvatar)
            } else {
                binding.ivAvatar.isVisible = false
                binding.tvAvatarLetter.isVisible = true
                binding.tvAvatarLetter.text = name?.firstOrNull()?.toString() ?: ""
            }
        }
    }

    class ReceivedImageViewHolder(private val binding: ItemMessageReceivedBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message, avatarUrl: String?, name: String?) {
            Glide.with(itemView.context).load(message.imageUrl).into(binding.ivMessageImage)
            binding.tvMessageBody.isVisible = false
            binding.cvImageContainer.isVisible = true
            updateAvatar(avatarUrl, name)
        }

        private fun updateAvatar(avatarUrl: String?, name: String?) {
            if (!avatarUrl.isNullOrEmpty()) {
                binding.ivAvatar.isVisible = true
                binding.tvAvatarLetter.isVisible = false
                Glide.with(itemView.context).load(avatarUrl).into(binding.ivAvatar)
            } else {
                binding.ivAvatar.isVisible = false
                binding.tvAvatarLetter.isVisible = true
                binding.tvAvatarLetter.text = name?.firstOrNull()?.toString() ?: ""
            }
        }
    }
}

class MessageDiffCallback(private val oldList: List<Message>, private val newList: List<Message>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].createdAt == newList[newItemPosition].createdAt
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
