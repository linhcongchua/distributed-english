package com.enthusiasm.forum.commands;

import com.enthusiasm.dispatcher.command.CommandDispatcher;
import com.enthusiasm.dispatcher.command.CommandHandler;
import com.enthusiasm.forum.entities.PostEntity;
import com.enthusiasm.forum.repository.PostRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

@Component
@CommandDispatcher(service = "forum-service", topic = "post")
public class PostCommandHandler implements PostCommandService{

    private final PostRepository postRepository;

    public PostCommandHandler(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    @Transactional
    @CommandHandler(commandType = "CREATE_POST_COMMAND")
    public void handle(CreatePostCommand command) {
        // todo: check userId exist

        var entity = new PostEntity();
        entity.setId(command.postId());
        entity.setTitle(command.postTitle());
        entity.setDetail(command.postDetail());
        entity.setUserId(command.userId());
        entity.setDeleted(false);

        postRepository.save(entity);

        // todo: reply
    }

    @Override
    @Transactional
    @CommandHandler(commandType = "CANCEL_POST_COMMAND")
    public void handle(CancelPostCommand command) {
        var entity = postRepository.findById(command.postId())
                .orElseThrow(() -> new RuntimeException("Not found post by id " + command.postId()));
        entity.setDeleted(true);

        postRepository.save(entity);
        // todo: reply
    }
}
