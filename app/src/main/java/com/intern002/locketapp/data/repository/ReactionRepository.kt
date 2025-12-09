package com.intern002.locketapp.data.repository

import com.intern002.locketapp.data.remote.model.reaction.ReactionTypeResponse

interface ReactionRepository {
    suspend fun getReactionTypes(): Result<List<ReactionTypeResponse>>
    suspend fun reactToPost(postId: String, reactionTypeId: Int): Result<Unit>

    fun getCachedReactionTypes(): List<ReactionTypeResponse>
}