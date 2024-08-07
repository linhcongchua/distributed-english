package com.enthusiasm.saga.orchestration;

public class QuestionCreateState {
    private State state;



    public static void markAsPaymentWaiting() {

    }

    enum State {
        REQUEST_POSTING,
        PAYMENT_AWAIT
    }
}
