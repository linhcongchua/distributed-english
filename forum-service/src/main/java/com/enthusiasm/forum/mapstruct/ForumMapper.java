package com.enthusiasm.forum.mapstruct;

import com.enthusiam.common.event.forum.PostCreatePendingEvent;
import com.enthusiasm.common.forum.command.CreatePostCommand;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ForumMapper {
    ForumMapper INSTANCE = Mappers.getMapper(ForumMapper.class);
    PostCreatePendingEvent toEvent(CreatePostCommand command);
}
