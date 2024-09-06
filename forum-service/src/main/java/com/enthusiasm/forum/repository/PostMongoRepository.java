package com.enthusiasm.forum.repository;

import com.enthusiasm.forum.entities.read.PostDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PostMongoRepository extends MongoRepository<PostDocument, String> {
}
