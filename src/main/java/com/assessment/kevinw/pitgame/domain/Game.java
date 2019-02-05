package com.assessment.kevinw.pitgame.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Game {

    @Id
    @GeneratedValue
    private Long id;

    // Internal id used to fetch the game from the datasource
    @JsonIgnore
    private int gameId;

    @OneToOne
    private Board board;

    private boolean gameOver;

    private int winnerScore;

    private int loserScore;

    private Player winner;
}
