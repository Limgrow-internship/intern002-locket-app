package com.intern002.locketapp.ui.adapter

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.intern002.locketapp.R
import com.intern002.locketapp.databinding.ItemChatConversationBinding
import com.intern002.locketapp.ui.screen.chat.Conversation
import com.intern002.locketapp.ui.screen.chat.MessageStatus
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

class ChatListAdapter(
    private val onConversationClicked: (Conversation) -> Unit
) : ListAdapter<Conversation, ChatListAdapter.ConversationViewHolder>(ConversationDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val binding = ItemChatConversationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ConversationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        val conversation = getItem(position)
        holder.bind(conversation)
        holder.itemView.setOnClickListener {
            onConversationClicked(conversation)
        }
    }

    private fun formatDisplayTimestamp(isoString: String): String {
        if (isoString.isBlank()) return ""
        return try {
            val pattern = if (isoString.contains(".")) "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" else "yyyy-MM-dd'T'HH:mm:ss'Z'"
            val parser = SimpleDateFormat(pattern, Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            val utcDate = parser.parse(isoString) ?: return ""

            val localDate = Date(utcDate.time + TimeUnit.HOURS.toMillis(7))

            val messageCal = Calendar.getInstance().apply { time = localDate }
            val today = Calendar.getInstance()

            val timeFormatter = SimpleDateFormat("h:mm a", Locale.US)

            if (messageCal.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                messageCal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                timeFormatter.format(localDate)
            } else {
                val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
                if (messageCal.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) &&
                    messageCal.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR)) {
                    "Yesterday"
                } else if (messageCal.get(Calendar.YEAR) == today.get(Calendar.YEAR)) {
                    SimpleDateFormat("MMM d", Locale.US).format(localDate)
                } else {
                    SimpleDateFormat("dd/MM/yy", Locale.US).format(localDate)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    inner class ConversationViewHolder(private val binding: ItemChatConversationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(conversation: Conversation) {
            binding.tvName.text = conversation.name
            binding.tvLastMessage.text = conversation.lastMessage
            binding.tvTimestamp.text = "· ${formatDisplayTimestamp(conversation.timestamp)}"

            if (conversation.avatarUrl.isNotEmpty()) {
                binding.ivAvatar.isVisible = true
                binding.tvAvatarInitial.isVisible = false
                Glide.with(itemView.context).load(conversation.avatarUrl).into(binding.ivAvatar)
            } else {
                binding.ivAvatar.isVisible = false
                binding.tvAvatarInitial.isVisible = true
                binding.tvAvatarInitial.text = conversation.name.firstOrNull()?.toString()?.uppercase() ?: ""
            }

            binding.tvName.typeface = Typeface.DEFAULT
            binding.tvLastMessage.typeface = Typeface.DEFAULT
            binding.tvLastMessage.setTextColor(ContextCompat.getColor(itemView.context, R.color.white_30per_opacity))
            binding.readStatusContainer.isVisible = false
            binding.ivReadStatusIcon.isVisible = false
            binding.ivReadStatusAvatar.isVisible = false
            binding.tvReadStatusInitial.isVisible = false


            if (!conversation.lastMessageFromMe) {
                if (conversation.status is MessageStatus.Unread) {
                    binding.tvName.typeface = Typeface.DEFAULT_BOLD
                    binding.tvLastMessage.typeface = Typeface.DEFAULT_BOLD
                    binding.tvLastMessage.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
                }
                return
            }

            binding.readStatusContainer.isVisible = true

            when (val status = conversation.status) {
                is MessageStatus.Failed -> {
                    binding.ivReadStatusIcon.isVisible = true
                    binding.ivReadStatusIcon.setImageResource(R.drawable.ic_not_read)
                }
                is MessageStatus.Sending -> {
                    binding.tvLastMessage.text = "Sending..."
                    binding.tvLastMessage.typeface = Typeface.defaultFromStyle(Typeface.ITALIC)
                    binding.readStatusContainer.isVisible = false
                }
                is MessageStatus.Sent -> {
                    binding.ivReadStatusIcon.isVisible = true
                    binding.ivReadStatusIcon.setImageResource(R.drawable.ic_sent_check) // Sent icon
                }
                is MessageStatus.Read -> {
                    if (!status.readerAvatarUrl.isNullOrEmpty()) {
                        binding.ivReadStatusAvatar.isVisible = true
                        binding.tvReadStatusInitial.isVisible = false
                        Glide.with(itemView.context).load(status.readerAvatarUrl).into(binding.ivReadStatusAvatar)
                    } else {
                        binding.ivReadStatusAvatar.isVisible = false
                        binding.tvReadStatusInitial.isVisible = true
                        binding.tvReadStatusInitial.text = status.readerName.firstOrNull()?.toString()?.uppercase() ?: ""
                    }
                }
                else -> {
                    binding.readStatusContainer.isVisible = false
                }
            }
        }
    }
}

class ConversationDiffCallback : DiffUtil.ItemCallback<Conversation>() {
    override fun areItemsTheSame(oldItem: Conversation, newItem: Conversation): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Conversation, newItem: Conversation): Boolean {
        return oldItem == newItem
    }
}
