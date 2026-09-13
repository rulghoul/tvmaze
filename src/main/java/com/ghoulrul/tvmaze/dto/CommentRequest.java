package com.ghoulrul.tvmaze.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        name = "Comment Request",
        description = "Peticion para crear un nuevo comentario para un show"
)
public class CommentRequest {
    @Schema(
            description = "ID único del show en TVMaze",
            example = "51006"
    )
    @NotNull(message = "El id no puede estar vacio")
    private Integer id;

    @Schema(
            description = "Comentario sobre el show",
            example = "Es un show muy bien escrito, con un ritmo impecable y actuaciones de gran calidad"
    )
    @NotNull(message = "El comentario no puede estar vacio")
    private String comment;

    @Schema(
            description = "Nota del show",
            example = "5"
    )
    @Min(value = 0, message = "La nota mínima debe ser 0")
    @Max(value = 5, message = "La nota máxima debe ser 5")
    @NotNull(message = "La nota no puede ser nula")
    private Integer rating;
}
