package com.ghoulrul.tvmaze.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        name = "Show",
        description = "Información resumida de un show de televisión obtenido desde TVMaze"
)
public class Show {
    @Schema(
            description = "ID único del show en TVMaze",
            example = "51006"
    )
    private Integer id;
    @Schema(
            description = "Nombre del show",
            example = "Demon Lord Dante"
    )
    private String name;

    @Schema(
            description = "Canal de transmisión (network o webChannel)",
            example = "AT-X"
    )
    private String channel;

    @Schema(
            description = "Sinopsis del show en formato HTML",
            example = "<p>While sleeping one night, Ryo Utsugi had a nightmare of monsters attacking him...."
    )
    private String summary;

    @Schema(
            description = "Lista de géneros del show",
            example = "[\"Action\", \"Anime\", \"Horror\", \"Supernatural\"]"
    )
    private List<String> generes;
}
