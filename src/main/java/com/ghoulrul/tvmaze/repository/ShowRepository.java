package com.ghoulrul.tvmaze.repository;

import com.ghoulrul.tvmaze.entities.ShowMongo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShowRepository extends MongoRepository<ShowMongo, Long> {
    Optional<ShowMongo> findByIdShow(Integer idShow);
}
