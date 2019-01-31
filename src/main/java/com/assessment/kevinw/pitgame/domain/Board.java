package com.assessment.kevinw.pitgame.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Board {

    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;

    // Pits are not created in the database, therefor we will cascade them
    @OneToMany(cascade = CascadeType.ALL)
    private List<Pit> pits;

    @OneToMany
    private List<Player> players;

    @OneToOne
    private Player activePlayer;
}
