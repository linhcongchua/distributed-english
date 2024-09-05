package com.enthusiasm.forum.projections;

import com.enthusiasm.common.forum.command.PostCreatePendingEvent;
import com.enthusiasm.dispatcher.event.EventBody;
import com.enthusiasm.dispatcher.event.EventDispatcher;
import com.enthusiasm.dispatcher.event.EventHandler;
import com.enthusiasm.forum.entities.read.PostDocument;
import com.enthusiasm.forum.repository.PostMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@EventDispatcher(service = "forum-service", topic = "", group = "post-mongo-project")
public class PostMongoProjection {

    private final static Logger LOGGER = LoggerFactory.getLogger(PostMongoProjection.class);

    private final PostMongoRepository postMongoRepository;

    public PostMongoProjection(PostMongoRepository postMongoRepository) {
        this.postMongoRepository = postMongoRepository;
    }

    @EventHandler(eventType = "POST_CREATING_EVENT")
    public void handlePostCreated(@EventBody PostCreatePendingEvent event) {
        final var document = PostDocument.builder()
                .id("")
                .build();

        final var inserted = postMongoRepository.insert(document);
        LOGGER.info("Synchronized post to read database");
    }


}
