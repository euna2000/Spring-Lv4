package com.sparta.springblog.aop;

import java.util.concurrent.RejectedExecutionException;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.sparta.springblog.entity.Comment;
import com.sparta.springblog.entity.Post;
import com.sparta.springblog.entity.User;
import com.sparta.springblog.entity.UserRoleEnum;
import com.sparta.springblog.security.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "RoleCheckAop")
@Aspect
@Component
public class RoleCheckAop {

    // 게시글 수정 메서드에 대한 Pointcut 설정
    @Pointcut("execution(* com.sparta.springblog.service.PostService.updatePost(..))")
    private void updatePost() {}

    // 게시글 삭제 메서드에 대한 Pointcut 설정
    @Pointcut("execution(* com.sparta.springblog.service.PostService.deletePost(..))")
    private void deletePost() {}

    // 댓글 수정 메서드에 대한 Pointcut 설정
    @Pointcut("execution(* com.sparta.springblog.service.CommentService.updateComment(..))")
    private void updateComment() {}

    // 댓글 삭제 메서드에 대한 Pointcut 설정
    @Pointcut("execution(* com.sparta.springblog.service.CommentService.deleteComment(..))")
    private void deleteComment() {}

    // 게시글 수정/삭제에 대한 AOP 적용
    @Around("updatePost() || deletePost()")
    public Object executePostRoleCheck(ProceedingJoinPoint joinPoint) throws Throwable {
        // 첫번째 매개변수로 게시글 받아옴
        Post post = (Post)joinPoint.getArgs()[0];

        // 로그인 회원이 없는 경우, 수행시간 기록하지 않음
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal().getClass() == UserDetailsImpl.class) {
            // 로그인 회원 정보
            UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
            User loginUser = userDetails.getUser();

            // 게시글 작성자(post.user)와 요청자(user)가 같은지 또는 Admin인지 체크 (아니면 예외 발생)
            if (!(loginUser.getRole().equals(UserRoleEnum.ADMIN) || post.getUser().equals(loginUser))) {
                log.warn("[AOP] 작성자만 게시글을 수정/삭제 할 수 있습니다.");
                throw new RejectedExecutionException();
            }
        }

        // 핵심 기능 수행
        return joinPoint.proceed();
    }

    // 댓글 수정/삭제에 대한 AOP 적용
    @Around("updateComment() || deleteComment()")
    public Object executeCommentRoleCheck(ProceedingJoinPoint joinPoint) throws Throwable {
        // 첫번째 매개변수로 댓글 받아옴
        Comment comment = (Comment) joinPoint.getArgs()[0];

        // 로그인 회원이 없는 경우, 수행시간 기록하지 않음
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal().getClass() == UserDetailsImpl.class) {
            // 로그인 회원 정보
            UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
            User loginUser = userDetails.getUser();

            // 댓글 작성자(comment.user)와 요청자(user)가 같은지 또는 Admin인지 체크 (아니면 예외 발생)
            if (!(loginUser.getRole().equals(UserRoleEnum.ADMIN) || comment.getUser().equals(loginUser))) {
                log.warn("[AOP] 작성자만 댓글을 수정/삭제 할 수 있습니다.");
                throw new RejectedExecutionException();
            }
        }

        // 핵심 기능 수행
        return joinPoint.proceed();
    }
}
