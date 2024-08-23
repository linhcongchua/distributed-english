package com.enthusiasm.notification.handler;


import com.enthusiasm.common.notifcation.command.NotifyPostSuccessCommand;
import com.enthusiasm.notification.NotificationServiceApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = "notification-service-notify")
public class NotificationHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationServiceApplication.class);

    @KafkaHandler
    public void listenToPartition(NotifyPostSuccessCommand notifyPostSuccessCommand) {
        LOGGER.info("NotifyPostSuccessCommand {}", notifyPostSuccessCommand);
        // todo: notify user & follower -> algorithm to suggest post
    }
}
