package com.enthusiasm.common.notifcation.command;

import com.enthusiasm.common.core.Command;

import java.util.UUID;

public record NotifyPostSuccessCommand(
        UUID postId,
        UUID userId
) implements Command {

}
