package com.ghoulrul.tvmaze.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Show {
    private Integer id;
    private String name;
    private String channel;
    private String summary;
    private List<String> generes;
}
