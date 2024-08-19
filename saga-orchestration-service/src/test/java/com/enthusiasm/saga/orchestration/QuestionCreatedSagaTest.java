package com.enthusiasm.saga.orchestration;

import com.enthusiasm.saga.core.SagaDefinition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QuestionCreatedSagaTest {
    @Test
    void testCreateDefinition() {
        QuestionCreatedSaga saga = new QuestionCreatedSaga();
        SagaDefinition<QuestionCreateState> questionCreateStateSagaDefinition = saga.questionCreateStateDefinition();
        assertNotNull(questionCreateStateSagaDefinition);
    }

}