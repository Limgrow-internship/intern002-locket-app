package com.intern002.locketapp.data.repository

import com.intern002.locketapp.data.local.dao.PostDao
import com.intern002.locketapp.data.local.entity.toEntity
import com.intern002.locketapp.data.remote.api.PostApi
import com.intern002.locketapp.data.remote.model.Post
import com.intern002.locketapp.data.remote.model.post.CreatePostRequest
import io.ktor.client.call.body
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val postApi: PostApi,
    private val postDao: PostDao
) : PostRepository {

    override val posts: Flow<List<Post>> = postDao.getAllPosts()
        .map { entities -> entities.map { it.toPost() } }


    override suspend fun fetchPosts(page: Int, pageSize: Int): Result<Unit> {
        return try {
            val response = postApi.getPosts(page, pageSize)

            if (response.status.isSuccess()) {
                val posts = response.body<List<Post>>()

                val entities = posts.map { it.toEntity() }

                if (page == 1) {
                    postDao.refreshPosts(entities)
                } else {
                    postDao.insertPosts(entities)
                }
                Result.success(Unit)
            } else {
                Result.failure(Exception("Lỗi: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveLocalPost(post: Post) {
        postDao.insertOnePost(post.toEntity())
    }
    
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

    override suspend fun getPosts(page: Int, pageSize: Int): Result<List<Post>> {
        return try {
            val response = postApi.getPosts(page, pageSize)
            if (response.status.isSuccess()) {
                val posts = response.body<List<Post>>()
                Result.success(posts)
            } else {
                Result.failure(Exception("Lỗi lấy bài: ${response.status.value}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}