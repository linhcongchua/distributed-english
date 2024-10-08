package com.enthusiam.gateway.forum.command;

import java.math.BigDecimal;
import java.util.UUID;

public record CreatePostCommand(
        UUID postId,
        String postTitle,
        String postDetail,
        UUID userId,
        BigDecimal reward
) {
}
