package com.ghoulrul.tvmaze.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.test.autoconfigure.AutoConfigureMockRestServiceServer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureMockRestServiceServer
class TvMazeIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MockRestServiceServer mockServer;


    @Test
    void getListSucces() throws Exception {
        String serie = "getter";
        var respuesta = loadMockJson("fullList.json");
        mockServer.expect(requestTo("http://api.tvmaze.com/search/shows?q="+serie) )
                .andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess(respuesta, MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/programas/busqueda/" +serie)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(50390))
                .andExpect(jsonPath("$[0].name").value("Getter Robo"))
                .andExpect(jsonPath("$[0].channel").value("Fuji TV"));
    }

    @Test
    void getListEmpty() throws Exception {
        String serie = "Escaflowone";
        mockServer.expect(requestTo("http://api.tvmaze.com/search/shows?q="+serie) )
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/programas/busqueda/" +serie)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(204));
    }

    @Test
    void getListFailServer() throws Exception {
        String serie = "Evangelion";
        mockServer.expect(requestTo("http://api.tvmaze.com/search/shows?q="+serie) )
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServerError());

        mockMvc.perform(get("/programas/busqueda/" +serie)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(500));
    }

    @Test
    void getListError4xx() throws Exception {
        String serie = "serieinexistente";

        mockServer.expect(requestTo("http://api.tvmaze.com/search/shows?q=" + serie))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withForbiddenRequest());

        mockMvc.perform(get("/programas/busqueda/" + serie)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        mockServer.verify();
    }

    @Test
    void getInfoNetwork() throws Exception {
        String id = "51006";
        var respuesta = loadMockJson("network.json");
        mockServer.expect(requestTo("https://api.tvmaze.com/shows/"+id) )
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(respuesta, MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/programas/informacion/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(51006))
                .andExpect(jsonPath("$.name").value("Demon Lord Dante"))
                .andExpect(jsonPath("$.channel").value("AT-X"));
    }

    @Test
    void getInfo404Server() throws Exception {
        String id = "12345";
        mockServer.expect(requestTo("https://api.tvmaze.com/shows/"+id) )
                .andExpect(method(HttpMethod.GET))
                .andRespond(withForbiddenRequest());

        mockMvc.perform(get("/programas/informacion/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));
    }

    @Test
    void getInfoFailServer() throws Exception {
        String id = "12345";
        mockServer.expect(requestTo("https://api.tvmaze.com/shows/"+id) )
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServerError());

        mockMvc.perform(get("/programas/informacion/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(500));
    }

    @Test
    void getInfoError4xx() throws Exception {
        String id = "12345";
        mockServer.expect(requestTo("https://api.tvmaze.com/shows/"+id) )
                .andExpect(method(HttpMethod.GET))
                .andRespond(withForbiddenRequest());

        mockMvc.perform(get("/programas/informacion/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        mockServer.verify();
    }

    private String loadMockJson(String fileName) {
        try {
            var resource = new ClassPathResource("mocks/" + fileName);
            return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo cargar el archivo mock: " + fileName, e);
        }
    }
}