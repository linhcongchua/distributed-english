package com.enthusiasm.payment.events;

import com.enthusiasm.events.BaseEvent;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AccountCreatedEvent extends BaseEvent {
    @Builder
    public AccountCreatedEvent(String aggregateId) {
        super(aggregateId);
    }
}
