package com.enthusiasm.saga.orchestration.command;

import com.enthusiasm.saga.core.Command;

import java.math.BigDecimal;
import java.util.UUID;

public record CreatePostCommand (
        UUID postId,
        String postTitle,
        String postDetail,
        UUID userId,
        BigDecimal reward
) implements Command {
}
