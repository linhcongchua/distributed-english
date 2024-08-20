package com.enthusiam.gateway.forum;

import com.enthusiasm.common.core.SagaHeader;
import com.enthusiam.gateway.forum.command.CreatePostCommand;
import com.enthusiam.gateway.forum.command.CreatePostRequest;
import com.enthusiam.gateway.forum.command.CreatePostResponse;
import com.enthusiam.gateway.forum.command.PostDetailResponse;
import com.enthusiasm.common.jackson.SerializerUtils;
import com.enthusiasm.producer.MessageProducer;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1")
public class PostForwarder {

    private final MessageProducer messageProducer;

    public PostForwarder(MessageProducer messageProducer) {
        this.messageProducer = messageProducer;
    }

    @PostMapping("/post")
    public CreatePostResponse createPost(@RequestBody CreatePostRequest request) {

        UUID postId = UUID.randomUUID();
        var command = new CreatePostCommand(
                postId, request.postTitle(), request.postDetail(), request.userId(), request.reward());
        messageProducer.send("orchestration-create-post", request.userId().toString(), SerializerUtils.serializeToJsonBytes(command),
                () -> {
                    RecordHeader recordHeader = new RecordHeader("SAGA_HEADER", SerializerUtils.serializeToJsonBytes(SagaHeader.getInitial()));
                    return List.of(recordHeader);
                });
        return new CreatePostResponse(postId);
    }

    @GetMapping("/post/{postId}")
    public PostDetailResponse getPost(@PathVariable UUID postId) {
        throw new RuntimeException("Not implement yet");
    }
}
