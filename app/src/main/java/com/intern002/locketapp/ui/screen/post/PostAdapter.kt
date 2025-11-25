package com.intern002.locketapp.ui.screen.post

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.intern002.locketapp.R
import com.intern002.locketapp.data.remote.model.Post
import com.intern002.locketapp.databinding.ItemPostBinding
import com.intern002.locketapp.utils.TimeUtils

class PostAdapter(
    private var list: List<Post>
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    inner class PostViewHolder(val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder(
            ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = list[position]

        with(holder.binding) {
            tvUsername.text = post.userName
            tvTimeAgo.text = TimeUtils.getTimeAgo(post.createdAt)

            // Avatar
            Glide.with(root).load(post.userAvatarUrl)
                .placeholder(R.drawable.avt_sample)
                .into(imgAvatar)

            // Caption
            if (post.caption.isNullOrEmpty()) {
                tvCaption.isVisible = false
            } else {
                tvCaption.isVisible = true
                tvCaption.text = post.caption
            }

            // 2. XỬ LÝ MEDIA (Ảnh & Video)

            videoPostMedia.visibility = View.GONE
            imgPostMedia.visibility = View.VISIBLE
            videoPostMedia.stopPlayback()

            Glide.with(root)
                .load(post.mediaUrl)
                .placeholder(android.R.color.darker_gray)
                .into(imgPostMedia)

            // Sự kiện Click để phát Video
            if (post.mediaType == "video") {
                cardMedia.setOnClickListener {
                    imgPostMedia.visibility = View.GONE
                    videoPostMedia.visibility = View.VISIBLE

                    val uri = post.mediaUrl.toUri()
                    videoPostMedia.setVideoURI(uri)

                    videoPostMedia.setOnPreparedListener { mp ->
                        mp.isLooping = true // Lặp lại
                        mp.start()
                    }

                }
            } else {
                cardMedia.setOnClickListener(null)
            }
        }
    }

    override fun getItemCount(): Int = list.size


    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: List<Post>) {
        list = newList
        notifyDataSetChanged()
    }
}