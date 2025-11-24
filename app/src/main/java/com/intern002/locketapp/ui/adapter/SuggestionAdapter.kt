package com.intern002.locketapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.intern002.locketapp.R
import com.intern002.locketapp.data.model.FriendStatus
import com.intern002.locketapp.data.model.Suggestion

class SuggestionAdapter(private val suggestions: List<Suggestion>, private val onAddClick: (Suggestion) -> Unit) :
    RecyclerView.Adapter<SuggestionAdapter.SuggestionViewHolder>() {

    inner class SuggestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val avatar: ImageView = itemView.findViewById(R.id.iv_avatar)
        private val avatarInitial: TextView = itemView.findViewById(R.id.tv_avatar_initial)
        private val name: TextView = itemView.findViewById(R.id.tv_name)
        private val username: TextView = itemView.findViewById(R.id.tv_username)
        private val addButton: MaterialButton = itemView.findViewById(R.id.btn_add_friend)

        fun bind(suggestion: Suggestion) {
            name.text = suggestion.name
            username.text = "@${suggestion.username}"

            if (suggestion.avatarUrl != null) {
                avatar.visibility = View.VISIBLE
                avatarInitial.visibility = View.GONE
                // TODO: Load real image with Glide/Picasso
                avatar.setImageResource(R.drawable.avt_sample)
            } else {
                avatar.visibility = View.GONE
                avatarInitial.visibility = View.VISIBLE
                avatarInitial.text = suggestion.name.first().toString()
            }

            when (suggestion.status) {
                FriendStatus.NOT_FRIEND -> {
                    addButton.text = "Add"
                    addButton.setIconResource(R.drawable.ic_add_friend)
                    addButton.isEnabled = true
                    addButton.setOnClickListener { onAddClick(suggestion) }
                }
                FriendStatus.INVITED -> {
                    addButton.text = "Invited"
                    addButton.icon = null
                    addButton.isEnabled = false
                }
                FriendStatus.FRIEND -> {
                    addButton.visibility = View.GONE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_suggestion, parent, false)
        return SuggestionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SuggestionViewHolder, position: Int) {
        holder.bind(suggestions[position])
    }

    override fun getItemCount(): Int = suggestions.size
}
