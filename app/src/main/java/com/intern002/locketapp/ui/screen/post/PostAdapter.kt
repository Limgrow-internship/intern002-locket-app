package com.intern002.locketapp.ui.screen.post

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.intern002.locketapp.R
import com.intern002.locketapp.data.remote.model.Post
import com.intern002.locketapp.data.remote.model.Reactor
import com.intern002.locketapp.databinding.ItemPostBinding
import com.intern002.locketapp.utils.TimeUtils


interface PostItemCallBack {
    fun onPostTypeChanged(isMine: Boolean)
    fun onShowReactions(reactors: List<Reactor>)
}

class PostAdapter(
    private var list: List<Post>,
    private val currentUserId: String = "me", // ID của mình
    private val callback: PostItemCallBack
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    inner class PostViewHolder(val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder(
            ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = list[position]
        val currentUserId = this.currentUserId

        // Gọi callback để Fragment cha ẩn/hiện thanh Reply Bar
        val isMine = post.authorId == currentUserId
        callback.onPostTypeChanged(isMine) // Báo hiệu lên PostListFragment

        with(holder.binding) {
            tvUsername.text = if (isMine) "You" else post.userName
            tvTimeAgo.text = TimeUtils.getTimeAgo(post.createdAt)
            tvCaption.isVisible = !post.caption.isNullOrEmpty()
            tvCaption.text = post.caption

            // Avatar
            Glide.with(root).load(post.userAvatarUrl)
                .placeholder(R.drawable.avt_sample)
                .into(imgAvatar)

            videoPostMedia.stopPlayback()
            videoPostMedia.visibility = View.GONE
            imgPostMedia.visibility = View.VISIBLE
            cardMedia.setOnClickListener(null)

            Glide.with(root)
                .load(post.mediaUrl)
                .placeholder(android.R.color.darker_gray)
                .into(imgPostMedia)

            // --- 3. LOGIC PHÁT VIDEO ---
            if (post.mediaType == "video") {
                cardMedia.setOnClickListener {
                    imgPostMedia.visibility = View.GONE
                    videoPostMedia.visibility = View.VISIBLE

                    val uri = post.mediaUrl.toUri()
                    videoPostMedia.setVideoURI(uri)

                    videoPostMedia.setOnPreparedListener { mp ->
                        mp.isLooping = true
                        mp.start()

                        videoPostMedia.setOnClickListener {
                            if (videoPostMedia.isPlaying) {
                                videoPostMedia.pause()
                                Toast.makeText(it.context, "Video Paused", Toast.LENGTH_SHORT)
                                    .show()
                            } else {
                                videoPostMedia.start()
                                Toast.makeText(it.context, "Video Resumed", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                }
            }
            if (isMine) {
                tvUsername.text = "You"
                tvUsername.alpha = 1.0f

                layoutMyActivity.isVisible = true
                val reactors = post.reactors

                layoutMyActivity.setOnClickListener {
                    callback.onShowReactions(post.reactors)
                }
                if (reactors.isEmpty()) {
                    holder.binding.tvActivityText.text = "No activity yet"
                    holder.binding.imgActivityIndicator.setImageResource(R.drawable.ic_smile)
                } else {
                    holder.binding.tvActivityText.text = "Activity"
                    holder.binding.imgActivityIndicator.setImageResource(R.drawable.ic_smile)

                    // Logic load Avatar chồng lên nhau (Chỉ load 3 người đầu tiên)
                    // Dùng Glide và set Visibility cho từng img_reactor_X
                }
            } else {
                tvUsername.text = post.userName
                layoutMyActivity.isVisible = false
            }
        }
    }

    fun updateVisibleItemType(post: Post) {
        val isMine = post.authorId == currentUserId
        callback.onPostTypeChanged(isMine)
    }

    override fun getItemCount(): Int = list.size


    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: List<Post>) {
        list = newList
        notifyDataSetChanged()
    }
}