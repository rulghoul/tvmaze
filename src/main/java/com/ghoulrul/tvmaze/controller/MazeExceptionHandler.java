package com.ghoulrul.tvmaze.controller;

import com.ghoulrul.tvmaze.exception.MazeException;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
@Tag(name = "Errores", description = "Manejador de excepciones mara Tv Maze")
public class MazeExceptionHandler {
    @ExceptionHandler(MazeException.class)
    public ResponseEntity<ErrorResponse> handleMazeException(
            MazeException ex,
            HttpServletRequest request
    ) {
        ErrorResponse error = ErrorResponse.builder(ex, ex.getStatus(), "Fallo con el servicio TvMaze").build();

        return new ResponseEntity<>(error, ex.getStatus());
    }

    // Puedes agregar más handlers para otras excepciones
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request
    ) {
        ErrorResponse error = ErrorResponse.builder(ex,
                ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,"Error Interno del Servidor"))
                .build();


        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
