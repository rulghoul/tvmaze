package com.ghoulrul.tvmaze.entities;

import com.ghoulrul.tvmaze.dto.CommentDTO;
import com.ghoulrul.tvmaze.dto.CommentRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "comentarios")
public class CommentMongo {
    @Id
    private String id;

    @Field(name = "show_id")
    private Integer idShow;

    private String comment;

    private Integer rating;

    public CommentMongo(CommentRequest request){
        this.idShow = request.getId();
        this.comment = request.getComment();
        this.rating = request.getRating();
    }

    public CommentDTO toDto(){
        return new CommentDTO(this.comment, this.rating);
    }

}
