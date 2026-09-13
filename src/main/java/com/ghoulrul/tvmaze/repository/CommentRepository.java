package com.ghoulrul.tvmaze.repository;

import com.ghoulrul.tvmaze.entities.CommentMongo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends MongoRepository<CommentMongo, String> {
    List<CommentMongo> findByIdShow(Integer idShow);
    List<CommentMongo> findByIdShowIn(List<Integer> idsShow);

}
