package com.enthusiasm.saga.orchestration;

import com.enthusiasm.saga.orchestration.command.CancelPostCommand;
import com.enthusiasm.saga.orchestration.command.CreateNotificationCommand;
import com.enthusiasm.saga.orchestration.command.CreatePostCommand;
import com.enthusiasm.saga.orchestration.command.HoldRewardCommand;

import java.math.BigDecimal;
import java.util.UUID;

public class QuestionCreateState {
    private State state;

    private final UUID postId;
    private final UUID userId;

    private final BigDecimal reward;

    public QuestionCreateState(UUID postId, UUID userId, BigDecimal reward) {
        this.postId = postId;
        this.userId = userId;
        this.reward = reward;
    }

    public String getUserId() {
        return userId.toString();
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

    enum State {
        REQUEST_POSTING,
        PAYMENT_AWAIT
    }
}
