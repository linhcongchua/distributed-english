package com.enthusiasm.forum.entities.read;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@Document(collection = "posts")
public class PostDocument {
    private String id;
    private String title;
    private String detail;

    private UserInfo userInfo;

    private PostComment postComment;

    @Data
    @Builder
    public static class UserInfo {
        private String userId;
        private String userName;
        private String email;
    }

    @Data
    @Builder
    public static class PostComment {
        private UserInfo userInfo;
        private Comment comment;
        private List<Comment> subComments;
    }

    @Data
    @Builder
    public static class Comment {
        private String commentId;
        private UserInfo userInfo;
        private String commentDetail;
    }
}

