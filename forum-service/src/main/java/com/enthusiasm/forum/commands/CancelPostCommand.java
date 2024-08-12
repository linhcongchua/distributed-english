package com.enthusiasm.forum.commands;

import java.util.UUID;

public record CancelPostCommand(
        UUID postId
) {

}
