package com.enthusiasm.forum.commands;

import java.util.UUID;

public record CreatePostCommand(
        UUID postId,
        String postTitle,
        String postDetail,
        UUID userId
) {
}
