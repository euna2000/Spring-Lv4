// 필요한 import 구문 생략

import com.sparta.springblog.dto.CommentRequestDto;
import com.sparta.springblog.dto.CommentResponseDto;
import com.sparta.springblog.entity.Comment;
import com.sparta.springblog.entity.UserRoleEnum;
import com.sparta.springblog.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final com.thesun4sky.springblog.service.PostService postService;
    private final CommentRepository commentRepository;

    public CommentResponseDto createComment(CommentRequestDto requestDto, User user) {
        Post post = postService.findPost(requestDto.getPostId());
        Comment comment = new Comment(requestDto.getBody());
        comment.setUser(user);
        comment.setPost(post);

        var savedComment = commentRepository.save(comment);

        return new CommentResponseDto(savedComment);
    }

    public void deleteComment(Long id, User user) {
        Comment comment = commentRepository.findById(id).orElseThrow();

        // 요청자가 운영자 이거나 댓글 작성자(post.user) 와 요청자(user) 가 같은지 체크
        if (!UserRoleEnum.ADMIN.equals(user.getRole()) && !comment.getUser().equals(user)) {
            throw new AccessDeniedException("You don't have permission to delete this comment.");
        }

        commentRepository.delete(comment);
    }

    @Transactional
    public CommentResponseDto updateComment(Long id, CommentRequestDto requestDto, User user) {
        Comment comment = commentRepository.findById(id).orElseThrow();

        // 요청자가 운영자 이거나 댓글 작성자(post.user) 와 요청자(user) 가 같은지 체크
        if (!UserRoleEnum.ADMIN.equals(user.getRole()) && !comment.getUser().equals(user)) {
            throw new AccessDeniedException("You don't have permission to update this comment.");
        }

        comment.setBody(requestDto.getBody());

        return new CommentResponseDto(comment);
    }
}