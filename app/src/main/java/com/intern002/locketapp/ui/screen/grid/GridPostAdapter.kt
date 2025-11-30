package com.intern002.locketapp.ui.screen.grid

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.intern002.locketapp.data.remote.model.Post
import com.intern002.locketapp.databinding.ItemGridPostBinding

class GridPostAdapter(
    private var list: List<Post>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<GridPostAdapter.GridViewHolder>() {

    inner class GridViewHolder(val binding: ItemGridPostBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
        return GridViewHolder(
            ItemGridPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: GridViewHolder, position: Int) {
        val post = list[position]

        Glide.with(holder.itemView)
            .load(post.mediaUrl)
            .placeholder(android.R.color.darker_gray)
            .centerCrop()
            .into(holder.binding.imgGrid)

        holder.itemView.setOnClickListener {
            onItemClick(position)
        }
    }

    override fun getItemCount(): Int = list.size

    fun updateData(newList: List<Post>) {
        list = newList
        notifyDataSetChanged()
    }
}