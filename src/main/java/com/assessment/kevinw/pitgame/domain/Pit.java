package com.assessment.kevinw.pitgame.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pit {

    @Id
    @GeneratedValue
    private Long id;

    private int pitId;

    private int amountOfStonesInPit;

    public void addStone() {
        this.amountOfStonesInPit++;
    }
}
