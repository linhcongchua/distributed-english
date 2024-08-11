package com.enthusiam.gateway.forum;

import com.enthusiam.gateway.forum.command.CreatePostCommand;
import com.enthusiam.gateway.forum.command.CreatePostRequest;
import com.enthusiam.gateway.forum.command.CreatePostResponse;
import com.enthusiam.gateway.forum.command.PostDetailResponse;
import com.enthusiasm.common.jackson.SerializerUtils;
import com.enthusiasm.producer.MessageProducer;
import org.springframework.web.bind.annotation.*;

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
//        Map<String, String> header = Map.of("COMMAND_TYPE", "CREATE_POST_COMMAND");

        UUID postId = UUID.randomUUID();
        var command = new CreatePostCommand(
                postId, request.postTitle(), request.postDetail(), request.userId(), request.reward());
        messageProducer.send("saga-orchestration-post-creating", request.userId().toString(), SerializerUtils.serializeToJsonBytes(command));
        return new CreatePostResponse(postId);
    }

    @GetMapping("/post/{postId}")
    public PostDetailResponse getPost(@PathVariable UUID postId) {
        throw new RuntimeException("Not implement yet");
    }
}
