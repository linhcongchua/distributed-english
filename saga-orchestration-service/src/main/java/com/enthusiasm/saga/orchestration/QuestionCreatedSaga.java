package com.enthusiasm.saga.orchestration;

import com.enthusiasm.saga.core.SagaDefinition;

public class QuestionCreatedSaga {
    SagaDefinition exampleSaga() {
        return SagaDefinition.<QuestionCreateState>builder()
                .withDescription("User create post with reward")
                .step()
                    .withDescription("")
                    .invoke(null, null).withCompensation(null, null).withReplyTo("")
                    .next()
                .step()
                    .withDescription("")
                    .withReplyTo("")
                        .invoke(null, null).withCompensation(null, null)
                        .invoke(null, null).withCompensation(null, null)
                    .next()
                .step()
                    .invoke(null, null)
                    .next()
                .build();
    }
}
