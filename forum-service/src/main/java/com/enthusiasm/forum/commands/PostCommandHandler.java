package com.enthusiasm.forum.commands;

import com.enthusiam.common.event.Constants;
import com.enthusiam.common.event.EventOutbox;
import com.enthusiam.common.event.EventPublisher;
import com.enthusiam.common.event.forum.PostCreatePendingEvent;
import com.enthusiasm.common.core.SagaHeader;
import com.enthusiasm.common.core.SagaMessageReplier;
import com.enthusiasm.common.core.SagaReplyOutbox;
import com.enthusiasm.common.forum.command.CreatePostCommand;
import com.enthusiasm.common.util.SagaReplyUtils;
import com.enthusiasm.dispatcher.command.CommandBody;
import com.enthusiasm.dispatcher.command.CommandDispatcher;
import com.enthusiasm.dispatcher.command.CommandHandler;
import com.enthusiasm.dispatcher.command.CommandHeader;
import com.enthusiasm.forum.entities.PostEntity;
import com.enthusiasm.forum.mapstruct.ForumMapper;
import com.enthusiasm.forum.repository.PostRepository;
import com.enthusiasm.outbox.EventDispatcher;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

@Component
@CommandDispatcher(service = "forum-service", topic = "post")
public class PostCommandHandler implements PostCommandService, SagaMessageReplier, EventPublisher {

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

        PostCreatePendingEvent event = ForumMapper.INSTANCE.toEvent(command);
        publishEvent(command.userId().toString(), Constants.POST_TOPIC_EVENT, "POST_CREATING_EVENT", event);

        replySaga(command.userId().toString(), sagaHeader, SagaReplyUtils.success());
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

    @Override
    public <T> void pushMessage(SagaReplyOutbox<T> reply) {
        eventDispatcher.onExportedEvent(reply);
    }

    @Override
    public <T> void publish(EventOutbox<T> eventWrapper) {
        eventDispatcher.onExportedEvent(eventWrapper);
    }
}
