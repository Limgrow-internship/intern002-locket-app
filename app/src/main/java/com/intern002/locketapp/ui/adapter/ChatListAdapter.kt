package com.intern002.locketapp.ui.adapter

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.intern002.locketapp.R
import com.intern002.locketapp.databinding.ItemChatConversationBinding
import com.intern002.locketapp.ui.screen.chat.Conversation
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class ChatListAdapter(
    private var conversations: List<Conversation>,
    private var currentUserAvatarUrl: String?,
    private val onItemClick: (Conversation) -> Unit
) : RecyclerView.Adapter<ChatListAdapter.ConversationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val binding = ItemChatConversationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ConversationViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        holder.bind(conversations[position], currentUserAvatarUrl)
    }

    override fun getItemCount(): Int = conversations.size

    fun updateData(newConversations: List<Conversation>, newAvatarUrl: String?) {
        conversations = newConversations
        currentUserAvatarUrl = newAvatarUrl
        notifyDataSetChanged()
    }

    class ConversationViewHolder(
        private val binding: ItemChatConversationBinding,
        private val onItemClick: (Conversation) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(conversation: Conversation, currentUserAvatarUrl: String?) {
            binding.tvName.text = conversation.name
            binding.tvLastMessage.text = conversation.lastMessage

            // Avatar logic
            if (conversation.avatarUrl.isNotEmpty()) {
                binding.ivAvatar.visibility = View.VISIBLE
                binding.tvAvatarInitial.visibility = View.GONE
                Glide.with(itemView.context)
                    .load(conversation.avatarUrl)
                    .into(binding.ivAvatar)
            } else {
                binding.ivAvatar.visibility = View.GONE
                binding.tvAvatarInitial.visibility = View.VISIBLE
                binding.tvAvatarInitial.text = conversation.name.firstOrNull()?.uppercase() ?: ""
            }

            // Timestamp formatting
            if (conversation.timestamp.isNotEmpty()) {
                try {
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                    inputFormat.timeZone = TimeZone.getTimeZone("UTC")
                    val date: Date? = inputFormat.parse(conversation.timestamp)

                    if (date != null) {
                        val outputFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
                        binding.tvTimestamp.text = "· ${outputFormat.format(date)}"
                    } else {
                        binding.tvTimestamp.text = ""
                    }
                } catch (e: Exception) {
                    binding.tvTimestamp.text = ""
                }
            } else {
                binding.tvTimestamp.text = ""
            }

            // Read status logic
            val isUnreadByCurrentUser = !conversation.isRead && !conversation.lastMessageFromMe

            // Reset all status views first
            binding.ivReadStatusIcon.visibility = View.GONE
            binding.ivReadStatusAvatar.visibility = View.GONE
            binding.tvName.setTypeface(null, Typeface.NORMAL)
            binding.tvLastMessage.setTypeface(null, Typeface.NORMAL)
            binding.tvLastMessage.setTextColor(itemView.context.getColor(R.color.grey_500))

            when {
                isUnreadByCurrentUser -> {
                    binding.tvName.setTypeface(null, Typeface.BOLD)
                    binding.tvLastMessage.setTypeface(null, Typeface.BOLD)
                    binding.tvLastMessage.setTextColor(itemView.context.getColor(android.R.color.white))
                    binding.ivReadStatusIcon.setImageResource(R.drawable.ic_not_read)
                    binding.ivReadStatusIcon.visibility = View.VISIBLE
                }
                conversation.lastMessageFromMe && conversation.isRead -> {
                    Glide.with(itemView.context)
                        .load(currentUserAvatarUrl)
                        .placeholder(R.drawable.avt_sample)
                        .error(R.drawable.avt_sample)
                        .into(binding.ivReadStatusAvatar)
                    binding.ivReadStatusAvatar.visibility = View.VISIBLE
                }
                conversation.lastMessageFromMe && !conversation.isRead -> {
                    binding.ivReadStatusIcon.setImageResource(R.drawable.ic_sent)
                    binding.ivReadStatusIcon.visibility = View.VISIBLE
                }
            }

            itemView.setOnClickListener {
                onItemClick(conversation)
            }
        }
    }
}