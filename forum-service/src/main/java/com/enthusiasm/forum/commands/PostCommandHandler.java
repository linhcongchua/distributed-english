package com.enthusiasm.forum.commands;

import com.enthusiasm.common.core.SagaHeader;
import com.enthusiasm.common.forum.command.CreatePostCommand;
import com.enthusiasm.common.jackson.SerializerUtils;
import com.enthusiasm.dispatcher.command.CommandBody;
import com.enthusiasm.dispatcher.command.CommandDispatcher;
import com.enthusiasm.dispatcher.command.CommandHandler;
import com.enthusiasm.dispatcher.command.CommandHeader;
import com.enthusiasm.forum.entities.PostEntity;
import com.enthusiasm.forum.events.PostCreatePendingEvent;
import com.enthusiasm.forum.repository.PostRepository;
import com.enthusiasm.outbox.EventDispatcher;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@CommandDispatcher(service = "forum-service", topic = "post")
public class PostCommandHandler implements PostCommandService{

    private final PostRepository postRepository;

    private final EventDispatcher eventDispatcher;

    public PostCommandHandler(PostRepository postRepository, EventDispatcher eventDispatcher) {
        this.postRepository = postRepository;
        this.eventDispatcher = eventDispatcher;
    }

    @Override
    @Transactional
    @CommandHandler(commandType = "CREATE_POST_COMMAND")
    public void handle(@CommandBody CreatePostCommand command, @CommandHeader("SAGA_HEADER") SagaHeader sagaHeader) {
        // todo: check userId exist

        var entity = new PostEntity();
        entity.setId(command.postId());
        entity.setTitle(command.postTitle());
        entity.setDetail(command.postDetail());
        entity.setUserId(command.userId());
        entity.setDeleted(false);

        postRepository.save(entity);

        // todo: 20-8 reply outbox
        PostCreatePendingEvent event = new PostCreatePendingEvent(
                command.userId(),
                sagaHeader.topic(),
                new String(SerializerUtils.serializeToJsonBytes(sagaHeader), StandardCharsets.UTF_8)
        );
        eventDispatcher.onExportedEvent(event);
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
