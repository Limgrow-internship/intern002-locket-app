package com.intern002.locketapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.intern002.locketapp.R
import com.intern002.locketapp.data.remote.model.Reactor
import com.intern002.locketapp.databinding.ItemReactionUserBinding

class ReactionAdapter(
    private val list: List<Reactor>
) : RecyclerView.Adapter<ReactionAdapter.ReactionViewHolder>() {

    inner class ReactionViewHolder(val binding: ItemReactionUserBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReactionViewHolder {
        return ReactionViewHolder(
            ItemReactionUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ReactionViewHolder, position: Int) {
        val reactor = list[position]
        with(holder.binding) {
            tvUsername.text = reactor.userName
            tvEmoji.text = reactor.reactionEmoji

            Glide.with(root)
                .load(reactor.avatarUrl)
                .placeholder(R.drawable.avt_sample)
                .into(imgAvatar)
        }
    }

    override fun getItemCount(): Int = list.size
}