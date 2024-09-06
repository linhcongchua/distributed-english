package com.enthusiam.common.event.forum;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
public class PostCreatePendingEvent {
    private UUID postId;
    private String postTitle;
    private String postDetail;

    @Data
    @Builder
    public static class UserInfo {
        private UUID userId;
        private String userName;
        private String email;
    }
}
