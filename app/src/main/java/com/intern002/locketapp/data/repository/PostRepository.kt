package com.intern002.locketapp.data.repository

import com.intern002.locketapp.data.remote.model.Post
import com.intern002.locketapp.data.remote.model.post.CreatePostRequest
import kotlinx.coroutines.flow.Flow

interface PostRepository {

    val posts: Flow<List<Post>>
    suspend fun createPost(request: CreatePostRequest): Result<Unit>
    suspend fun getPosts(page: Int, pageSize: Int): Result<List<Post>>
    suspend fun fetchPosts(page: Int, pageSize: Int): Result<Unit>

    suspend fun saveLocalPost(post: Post)
}