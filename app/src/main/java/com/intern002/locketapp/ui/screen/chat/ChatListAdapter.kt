package com.intern002.locketapp.ui.screen.chat

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.intern002.locketapp.R

class ChatListAdapter(
    private val conversations: List<Conversation>,
    private val onItemClick: (Conversation) -> Unit // Added this listener
) : RecyclerView.Adapter<ChatListAdapter.ConversationViewHolder>() {

    inner class ConversationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val avatar: ImageView = itemView.findViewById(R.id.iv_avatar)
        private val name: TextView = itemView.findViewById(R.id.tv_name)
        private val lastMessage: TextView = itemView.findViewById(R.id.tv_last_message)
        private val statusIcon: ImageView = itemView.findViewById(R.id.iv_status_icon)

        fun bind(conversation: Conversation) {
            name.text = conversation.name

            val fullMessage = if (conversation.timestamp.isNotBlank()) {
                "${conversation.lastMessage} · ${conversation.timestamp}"
            } else {
                conversation.lastMessage
            }
            lastMessage.text = fullMessage

            // TODO: Load real avatar using Glide/Picasso

            if (conversation.lastMessageFromMe) {
                statusIcon.visibility = View.VISIBLE
                val statusRes = if (conversation.isRead) R.drawable.ic_read_receipt_empty else R.drawable.ic_not_received
                statusIcon.setImageResource(statusRes)
            } else {
                statusIcon.visibility = View.GONE
            }

            if (!conversation.isRead && !conversation.lastMessageFromMe) {
                name.setTypeface(null, Typeface.BOLD)
                lastMessage.setTypeface(null, Typeface.BOLD)
                lastMessage.setTextColor(itemView.context.getColor(android.R.color.white))
            } else {
                name.setTypeface(null, Typeface.NORMAL)
                lastMessage.setTypeface(null, Typeface.NORMAL)
                lastMessage.setTextColor(ContextCompat.getColor(itemView.context, R.color.grey_500))
            }

            itemView.setOnClickListener {
                onItemClick(conversation)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_conversation, parent, false)
        return ConversationViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        holder.bind(conversations[position])
    }

    override fun getItemCount(): Int = conversations.size
}
