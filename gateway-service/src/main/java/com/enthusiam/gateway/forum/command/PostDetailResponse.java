package com.enthusiam.gateway.forum.command;

import lombok.Data;

import java.util.List;

@Data
public class PostDetailResponse {
    private String postId;
    private String title;
    private String detail;
    private UserInfo userInfo;
    private List<PostComment> postComments;

    @Data
    public static class UserInfo {
        private String userId;
        private String userName;
        private String email;
    }

    @Data
    public static class PostComment {
        private String userId;
        private Comment comment;
        private List<Comment> subComments;
    }

    @Data
    public static class Comment {
        private String commentId;
        private String commentDetail;
    }
}
