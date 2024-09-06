package com.enthusiasm.forum.projections;

import com.enthusiam.common.event.Constants;
import com.enthusiam.common.event.forum.PostCreatePendingEvent;
import com.enthusiasm.dispatcher.event.EventBody;
import com.enthusiasm.dispatcher.event.EventDispatcher;
import com.enthusiasm.dispatcher.event.EventHandler;
import com.enthusiasm.forum.entities.read.PostDocument;
import com.enthusiasm.forum.repository.PostMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@EventDispatcher(topic = Constants.POST_TOPIC_EVENT, group = "post-mongo-project")
public class PostMongoProjection {

    private final static Logger LOGGER = LoggerFactory.getLogger(PostMongoProjection.class);

    private final PostMongoRepository postMongoRepository;

    public PostMongoProjection(PostMongoRepository postMongoRepository) {
        this.postMongoRepository = postMongoRepository;
    }

    @EventHandler(eventType = "POST_CREATING_EVENT")
    public void handlePostCreated(@EventBody PostCreatePendingEvent event) {
        final var document = PostDocument.builder()
                .id(event.getPostId().toString())
                .title(event.getPostTitle())
                .detail(event.getPostDetail())
                .build();

        final var inserted = postMongoRepository.insert(document);
        LOGGER.info("Synchronized post to read database: {}", inserted);
    }
}
