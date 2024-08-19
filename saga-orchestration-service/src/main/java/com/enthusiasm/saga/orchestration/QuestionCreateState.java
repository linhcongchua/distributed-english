package com.enthusiasm.saga.orchestration;

import com.enthusiasm.common.jackson.DeserializerUtils;
import com.enthusiasm.saga.core.SagaState;
import com.enthusiasm.saga.orchestration.command.CancelPostCommand;
import com.enthusiasm.saga.orchestration.command.CreateNotificationCommand;
import com.enthusiasm.saga.orchestration.command.CreatePostCommand;
import com.enthusiasm.saga.orchestration.command.HoldRewardCommand;
import org.json.JSONPropertyIgnore;

import java.math.BigDecimal;
import java.util.UUID;

public class QuestionCreateState implements SagaState {
    private final UUID postId;
    private final UUID userId;

    private final BigDecimal reward;

    public QuestionCreateState(UUID postId, UUID userId, BigDecimal reward) {
        this.postId = postId;
        this.userId = userId;
        this.reward = reward;
    }

    public static QuestionCreateState fromByte(byte[] bytes) {
        CreatePostCommand command = DeserializerUtils.deserialize(bytes, CreatePostCommand.class);
        return new QuestionCreateState(command.postId(), command.userId(), command.reward());
    }

    public CreatePostCommand createPostCommand() {
        return null; // todo
    }

    public boolean handleCreatePostResponse(byte[] response) {
        return true;
    }

    public CancelPostCommand cancelPostCommand() {
        return new CancelPostCommand(postId);
    }

    public boolean handleCancelPostResponse(byte[] response) {
        return true;
    }

    public HoldRewardCommand holdRewardCommand() {
        return new HoldRewardCommand(userId, reward);
    }

    public boolean handleHoldRewardResponse(byte[] response) {
        return true;
    }


    public CreateNotificationCommand createNotificationCommand() {
        return new CreateNotificationCommand();
    }

    @Override
    public String getId() {
        return userId.toString();
    }
}
