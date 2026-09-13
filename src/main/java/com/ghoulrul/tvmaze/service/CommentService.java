package com.ghoulrul.tvmaze.service;

import com.ghoulrul.tvmaze.dto.CommentRequest;
import com.ghoulrul.tvmaze.entities.CommentMongo;
import com.ghoulrul.tvmaze.exception.MongoAtlasException;
import com.ghoulrul.tvmaze.repository.CommentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CommentService {

    private final CommentRepository commentRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public CommentMongo guardarComentario(CommentRequest request) {
        try {
            CommentMongo comentario = new CommentMongo(request);
            return commentRepository.save(comentario);
        } catch (Exception e) {
            log.error("No se pudo guardar el objeto {} por: {}", request, e.getMessage());
            throw new MongoAtlasException(e.getMessage());
        }
    }

    public List<CommentMongo> obtenerPorShow(Integer idShow) {
        return commentRepository.findByIdShow(idShow);
    }

    public List<CommentMongo> obtenerPorListaDeShows(List<Integer> idsShow) {
        return commentRepository.findByIdShowIn(idsShow);
    }

}