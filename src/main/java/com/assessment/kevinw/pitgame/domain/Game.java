package com.assessment.kevinw.pitgame.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
public class Game {

    @Id
    @GeneratedValue
    private Long id;

    private int scorePlayerOne;
    private int scorePlayerTwo;
}
