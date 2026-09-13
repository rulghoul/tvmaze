package com.ghoulrul.tvmaze.controller;

import com.ghoulrul.tvmaze.dto.CommentDTO;
import com.ghoulrul.tvmaze.dto.CommentRequest;
import com.ghoulrul.tvmaze.dto.ShowDTO;
import com.ghoulrul.tvmaze.entities.CommentMongo;
import com.ghoulrul.tvmaze.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("comment")
@Tag(name = "Tv Maze", description = "API para recuperar informacion de series desde Tv Maze")
public class CommentController {

    private CommentService service;

    @Autowired
    public CommentController(CommentService service){
        this.service = service;
    }

    @Operation(summary = "Recupera comentario/s", description = "Recupera uno o mas comentarios a partir del Id de un show.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Recuperado exitosamente", content = @Content(schema = @Schema(implementation = CommentDTO.class))),
            @ApiResponse(
                    responseCode = "204",
                    description = "No se encontraron shows con los fase solicitada",
                    content = @Content
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<List<CommentDTO>> getFromSingleID(@PathVariable Integer id){
        var comments = service.obtenerPorShow(id);
        if(comments.isEmpty()){
            return ResponseEntity.noContent().build();
        }

        var dtos = comments.stream().map(CommentMongo::toDto)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Recupera comentario/s", description = "Recupera uno o mas comentarios a partir del Id de un show.")
    @ApiResponses(value = {

            @ApiResponse(
                    responseCode = "200",
                    description = "Comentario creado Exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommentDTO.class))),
            @ApiResponse(
                    responseCode = "204",
                    description = "No se pudo crear el comentario",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Fallo al guardar el comentario",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping
    public ResponseEntity<CommentDTO> saveComment(@Valid @RequestBody CommentRequest comentario){
        var comment = service.guardarComentario(comentario);
        return ResponseEntity.ok(comment.toDto());
    }


    @Operation(summary = "Recupera comentario/s", description = "Recupera uno o mas comentarios a partir deuna lista de Id's de uno o vaiors shows.")
    @ApiResponses(value = {

            @ApiResponse(
                    responseCode = "200",
                    description = "Se encontraron comentarios de uno o mas shows solicitados",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommentDTO.class))),
            @ApiResponse(
                    responseCode = "204",
                    description = "No se encontraron comentarios",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Fallo al recuperar los comentarios",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/multiple")
    public ResponseEntity<List<CommentDTO>> saveComment(@Valid @RequestBody List<Integer> ids){
        var comments = service.obtenerPorListaDeShows(ids);
        if(comments.isEmpty()){
            return ResponseEntity.noContent().build();
        }

        var dtos = comments.stream().map(CommentMongo::toDto)
                .toList();
        return ResponseEntity.ok(dtos);
    }


}
