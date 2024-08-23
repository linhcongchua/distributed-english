package com.enthusiasm.saga.orchestration;

import com.enthusiasm.common.core.SagaResponse;
import com.enthusiasm.common.forum.command.PostCreatePendingEvent;
import com.enthusiasm.common.notifcation.command.NotifyPostSuccessCommand;
import com.enthusiasm.saga.core.SagaState;
import com.enthusiasm.saga.orchestration.command.CancelPostCommand;
import com.enthusiasm.saga.orchestration.command.HoldRewardCommand;

import java.math.BigDecimal;
import java.util.UUID;

public class QuestionCreateState implements SagaState {
    private UUID postId;
    private UUID userId;
    private String postTitle;
    private String postDetail;
    private BigDecimal reward;

    public QuestionCreateState() {
    }

    public QuestionCreateState(UUID postId, UUID userId, String postTitle, String postDetail, BigDecimal reward) {
        this.postId = postId;
        this.userId = userId;
        this.postTitle = postTitle;
        this.postDetail = postDetail;
        this.reward = reward;
    }

    public com.enthusiasm.common.forum.command.CreatePostCommand createPostCommand() {
        return new com.enthusiasm.common.forum.command.CreatePostCommand(postId, postTitle, postDetail, userId);
    }

    public boolean handleCreatePostResponse(PostCreatePendingEvent response) {
        //
        return true;
    } // todo: using reflection

    public CancelPostCommand cancelPostCommand() {
        return new CancelPostCommand(postId);
    }

    public boolean handleCancelPostResponse(SagaResponse response) {
        return true;
    }

    public HoldRewardCommand holdRewardCommand() {
        return new HoldRewardCommand(userId, reward);
    }

    public boolean handleHoldRewardResponse(SagaResponse response) {
        return true;
    }


    public NotifyPostSuccessCommand createNotificationCommand() {
        return new NotifyPostSuccessCommand(postId, userId);
    }

    @Override
    public String getId() {
        return userId.toString();
    }

    public UUID getPostId() {
        return postId;
    }

    public void setPostId(UUID postId) {
        this.postId = postId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostDetail() {
        return postDetail;
    }

    public void setPostDetail(String postDetail) {
        this.postDetail = postDetail;
    }

    public BigDecimal getReward() {
        return reward;
    }

    public void setReward(BigDecimal reward) {
        this.reward = reward;
    }
}
