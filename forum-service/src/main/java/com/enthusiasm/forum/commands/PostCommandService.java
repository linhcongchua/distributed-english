package com.enthusiasm.forum.commands;

public interface PostCommandService {
    void handle(CreatePostCommand command);
    void handle(CancelPostCommand command);
}
