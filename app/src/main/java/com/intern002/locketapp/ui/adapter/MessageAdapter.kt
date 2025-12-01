package com.intern002.locketapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.intern002.locketapp.data.model.Message
import com.intern002.locketapp.databinding.ItemMessageImageSentBinding
import com.intern002.locketapp.databinding.ItemMessageReceivedBinding
import com.intern002.locketapp.databinding.ItemMessageSentBinding

class MessageAdapter(private var messages: MutableList<Message>, private val currentUserId: String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_SENT_TEXT = 1
        private const val VIEW_TYPE_SENT_IMAGE = 2
        private const val VIEW_TYPE_RECEIVED_TEXT = 3
        private const val VIEW_TYPE_RECEIVED_IMAGE = 4
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
            is ReceivedTextViewHolder -> holder.bind(message)
            is ReceivedImageViewHolder -> holder.bind(message)
        }
    }

    override fun getItemCount(): Int = messages.size

    // FIX: Replaced notifyDataSetChanged() with a more efficient DiffUtil implementation
    fun setMessages(newMessages: List<Message>) {
        val diffCallback = MessageDiffCallback(this.messages, newMessages)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.messages.clear()
        this.messages.addAll(newMessages)
        diffResult.dispatchUpdatesTo(this)
    }

    // ViewHolder for sent text messages
    class SentTextViewHolder(private val binding: ItemMessageSentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            binding.tvMessageBody.text = message.content
        }
    }

    // ViewHolder for sent image messages
    class SentImageViewHolder(private val binding: ItemMessageImageSentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            Glide.with(itemView.context).load(message.imageUrl).into(binding.ivMessageImage)
        }
    }

    // ViewHolder for received text messages
    class ReceivedTextViewHolder(private val binding: ItemMessageReceivedBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            binding.tvMessageBody.text = message.content
            binding.tvMessageBody.visibility = android.view.View.VISIBLE
            binding.cvImageContainer.visibility = android.view.View.GONE
        }
    }

    // ViewHolder for received image messages
    class ReceivedImageViewHolder(private val binding: ItemMessageReceivedBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            Glide.with(itemView.context).load(message.imageUrl).into(binding.ivMessageImage)
            binding.tvMessageBody.visibility = android.view.View.GONE
            binding.cvImageContainer.visibility = android.view.View.VISIBLE
        }
    }
}

// DiffUtil Callback to calculate the difference between two lists efficiently
class MessageDiffCallback(private val oldList: List<Message>, private val newList: List<Message>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        // Use a unique identifier for each message, createdAt is a good candidate
        return oldList[oldItemPosition].createdAt == newList[newItemPosition].createdAt
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        // Check if the content of the message is the same
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
