package com.intern002.locketapp.data.repository

import com.intern002.locketapp.data.remote.model.post.CreatePostRequest

interface PostRepository {
    suspend fun createPost(request: CreatePostRequest): Result<Unit>
}