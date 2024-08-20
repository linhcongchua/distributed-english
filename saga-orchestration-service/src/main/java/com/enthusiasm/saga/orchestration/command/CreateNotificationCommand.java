package com.enthusiasm.saga.orchestration.command;

import com.enthusiasm.common.core.Command;

import java.util.UUID;

public record CreateNotificationCommand(
        UUID postId,
        UUID userId
) implements Command {

}
