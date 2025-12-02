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
import com.intern002.locketapp.data.remote.model.Reactor
import com.intern002.locketapp.databinding.ItemPostBinding
import com.intern002.locketapp.utils.TimeUtils

interface PostItemCallBack {
    fun onPostTypeChanged(isMine: Boolean)
    fun onShowReactions(reactors: List<Reactor>)
}

class PostAdapter(
    private var list: List<Post>,
    private val currentUserId: String,
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

        val isMine = post.authorId == currentUserId
        android.util.Log.d(
            "DEBUG_POST",
            "Item $position: User=${post.userName}, Time=${post.createdAt}, ID=${post.authorId}"
        )
        with(holder.binding) {
            tvUsername.text = if (isMine) "You" else post.userName
            tvTimeAgo.text = TimeUtils.getTimeAgo(post.createdAt)
            tvCaption.isVisible = !post.caption.isNullOrEmpty()
            tvCaption.text = post.caption

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

            if (post.mediaType == "video") {
                cardMedia.setOnClickListener {
                    imgPostMedia.visibility = View.GONE
                    videoPostMedia.visibility = View.VISIBLE

                    try {
                        val uri = post.mediaUrl.toUri()
                        videoPostMedia.setVideoURI(uri)

                        videoPostMedia.setOnPreparedListener { mp ->
                            mp.isLooping = true
                            mp.start()

                            videoPostMedia.setOnClickListener {
                                if (videoPostMedia.isPlaying) {
                                    videoPostMedia.pause()
                                } else {
                                    videoPostMedia.start()
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            if (isMine) {
                layoutMyActivity.isVisible = true

                val reactors = post.reactors ?: emptyList()

                layoutMyActivity.setOnClickListener {
                    callback.onShowReactions(reactors)
                }

                if (reactors.isEmpty()) {
                    tvActivityText.text = "No activity yet"
                } else {
                    tvActivityText.text = "Activity"
                    // TODO: Logic load 3 avatar chồng lên nhau ở đây (dùng Glide)
                }

            } else {
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

    fun getCurrentList(): List<Post> = list
}