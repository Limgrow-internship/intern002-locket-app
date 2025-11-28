package com.intern002.locketapp.data.repository

import com.intern002.locketapp.data.remote.api.PostApi
import com.intern002.locketapp.data.remote.model.post.CreatePostRequest
import io.ktor.http.isSuccess
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val postApi: PostApi
) : PostRepository {

    override suspend fun createPost(request: CreatePostRequest): Result<Unit> {
        return try {
            val response = postApi.createPost(request)
            if (response.status.isSuccess()) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Lỗi Server: ${response.status.value}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}