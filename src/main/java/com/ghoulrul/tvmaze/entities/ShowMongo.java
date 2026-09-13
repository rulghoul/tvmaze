package com.ghoulrul.tvmaze.entities;

import com.ghoulrul.tvmaze.dto.ShowDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "programas")
public class ShowMongo {
    @Id
    private String id;
    private Integer idShow;
    private String name;
    private String channel;
    private String summary;
    private List<String> genres;

    public ShowMongo(ShowDTO showDTO){
        this.idShow = showDTO.getId();
        this.name = showDTO.getName();
        this.channel = showDTO.getChannel();
        this.summary = showDTO.getSummary();
        this.genres = showDTO.getGenres();
    }

    public ShowDTO toDTO(){
        return new ShowDTO(this.idShow, this.name, this.channel, this.summary, this.genres,List.of());
    }
}

