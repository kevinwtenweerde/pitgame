package com.assessment.kevinw.pitgame.helper;

import com.assessment.kevinw.pitgame.domain.Pit;
import com.assessment.kevinw.pitgame.exception.PitretrievalException;

import java.util.List;

public class PitHelper {

    // private constructor that hides the public one
    private PitHelper() {
    }


    public static Pit getPitFromList(List<Pit> pitsToSet, Integer pitToGet) throws PitretrievalException {
        return pitsToSet.stream().filter(
                pit -> pit.getPitId() == pitToGet)
                .findFirst()
                .orElseThrow(() -> new PitretrievalException("The requested pit [" + pitToGet + "] " +
                        "could not be found in the list of pits available." +
                        "\nPlease check the application configuration and  restart the game."));
    }
}
