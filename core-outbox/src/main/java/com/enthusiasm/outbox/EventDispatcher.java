package com.enthusiasm.outbox;

public interface EventDispatcher {
    void onExportedEvent(ExportedEvent<?, ?> event);
}
