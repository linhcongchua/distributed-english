package com.enthusiam.gateway.forum.command;

import java.math.BigDecimal;
import java.util.UUID;

public record CreatePostRequest(
        String postTitle,
        String postDetail,
        UUID userId,
        BigDecimal reward
) {
}
