package com.ghoulrul.tvmaze.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        name = "Show",
        description = "Información resumida de un show de televisión obtenido desde TVMaze"
)
public class CommentDTO {
    @Schema(
            description = "Es un show muy bien escrito, con un ritmo impecable y actuaciones de gran calidad",
            example = "51006"
    )
    private String comment;

    @Schema(
            description = "Nota del show",
            example = "5"
    )
    private Integer rating;
}
