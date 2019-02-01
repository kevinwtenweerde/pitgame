package com.assessment.kevinw.pitgame.helper;

import com.assessment.kevinw.pitgame.domain.Pit;
import com.assessment.kevinw.pitgame.exceptions.PitretrievalException;

import java.util.List;

public class PitHelper {

    public static Pit getPitFromList(List<Pit> pitsToSet, Integer pitToGet) throws PitretrievalException {
        return pitsToSet.stream().filter(
                pit -> pit.getPitId() == pitToGet)
                .findFirst()
                .orElseThrow(() -> new PitretrievalException("The requested pit [" + pitToGet + "] " +
                        "could not be found in the list of pits available." +
                        "\nPlease restart the game by accessing /new-game."));
    }
}
