package com.intern002.locketapp.ui.screen.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.PagerSnapHelper
import com.intern002.locketapp.data.remote.model.Post
import com.intern002.locketapp.databinding.FragmentPostListBinding
import java.util.UUID

class PostListFragment : Fragment() {

    private var _binding: FragmentPostListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. TẠO DỮ LIỆU GIẢ (Mock Data) để test UI
        val mockPosts = listOf(
            Post(
                id = UUID.randomUUID().toString(),
                authorId = "u1",
                userName = "Trâm",
                userAvatarUrl = "https://i.pravatar.cc/150?img=5",
                mediaUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9", // Ảnh
                mediaType = "photo",
                caption = "Làm trò con bò 😜",
                createdAt = "2025-11-25T02:00:00Z" // 1 tiếng trước
            ),
            Post(
                id = UUID.randomUUID().toString(),
                authorId = "u2",
                userName = "Minh",
                userAvatarUrl = "https://i.pravatar.cc/150?img=3",
                mediaUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4", // Video
                mediaType = "video",
                caption = "Đi xem phim neee",
                createdAt = "2025-11-25T03:00:00Z" // Vừa xong
            )
        )

        // 2. GẮN ADAPTER
        val adapter = PostAdapter(mockPosts)
        binding.recyclerViewPosts.adapter = adapter

        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.recyclerViewPosts)
    }

    override fun onPause() {
        super.onPause()

        // KHI RỜI KHỎI MÀN HÌNH FEED (Về Camera hoặc tắt app)
        // -> Reset cuộn về vị trí đầu tiên (0) ngay lập tức
        // Lần sau quay lại nó sẽ ở sẵn vị trí 0 rồi.
        binding.recyclerViewPosts.scrollToPosition(0)

        // (Optional) Nếu đang phát video thì nhớ pause video ở đây luôn
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}