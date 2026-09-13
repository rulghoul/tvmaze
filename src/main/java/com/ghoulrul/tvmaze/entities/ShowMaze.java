package com.ghoulrul.tvmaze.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ghoulrul.tvmaze.dto.ShowDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShowMaze {

    private Integer id;
    private String url;
    private String name;
    private String type;
    private String language;
    private List<String> genres;
    private String status;
    private Integer runtime;
    private Integer averageRuntime;
    private LocalDate premiered;
    private LocalDate ended;
    private String officialSite;
    private Schedule schedule;
    private Rating rating;
    private Integer weight;

    // Reutilizamos el mismo record para ambos
    private Channel network;
    private Channel webChannel;

    private Country dvdCountry;
    private Externals externals;
    private Image image;
    private String summary;
    private long updated;
    @JsonProperty("_links")
    private Links links;

    // Constructor vacío (Lombok lo genera con @Data, pero es bueno saber que existe para JSON)

    /**
     * Método de conversión a DTO integrado.
     * Centraliza la lógica de mapeo sin necesitar una tercera clase.
     */
    public ShowDTO toDto() {
        ShowDTO dto = new ShowDTO();
        dto.setId(this.id);
        dto.setName(this.name);
        dto.setSummary(this.summary);
        dto.setGenres(this.genres);

        // Lógica inteligente para unificar network y webChannel
        String canal = Optional.ofNullable(this.network)
                .map(Channel::name)
                .orElseGet(() -> Optional.ofNullable(this.webChannel)
                        .map(Channel::name)
                        .orElse("Desconocido"));
        dto.setChannel(canal);
        return dto;
    }

    // Records para El objeto ShowMaze

    public record Channel(
            int id,
            String name,
            Country country,
            String officialSite
    ) {}

    public record Country(
            String name,
            String code,
            String timezone
    ) {}

    public record Schedule(
            String time,
            List<String> days
    ) {}

    public record Rating(
            Double average
    ) {}

    public record Externals(
            Integer tvrage,
            Integer thetvdb,
            String imdb
    ) {}

    public record Image(
            String medium,
            String original
    ) {}

    public record Links(
            Link self,
            Link previousepisode
    ) {}

    public record Link(
            String href,
            String name
    ) {}
}
