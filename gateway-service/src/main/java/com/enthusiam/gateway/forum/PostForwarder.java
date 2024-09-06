package com.enthusiam.gateway.forum;

import com.enthusiam.gateway.forum.command.CreatePostCommand;
import com.enthusiam.gateway.forum.command.CreatePostRequest;
import com.enthusiam.gateway.forum.command.CreatePostResponse;
import com.enthusiam.gateway.forum.command.PostDetailResponse;
import com.enthusiasm.common.core.SagaHeader;
import com.enthusiasm.common.jackson.DeserializerUtils;
import com.enthusiasm.common.jackson.SerializerUtils;
import com.enthusiasm.producer.MessageProducer;
import com.enthusiasm.proto.PostDetailRequest;
import com.enthusiasm.proto.PostServiceGrpc;
import com.enthusisam.proto.JsonUtils;
import io.grpc.*;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("api/v1")
public class PostForwarder {

    private final MessageProducer messageProducer;
    private final Channel forumChannel;

    public PostForwarder(MessageProducer messageProducer,@Autowired @Qualifier("forumChannel") Channel forumChannel) {
        this.messageProducer = messageProducer;
        this.forumChannel = forumChannel;
    }

    @PostMapping("/post")
    public CreatePostResponse createPost(@RequestBody CreatePostRequest request) {

        UUID postId = UUID.randomUUID();
        var command = new CreatePostCommand(
                postId, request.postTitle(), request.postDetail(), request.userId(), request.reward());
        messageProducer.send("orchestration-create-post", request.userId().toString(), SerializerUtils.serializeToJsonBytes(command),
                () -> {
                    Map<String, Object> extraHeader = new HashMap<>();
                    extraHeader.put("SAGA_HEADER", SagaHeader.getInitial());
                    RecordHeader recordHeader = new RecordHeader("EXTRA_HEADER", SerializerUtils.serializeToJsonBytes(extraHeader));
                    return List.of(recordHeader);
                });
        return new CreatePostResponse(postId);
    }

    @GetMapping("/post/{postId}")
    public PostDetailResponse getPost(@PathVariable UUID postId) {
        PostServiceGrpc.PostServiceBlockingStub blockingStub = PostServiceGrpc.newBlockingStub(forumChannel);
        PostDetailRequest request = PostDetailRequest.newBuilder()
                .setPostId(postId.toString()).build();

        var response = blockingStub.getPostDetail(request);
        String json = JsonUtils.toJson(response);
        return DeserializerUtils.deserialize(json, PostDetailResponse.class);
    }
}
