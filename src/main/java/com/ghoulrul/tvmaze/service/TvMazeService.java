package com.ghoulrul.tvmaze.service;

import com.ghoulrul.tvmaze.dto.ShowDTO;
import com.ghoulrul.tvmaze.entities.ShowMaze;
import com.ghoulrul.tvmaze.entities.ShowMazeResultado;
import com.ghoulrul.tvmaze.entities.ShowMongo;
import com.ghoulrul.tvmaze.exception.MazeException;
import com.ghoulrul.tvmaze.repository.ShowRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
//import org.springframework.retry.support.RetryTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class TvMazeService {

    private final RestClient client;

    private final ShowRepository showRepository;

    @Value("${url.info}")
    private String infoURL;

    @Value("${url.search}")
    private String searchURL;


    @Autowired
    public TvMazeService(RestClient.Builder builder, ShowRepository showRepository){
        this.client = builder
                .build();
        this.showRepository = showRepository;
    }


    public List<ShowDTO> busqueda(String busqueda){
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

    public ShowDTO informacion(Integer id) throws MazeException {
        try {
            //se intenta recuperar el show desde mongo
            var mongoShow = this.getShowFromMongo(id);
            if(mongoShow.isPresent()){
                return mongoShow.get().toDTO();
            }
            log.debug("Se intenta recuperar la infaormacion para el show con el ID \"{}\"", id);
            var showMaze = client.get()
                    .uri(infoURL + id.toString())
                    .retrieve()
                    .body(ShowMaze.class);
            //Guarda el objeto en mongodb a partir de la informacion de la API
            this.saveMongoShow(showMaze);
            return showMaze.toDto();
        }catch (RestClientResponseException e){
            log.error("Fallo la recuperacion de la informacion por: {}", e.getMessage());
            if(e.getStatusCode().is4xxClientError()) {
                throw new MazeException("No se encontro informacion sobre el id " + id, HttpStatus.NOT_FOUND);
            }
            throw new MazeException("Error al consultar TVMaze", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Optional<ShowMongo>  getShowFromMongo(Integer id){
        try {
            return showRepository.findByIdShow(id);
        }catch (Exception e){
            log.info("No se pudo recuperar el show desde Mongo por: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Async
    private void saveMongoShow(ShowMaze showMaze){
        try{
            showRepository.save(new ShowMongo(showMaze.toDto()));
        }catch (Exception e){
            log.error("Fallo el guardado de a mongo por: {}", e.getMessage());
        }
    }
}
