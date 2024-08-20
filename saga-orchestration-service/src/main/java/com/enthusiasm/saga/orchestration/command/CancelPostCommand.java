package com.enthusiasm.saga.orchestration.command;

import com.enthusiasm.common.core.Command;

import java.util.UUID;

public class CancelPostCommand implements Command {
    private UUID uuid;

    public CancelPostCommand(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
}
