package com.ghoulrul.tvmaze.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShowMazeResultado {
    BigDecimal score;
    private ShowMaze show;
}
