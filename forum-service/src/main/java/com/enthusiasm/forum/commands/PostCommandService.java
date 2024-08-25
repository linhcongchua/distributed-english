package com.enthusiasm.forum.commands;

import com.enthusiasm.common.core.SagaHeader;
import com.enthusiasm.common.forum.command.CreatePostCommand;

import java.util.Map;

public interface PostCommandService {
    void handle(CreatePostCommand command, SagaHeader sagaHeader);
    void handle(CancelPostCommand command);
}
