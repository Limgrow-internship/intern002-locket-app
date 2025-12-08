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
        return try {
            val pattern = if (isoString.contains(".")) "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" else "yyyy-MM-dd'T'HH:mm:ss'Z'"
            val parser = SimpleDateFormat(pattern, Locale.US)
            parser.timeZone = TimeZone.getTimeZone("UTC")
            val date = parser.parse(isoString) ?: return ""

            val messageCal = Calendar.getInstance().apply { time = date }
            val today = Calendar.getInstance()

            val timeFormatter = SimpleDateFormat("h:mm a", Locale.getDefault())

            if (messageCal.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                messageCal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                // Today
                timeFormatter.format(date)
            } else {
                today.add(Calendar.DAY_OF_YEAR, -1)
                if (messageCal.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                    messageCal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                    // Yesterday
                    "Yesterday"
                } else if (messageCal.get(Calendar.YEAR) == today.get(Calendar.YEAR)) {
                    // This year
                    SimpleDateFormat("MMM d", Locale.getDefault()).format(date)
                } else {
                    // Previous years
                    SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(date)
                }
            }
        } catch (e: Exception) {
            ""
        }
    }

    inner class ConversationViewHolder(private val binding: ItemChatConversationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(conversation: Conversation) {
            // 1. Set basic info
            binding.tvName.text = conversation.name
            binding.tvLastMessage.text = conversation.lastMessage
            binding.tvTimestamp.text = "· ${formatDisplayTimestamp(conversation.timestamp)}"

            // 2. Set partner avatar
            if (conversation.avatarUrl.isNotEmpty()) {
                binding.ivAvatar.isVisible = true
                binding.tvAvatarInitial.isVisible = false
                Glide.with(itemView.context).load(conversation.avatarUrl).into(binding.ivAvatar)
            } else {
                binding.ivAvatar.isVisible = false
                binding.tvAvatarInitial.isVisible = true
                binding.tvAvatarInitial.text = conversation.name.firstOrNull()?.toString()?.uppercase() ?: ""
            }

            // 3. Reset styles to default before applying new ones
            binding.tvName.typeface = Typeface.DEFAULT
            binding.tvLastMessage.typeface = Typeface.DEFAULT
            binding.tvLastMessage.setTextColor(ContextCompat.getColor(itemView.context, R.color.white_30per_opacity))
            binding.readStatusContainer.isVisible = false
            binding.ivReadStatusIcon.isVisible = false
            binding.ivReadStatusAvatar.isVisible = false
            binding.tvReadStatusInitial.isVisible = false

            // 4. Apply styles based on who sent the last message and its status

            // --- CASE 1: I AM THE RECEIVER of the last message ---
            if (!conversation.lastMessageFromMe) {
                if (conversation.status is MessageStatus.Unread) {
                    // Make text bold if I haven't read it yet
                    binding.tvName.typeface = Typeface.DEFAULT_BOLD
                    binding.tvLastMessage.typeface = Typeface.DEFAULT_BOLD
                    binding.tvLastMessage.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
                }
                // As requested, no status icon is shown for received messages, so we are done.
                return
            }

            // --- CASE 2: I AM THE SENDER of the last message ---
            binding.readStatusContainer.isVisible = true // The status container is generally visible if I'm the sender

            when (val status = conversation.status) {
                is MessageStatus.Failed -> {
                    binding.ivReadStatusIcon.isVisible = true
                    binding.ivReadStatusIcon.setImageResource(R.drawable.ic_not_read)
                }
                is MessageStatus.Sending -> {
                    binding.tvLastMessage.text = "Sending..."
                    binding.tvLastMessage.typeface = Typeface.defaultFromStyle(Typeface.ITALIC)
                    binding.readStatusContainer.isVisible = false // Hide status icon while sending
                }
                is MessageStatus.Sent -> {
                    binding.ivReadStatusIcon.isVisible = true
                    binding.ivReadStatusIcon.setImageResource(R.drawable.ic_sent_check) // Sent icon
                }
                is MessageStatus.Read -> {
                    // Show recipient's avatar or initial
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
                    // For any other case (like MessageStatus.None), just hide the container
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
