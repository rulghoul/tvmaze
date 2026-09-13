package com.ghoulrul.tvmaze.controller;

import com.ghoulrul.tvmaze.dto.ShowDTO;
import com.ghoulrul.tvmaze.service.TvMazeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("programas")
@Tag(name = "Tv Maze", description = "API para recuperar informacion de series desde Tv Maze")
public class TvMazeController {

    private TvMazeService mazeService;

    @Autowired
    public TvMazeController(TvMazeService service){
        this.mazeService = service;
    }

    @Operation(summary = "Obtener lista de shows por cadena de busqueda", description = "Devuelve una lista de show con la palapra o palabra clave solicitadas")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de shows por palabra/s clave",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ShowDTO.class))),
            @ApiResponse(
                    responseCode = "204",
                    description = "No se encontraron shows con los fase solicitada",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Parámetro de búsqueda inválido, favor de probar otra palabra",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/busqueda/{busqueda}")
    public ResponseEntity<List<ShowDTO>> buscaShows(
            @PathVariable String busqueda
    ){
        var resultado = mazeService.busqueda(busqueda);
        if (resultado.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(resultado);
    }


    @Operation(summary = "Muestra informacion de un show por id", description = "Muestra informacion de un show por id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Muestra informacion de un show por id",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ShowDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Show no encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "ID inválido",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/informacion/{idShow}")
    public ResponseEntity<ShowDTO> informacionShow(
            @PathVariable Integer idShow
    ){
        var info = mazeService.informacion(idShow);
        return ResponseEntity.ok(info);
    }
}
