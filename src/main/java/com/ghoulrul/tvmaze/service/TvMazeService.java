package com.ghoulrul.tvmaze.service;

import com.ghoulrul.tvmaze.dto.Show;
import com.ghoulrul.tvmaze.entities.ShowMaze;
import com.ghoulrul.tvmaze.entities.ShowMazeResultado;
import com.ghoulrul.tvmaze.exception.MazeException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class TvMazeService {

    private final RestClient client;

    @Value("${url.info}")
    private String infoURL;

    @Value("${url.search}")
    private String searchURL;


    @Autowired
    public TvMazeService(RestClient.Builder builder){
        this.client = builder
                .build();
    }


    public List<Show> busqueda(String busqueda){
        try {
            log.debug("Se empieza la busqueda de la frase \"{}\"", busqueda);
            var showMaze = client.get()
                    .uri(searchURL + busqueda)
                    .retrieve()
                    .body(ShowMazeResultado[].class);
            if (Objects.isNull(showMaze) || showMaze.length == 0) {
                return List.of();
            }
            return Arrays.stream(showMaze)
                    .map(ShowMazeResultado::getShow)
                    .map(ShowMaze::toDto)
                    .toList();
        }catch (RestClientResponseException e){
            log.error("Fallo la recuperacion de la informacion por: {}", e.getMessage());
            if(e.getStatusCode().is4xxClientError()) {
                throw new MazeException("No se encontro informacion sobre la cadena \"" + busqueda + "\"", HttpStatus.NOT_FOUND);
            }
            throw new MazeException("Error al consultar TVMaze", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Show informacion(Integer id) throws MazeException {
        try {

            log.debug("Se intenta recuperar la infaormacion para el show con el ID \"{}\"", id);
            var showMaze = client.get()
                    .uri(infoURL + id.toString())
                    .retrieve()
                    .body(ShowMaze.class);
            return showMaze.toDto();
        }catch (RestClientResponseException e){
            log.error("Fallo la recuperacion de la informacion por: {}", e.getMessage());
            if(e.getStatusCode().is4xxClientError()) {
                throw new MazeException("No se encontro informacion sobre el id " + id, HttpStatus.NOT_FOUND);
            }
            throw new MazeException("Error al consultar TVMaze", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
