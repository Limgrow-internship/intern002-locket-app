package com.intern002.locketapp.data.repository

import com.intern002.locketapp.data.remote.api.ReactRequestDto
import com.intern002.locketapp.data.remote.api.ReactionApi
import com.intern002.locketapp.data.remote.model.reaction.ReactionTypeResponse
import io.ktor.client.call.body
import io.ktor.http.isSuccess
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReactionRepositoryImpl @Inject constructor(
    private val reactionApi: ReactionApi
) : ReactionRepository {

    private var cachedTypes: List<ReactionTypeResponse> = emptyList()

    override suspend fun getReactionTypes(): Result<List<ReactionTypeResponse>> {
        if (cachedTypes.isNotEmpty()) return Result.success(cachedTypes)

        return try {
            val response = reactionApi.getReactionTypes()
            if (response.status.isSuccess()) {
                val list = response.body<List<ReactionTypeResponse>>()
                cachedTypes = list
                Result.success(list)
            } else {
                Result.failure(Exception("Lỗi: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCachedReactionTypes(): List<ReactionTypeResponse> = cachedTypes

    override suspend fun reactToPost(postId: String, reactionTypeId: Int): Result<Unit> {
        return try {
            val request = ReactRequestDto(postId, reactionTypeId)
            val response = reactionApi.reactToPost(request)
            if (response.status.isSuccess()) Result.success(Unit)
            else Result.failure(Exception("Lỗi react"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}