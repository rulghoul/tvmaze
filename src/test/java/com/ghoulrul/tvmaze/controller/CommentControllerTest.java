package com.ghoulrul.tvmaze.controller;

import com.ghoulrul.tvmaze.dto.CommentRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Container
    static MongoDBContainer mongoDBContainer =
            new MongoDBContainer(DockerImageName.parse("mongo:7.0.41"));

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add(
                "spring.mongodb.uri",
                mongoDBContainer::getReplicaSetUrl
        );
    }

    @BeforeEach
    void setUp() {
        mongoTemplate.getDb().drop();
    }


    @Test
    void getFromSingleID() throws Exception {
        var idShow = 55802;
        mockMvc.perform(get("/comment/{id}", idShow))
                .andExpect(status().isNoContent());

        //No cumple con rating de 0-5
        var requestFail = new CommentRequest(idShow,"La animacion 3d es de muy mala calidad",6);
        mockMvc.perform(post("/comment" )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(requestFail)))
                .andExpect(status().is5xxServerError());

        var request = new CommentRequest(idShow,"La animacion 3d es de muy mala calidad",3);
        mockMvc.perform(post("/comment" )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comment").value(request.getComment()))
                .andExpect(jsonPath("$.rating").value(request.getRating()));

        mockMvc.perform(get("/comment/{id}", idShow))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].comment").value("La animacion 3d es de muy mala calidad"))
                .andExpect(jsonPath("$[0].rating").value(3));

    }

    @Test
    void saveAndRetrieveMultipleComments() throws Exception {
        var showIds = List.of(51006, 55802);
        mockMvc.perform(post("/comment/multiple" )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(showIds)))
                .andExpect(status().isNoContent());

        var request = new CommentRequest(51006,"Es un show muy bien escrito, con un ritmo impecable y actuaciones de gran calidad",5);
        mockMvc.perform(post("/comment" )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comment").value(request.getComment()))
                .andExpect(jsonPath("$.rating").value(request.getRating()));

        var request2 = new CommentRequest(55802,"La animacion 3d es de muy mala calidad",3);

        mockMvc.perform(post("/comment" )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(request2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comment").value(request2.getComment()))
                .andExpect(jsonPath("$.rating").value(request2.getRating()));


        //Comprueba los valores previamente guardados
        mockMvc.perform(post("/comment/multiple" )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(showIds)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].comment").value(request.getComment()))
                .andExpect(jsonPath("$[0].rating").value(request.getRating()))
                .andExpect(jsonPath("$[1].comment").value(request2.getComment()))
                .andExpect(jsonPath("$[1].rating").value(request2.getRating()));

    }


}