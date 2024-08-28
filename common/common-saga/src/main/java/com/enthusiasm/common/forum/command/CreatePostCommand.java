package com.enthusiasm.common.forum.command;

import com.enthusiasm.common.core.Command;

import java.util.UUID;

public record CreatePostCommand(
        UUID postId,
        String postTitle,
        String postDetail,
        UUID userId
) implements Command {
}
