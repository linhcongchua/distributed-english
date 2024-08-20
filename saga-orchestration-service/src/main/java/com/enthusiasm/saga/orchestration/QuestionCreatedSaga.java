package com.enthusiasm.saga.orchestration;

import com.enthusiasm.common.forum.command.CreatePostCommand;
import com.enthusiasm.saga.core.Endpoint;
import com.enthusiasm.saga.core.SagaDefinition;
import com.enthusiasm.saga.orchestration.command.CancelPostCommand;
import com.enthusiasm.saga.orchestration.command.CreateNotificationCommand;
import com.enthusiasm.saga.orchestration.command.HoldRewardCommand;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuestionCreatedSaga {

    private static Endpoint<CreatePostCommand, QuestionCreateState> FORUM_CREATE_POST = Endpoint.<CreatePostCommand, QuestionCreateState>builder()
            .withService("forum-service")
            .withTopic("post")
            .withHeader("COMMAND_TYPE", "CREATE_POST_COMMAND")
            .withReplyHandler(QuestionCreateState::handleCreatePostResponse) // todo: fix reply handle
            .withKeyProvider(QuestionCreateState::getId)
            .withValueProvider(QuestionCreateState::createPostCommand)
            .build();

    private static Endpoint<CancelPostCommand, QuestionCreateState> FORUM_CANCEL_POST = Endpoint.<CancelPostCommand, QuestionCreateState>builder()
            .withService("forum-service")
            .withTopic("post")
            .withHeader("COMMAND_TYPE", "CANCEL_POST_COMMAND")
            .withReplyHandler(QuestionCreateState::handleCancelPostResponse)
            .withKeyProvider(QuestionCreateState::getId)
            .withValueProvider(QuestionCreateState::cancelPostCommand)
            .build();

    private static Endpoint<HoldRewardCommand, QuestionCreateState> ACCOUNT_HOLD_REWARD = Endpoint.<HoldRewardCommand, QuestionCreateState>builder()
            .withService("payment-service")
            .withTopic("")
            .withHeader("", "")
            .withReplyHandler(QuestionCreateState::handleHoldRewardResponse)
            .withKeyProvider(QuestionCreateState::getId)
            .withValueProvider(QuestionCreateState::holdRewardCommand)
            .build();

    private static Endpoint<CreateNotificationCommand, QuestionCreateState> CREATE_NOTIFICATION = Endpoint.<CreateNotificationCommand, QuestionCreateState>builder()
            .withService("notification-service")
            .withTopic("")
            .withKeyProvider(QuestionCreateState::getId)
            .withValueProvider(QuestionCreateState::createNotificationCommand)
            .build();


    @Bean
    SagaDefinition<QuestionCreateState> questionCreateStateDefinition() {

        // todo: auto generate stepId
        return SagaDefinition.<QuestionCreateState>builder("orchestration-create-post")
                .withStateClass(QuestionCreateState.class)
                .withDescription("User create post with reward")
                .withInitializedFunction(QuestionCreateState::fromByte)
                .step()
                .withDescription("")
                .invoke(FORUM_CREATE_POST).withCompensation(FORUM_CANCEL_POST)
                .next()
                .step()
                .withDescription("")
                .invoke(ACCOUNT_HOLD_REWARD)
                .next()
                .step()
                .invoke(CREATE_NOTIFICATION)
                .next()
                .build();
    }
}
