package com.enthusisam.proto;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.Struct;
import com.google.protobuf.util.JsonFormat;

public class JsonUtils {

    private JsonUtils() {
    }

    public static String toJson(MessageOrBuilder messageOrBuilder) {
        try {
            return JsonFormat.printer().print(messageOrBuilder);
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
    }

    public static Message fromJson(String json) {
        try {
            Struct.Builder builder = Struct.newBuilder();
            JsonFormat.parser().ignoringUnknownFields().merge(json, builder);
            return builder.build();
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Message> T fromJson(String json, Class<T> clazz) {
        try {
            T t = clazz.getDeclaredConstructor().newInstance();
            Message.Builder builder = t.toBuilder();
            JsonFormat.parser().ignoringUnknownFields().merge(json, builder);
            return (T) builder.build();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}
