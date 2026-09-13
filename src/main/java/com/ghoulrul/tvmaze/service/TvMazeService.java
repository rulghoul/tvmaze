package com.ghoulrul.tvmaze.service;

import com.ghoulrul.tvmaze.dto.CommentDTO;
import com.ghoulrul.tvmaze.dto.ShowDTO;
import com.ghoulrul.tvmaze.entities.CommentMongo;
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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class TvMazeService {

    private final RestClient client;

    private final ShowRepository showRepository;

    private final CommentService commentService;

    @Value("${url.info}")
    private String infoURL;

    @Value("${url.search}")
    private String searchURL;


    @Autowired
    public TvMazeService(RestClient.Builder builder, ShowRepository showRepository,CommentService commentService){
        this.client = builder
                .build();
        this.showRepository = showRepository;
        this.commentService = commentService;
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
            //recupera los Id's de los shows encontrados
            var ids =Arrays.stream(showMaze)
                    .map(ShowMazeResultado::getShow)
                    .map(ShowMaze::getId)
                    .toList();
            //Recupara los comentarios para los shows encontrados
            var comments = commentService.obtenerPorListaDeShows(ids);
            //Devuelve los ojetos DTO completos junto con sus comentarios
            return Arrays.stream(showMaze)
                    .map(ShowMazeResultado::getShow)
                    .map(ShowMaze::toDto)
                    .map(show -> this.addCommentstoShowDto(show, comments))
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
                var show = mongoShow.get().toDTO();
                return this.addCommentstoShowDto(show);
            }
            log.debug("Se intenta recuperar la infaormacion para el show con el ID \"{}\"", id);
            var showMaze = client.get()
                    .uri(infoURL + id.toString())
                    .retrieve()
                    .body(ShowMaze.class);
            //Guarda el objeto en mongodb a partir de la informacion de la API
            this.saveMongoShow(showMaze);
            var showDto = showMaze.toDto();
            //recupera los comentarios del programa/show
            return this.addCommentstoShowDto(showDto);
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


    public void saveMongoShow(ShowMaze showMaze){
        try{
            showRepository.save(new ShowMongo(showMaze.toDto()));
        }catch (Exception e){
            log.error("Fallo el guardado de a mongo por: {}", e.getMessage());
        }
    }

    //Metodo para agregar comentarios para un show individual
    private ShowDTO addCommentstoShowDto(ShowDTO showDTO){
        //Recupera los comentarios
        var comments = commentService.obtenerPorShow(showDTO.getId())
                .stream().map(CommentMongo::toDto)
                .toList();
        //Agrega los comentarios al ShowDto
        showDTO.setComments(comments);
        return showDTO;
    }

    //Metodo para agregar comentarios en la busqueda de shows
    private ShowDTO addCommentstoShowDto(ShowDTO showDTO, List<CommentMongo> comments){
        List<CommentDTO> comentsShow = comments.stream()
                .filter(coment -> coment.getIdShow().equals(showDTO.getId())) //Filtra solo los comentarios del Show especifico
                .map(CommentMongo::toDto)
                .toList();
        showDTO.setComments(comentsShow);
        return showDTO;
    }
}
