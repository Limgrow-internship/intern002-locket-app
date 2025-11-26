package com.intern002.locketapp.data.datasource // Package chuẩn

import com.intern002.locketapp.data.remote.model.Post
import com.intern002.locketapp.data.remote.model.Reactor
import java.util.UUID

object MockData {
    val currentUserId = "me"

    val REACTOR_LIST_MAX = listOf(
        Reactor("r1", "Hùng", "https://i.pravatar.cc/150?img=15", "😍"),
        Reactor("r2", "Linh", "https://i.pravatar.cc/150?img=18", "😂"),
        Reactor("r3", "Tâm", "https://i.pravatar.cc/150?img=22", "👍"),
        Reactor("r4", "Khanh", "https://i.pravatar.cc/150?img=25", "😮")
    )

    val REACTOR_LIST_SINGLE = listOf(
        Reactor("r5", "Thảo", "https://i.pravatar.cc/150?img=30", "❤️")
    )

    val REACTOR_LIST_EMPTY = emptyList<Reactor>()
    val posts = listOf(
        Post(
            id = UUID.randomUUID().toString(),
            authorId = "u1",
            userName = "Minh",
            userAvatarUrl = "https://i.pravatar.cc/150?img=11",
            mediaUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            mediaType = "video",
            caption = "Hôm nay đi xem phim nha mọi người 🎬",
            createdAt = "2025-11-25T10:30:00Z" // Giả sử giờ hiện tại là 10:35
        ),
        // 2. Photo - Lan (Portrait)
        Post(
            id = UUID.randomUUID().toString(),
            authorId = "u2",
            userName = "Lan",
            userAvatarUrl = "https://i.pravatar.cc/150?img=5",
            mediaUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?q=80&w=1000&auto=format&fit=crop",
            mediaType = "photo",
            caption = "Lên đồ đi quẩy 💃",
            createdAt = "2025-11-25T09:15:00Z"
        ),
        // 3. Photo - Trâm (Mèo)
        Post(
            id = UUID.randomUUID().toString(),
            authorId = "me",
            userName = "Long",
            userAvatarUrl = "https://i.pravatar.cc/150?img=9",
            mediaUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?q=80&w=1000&auto=format&fit=crop",
            mediaType = "photo",
            caption = "Làm trò con bò 😜",
            createdAt = "2025-11-25T08:00:00Z",
            reactors = REACTOR_LIST_MAX
        ),
        // 4. Video - Hùng (Elephants Dream)
        Post(
            id = UUID.randomUUID().toString(),
            authorId = "u4",
            userName = "Hùng",
            userAvatarUrl = "https://i.pravatar.cc/150?img=12",
            mediaUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
            mediaType = "video",
            caption = "Test video cái nào 🎥",
            createdAt = "2025-11-24T20:00:00Z" // Hôm qua
        ),
        // 5. Photo - Vy (Không Caption - Để test ẩn hiện)
        Post(
            id = UUID.randomUUID().toString(),
            authorId = "u5",
            userName = "Vy",
            userAvatarUrl = "https://i.pravatar.cc/150?img=24",
            mediaUrl = "https://images.unsplash.com/photo-1526512340740-9217d0159da9?q=80&w=1000&auto=format&fit=crop",
            mediaType = "photo",
            caption = null, // <--- Không có caption
            createdAt = "2025-11-24T15:30:00Z"
        ),
        // 6. Photo - Long (Food)
        Post(
            id = UUID.randomUUID().toString(),
            authorId = "me",
            userName = "Long",
            userAvatarUrl = "https://i.pravatar.cc/150?img=33",
            mediaUrl = "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?q=80&w=1000&auto=format&fit=crop",
            mediaType = "photo",
            caption = "Pizza đêm khuya 🍕 đói chưa?",
            createdAt = "2025-11-23T23:00:00Z",
            reactors = REACTOR_LIST_EMPTY
        ),
        // 7. Video - For Bigger Blazes
        Post(
            id = UUID.randomUUID().toString(),
            authorId = "u7",
            userName = "An",
            userAvatarUrl = "https://i.pravatar.cc/150?img=52",
            mediaUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
            mediaType = "video",
            caption = "Chill tí nhạc 🎵",
            createdAt = "2025-11-22T10:00:00Z"
        ),
        // 8. Photo - Street
        Post(
            id = UUID.randomUUID().toString(),
            authorId = "me",
            userName = "Long",
            userAvatarUrl = "https://i.pravatar.cc/150?img=60",
            mediaUrl = "https://images.unsplash.com/photo-1514565131-fce0801e5785?q=80&w=1000&auto=format&fit=crop",
            mediaType = "photo",
            caption = "Phố lên đèn... 🌃",
            createdAt = "2025-11-20T18:00:00Z",
            reactors = REACTOR_LIST_SINGLE
        ),
        // 9. Photo - Dog
        Post(
            id = UUID.randomUUID().toString(),
            authorId = "u9",
            userName = "Chi",
            userAvatarUrl = "https://i.pravatar.cc/150?img=44",
            mediaUrl = "https://images.unsplash.com/photo-1543466835-00a7907e9de1?q=80&w=1000&auto=format&fit=crop",
            mediaType = "photo",
            caption = "Gâu gâu 🐶",
            createdAt = "2025-11-19T08:00:00Z"
        ),
        // 10. Video - Tears of Steel
        Post(
            id = UUID.randomUUID().toString(),
            authorId = "u10",
            userName = "Dũng",
            userAvatarUrl = "https://i.pravatar.cc/150?img=68",
            mediaUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
            mediaType = "video",
            caption = "Kỹ xảo đỉnh cao quá anh em ơi!",
            createdAt = "2025-11-15T12:00:00Z"
        )
    )
}