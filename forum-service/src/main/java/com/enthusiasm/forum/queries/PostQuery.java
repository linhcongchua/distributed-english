package com.enthusiasm.forum.queries;


import com.enthusiasm.common.jackson.SerializerUtils;
import com.enthusiasm.forum.entities.read.PostDocument;
import com.enthusiasm.forum.repository.PostMongoRepository;
import com.enthusiasm.proto.PostDetailRequest;
import com.enthusiasm.proto.PostDetailResponse;
import com.enthusiasm.proto.PostServiceGrpc;
import com.enthusisam.proto.JsonUtils;
import com.enthusisam.proto.LogInterceptor;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcService(
        interceptors = LogInterceptor.class
)
public class PostQuery extends PostServiceGrpc.PostServiceImplBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostQuery.class);

    private final PostMongoRepository postMongoRepository;

    public PostQuery(PostMongoRepository postMongoRepository) {
        this.postMongoRepository = postMongoRepository;
    }

    @Override
    public void getPostDetail(PostDetailRequest request, StreamObserver<PostDetailResponse> responseObserver) {
        PostDocument postDocument = postMongoRepository.findById(request.getPostId())
                .orElseThrow(() -> new NotFoundException("Not found post by id " + request.getPostId()));

        PostDetailResponse response = JsonUtils.fromJson(
                SerializerUtils.serializeToJsonStr(postDocument), PostDetailResponse.class);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
