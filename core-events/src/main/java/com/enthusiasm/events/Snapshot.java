package com.enthusiasm.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Snapshot {
    private UUID id;
    private String aggregateId;
    private String aggregateType;
    private byte[] data;
    private byte[] metaData;
    private long version;
    private LocalDateTime timeStamp;

    @Override
    public String toString() {
        return "Snapshot{" +
                "id=" + id +
                ", aggregateId='" + aggregateId + '\'' +
                ", aggregateType='" + aggregateType + '\'' +
                ", data=" + Arrays.toString(data) +
                ", metaData=" + Arrays.toString(metaData) +
                ", version=" + version +
                ", timeStamp=" + timeStamp +
                '}';
    }
}
