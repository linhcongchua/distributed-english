package com.enthusiasm.saga.orchestration;

import com.enthusiasm.common.core.SagaResponse;
import com.enthusiasm.common.forum.command.CreatePostCommand;
import com.enthusiasm.common.forum.command.PostCreatePendingEvent;
import com.enthusiasm.common.notifcation.command.NotifyPostSuccessCommand;
import com.enthusiasm.saga.core.Endpoint;
import com.enthusiasm.saga.core.SagaDefinition;
import com.enthusiasm.saga.orchestration.command.CancelPostCommand;
import com.enthusiasm.saga.orchestration.command.HoldRewardCommand;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuestionCreatedSaga {

    private static Endpoint<CreatePostCommand, QuestionCreateState, PostCreatePendingEvent> FORUM_CREATE_POST = Endpoint.<CreatePostCommand, QuestionCreateState, PostCreatePendingEvent>builder()
            .withService("forum-service")
            .withTopic("post")
            .withHeader("COMMAND_TYPE", "CREATE_POST_COMMAND")
            .withReplyHandler(QuestionCreateState::handleCreatePostResponse) // todo: fix reply handle
            .withKeyProvider(QuestionCreateState::getId)
            .withValueProvider(QuestionCreateState::createPostCommand)
            .build(PostCreatePendingEvent.class);

    private static Endpoint<CancelPostCommand, QuestionCreateState, SagaResponse> FORUM_CANCEL_POST = Endpoint.<CancelPostCommand, QuestionCreateState, SagaResponse>builder()
            .withService("forum-service")
            .withTopic("post")
            .withHeader("COMMAND_TYPE", "CANCEL_POST_COMMAND")
            .withReplyHandler(QuestionCreateState::handleCancelPostResponse)
            .withKeyProvider(QuestionCreateState::getId)
            .withValueProvider(QuestionCreateState::cancelPostCommand)
            .build(SagaResponse.class);

    private static Endpoint<HoldRewardCommand, QuestionCreateState, SagaResponse> ACCOUNT_HOLD_REWARD = Endpoint.<HoldRewardCommand, QuestionCreateState, SagaResponse>builder()
            .withService("payment-service")
            .withTopic("emoney")
            .withHeader("COMMAND_TYPE", "WITHDRAW_ACCOUNT_COMMAND")
            .withReplyHandler(QuestionCreateState::handleHoldRewardResponse)
            .withKeyProvider(QuestionCreateState::getId)
            .withValueProvider(QuestionCreateState::holdRewardCommand)
            .build(SagaResponse.class);

    private static Endpoint<NotifyPostSuccessCommand, QuestionCreateState, SagaResponse> CREATE_NOTIFICATION = Endpoint.<NotifyPostSuccessCommand, QuestionCreateState, SagaResponse>builder()
            .withService("notification-service")
            .withTopic("notify")
            .withKeyProvider(QuestionCreateState::getId)
            .withValueProvider(QuestionCreateState::createNotificationCommand)
            .build(SagaResponse.class); // todo


    @Bean
    SagaDefinition<QuestionCreateState> questionCreateStateDefinition() {

        // todo: auto generate stepId
        return SagaDefinition.<QuestionCreateState>builder("orchestration-create-post")
                .withStateClass(QuestionCreateState.class)
                .step().invoke(FORUM_CREATE_POST).withCompensation(FORUM_CANCEL_POST).next()
                .step().invoke(ACCOUNT_HOLD_REWARD).next()
                .step().invoke(CREATE_NOTIFICATION).next()
                .build();
    }
}
