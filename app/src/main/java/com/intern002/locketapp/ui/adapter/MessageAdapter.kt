package com.intern002.locketapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.intern002.locketapp.R
import com.intern002.locketapp.data.model.Message

class MessageAdapter(private val messages: List<Message>, private val currentUserId: String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_SENT = 1
        private const val VIEW_TYPE_RECEIVED = 2
        private const val VIEW_TYPE_IMAGE_RECEIVED = 3 // New view type for received images
    }

    // ViewHolder for sent messages (text only)
    inner class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageBody: TextView = itemView.findViewById(R.id.tv_message_body)
        fun bind(message: Message) {
            messageBody.text = message.content
        }
    }

    // ViewHolder for received text messages
    inner class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageBody: TextView = itemView.findViewById(R.id.tv_message_body)
        fun bind(message: Message) {
            messageBody.text = message.content
        }
    }

    // ViewHolder for received image messages
    inner class ReceivedImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageImage: ImageView = itemView.findViewById(R.id.iv_message_image)
        fun bind(message: Message) {
            if (message.imageUrl == "img_food_sample") { // Check for the specific drawable name
                messageImage.setImageResource(R.drawable.img_food_sample)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return if (message.senderId == currentUserId) {
            VIEW_TYPE_SENT
        } else {
            if (message.imageUrl != null) {
                VIEW_TYPE_IMAGE_RECEIVED
            } else {
                VIEW_TYPE_RECEIVED
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_SENT -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_sent, parent, false)
                SentMessageViewHolder(view)
            }
            VIEW_TYPE_IMAGE_RECEIVED -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_received, parent, false)
                ReceivedImageViewHolder(view)
            }
            else -> { // VIEW_TYPE_RECEIVED
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_received, parent, false)
                ReceivedMessageViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        when (holder) {
            is SentMessageViewHolder -> holder.bind(message)
            is ReceivedMessageViewHolder -> {
                holder.itemView.findViewById<CardView>(R.id.cv_image_container).visibility = View.GONE
                holder.itemView.findViewById<TextView>(R.id.tv_message_body).visibility = View.VISIBLE
                holder.bind(message)
            }
            is ReceivedImageViewHolder -> {
                holder.itemView.findViewById<CardView>(R.id.cv_image_container).visibility = View.VISIBLE
                holder.itemView.findViewById<TextView>(R.id.tv_message_body).visibility = View.GONE
                holder.bind(message)
            }
        }
    }

    override fun getItemCount(): Int = messages.size
}
