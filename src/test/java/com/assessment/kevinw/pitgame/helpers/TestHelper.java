package com.assessment.kevinw.pitgame.helpers;

import com.assessment.kevinw.pitgame.domain.DirectionOfPlay;
import com.assessment.kevinw.pitgame.domain.Pit;
import com.assessment.kevinw.pitgame.domain.Player;
import com.assessment.kevinw.pitgame.exception.PitRetrievalException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static com.assessment.kevinw.pitgame.helper.PitHelper.getPitFromList;

public class TestHelper {

    private TestHelper() {
    }

    public static List<Pit> getPits(int amountOfSmallPitsOnBoard, int amountOfStonesInSmallPit, int amountOfBigPitsOnBoard, int amountOfStonesInBigPit) {
        List<Pit> generatedPits = new ArrayList<>();
        IntStream.range(0, amountOfSmallPitsOnBoard)
                .forEach(pitId -> generatedPits.add(Pit.builder()
                        .pitId(1 + pitId)
                        .amountOfStonesInPit(amountOfStonesInSmallPit).build()));
        IntStream.range(0, amountOfBigPitsOnBoard)
                .forEach(pitId -> generatedPits.add(Pit.builder()
                        .pitId(101 + pitId)
                        .amountOfStonesInPit(amountOfStonesInBigPit).build()));
        return generatedPits;
    }

    public static Map<Pit, Pit> getBoardLayout(List<Pit> pitsToSet) {
        // Define board layout
        Map<Integer, Integer> layoutIds = new HashMap<>();
        layoutIds.put(1, 7);
        layoutIds.put(2, 8);
        layoutIds.put(3, 9);
        layoutIds.put(4, 10);
        layoutIds.put(5, 11);
        layoutIds.put(6, 12);
        layoutIds.put(7, 1);
        layoutIds.put(8, 2);
        layoutIds.put(9, 3);
        layoutIds.put(10, 4);
        layoutIds.put(11, 5);
        layoutIds.put(12, 6);

        Map<Pit, Pit> boardLayout = new HashMap<>();
        layoutIds.forEach((key, value) -> {
            try {
                boardLayout.put(
                        getPitFromList(pitsToSet, key),
                        getPitFromList(pitsToSet, value));
            } catch (PitRetrievalException pIex) {
                System.exit(1);
            }
        });
        return boardLayout;
    }

    public static List<Player> getPlayers() {
        Player testPlayer1 = Player.builder()
                .name("testPlayer1")
                .assignedBigPit(101)
                .directionOfPlay(DirectionOfPlay.LEFT)
                .assignedSmallPits(new ArrayList<>(Arrays.asList(6, 5, 4, 3, 2, 1))).build();
        Player testPlayer2 = Player.builder()
                .assignedBigPit(102)
                .name("testPlayer2")
                .directionOfPlay(DirectionOfPlay.RIGHT)
                .assignedSmallPits(new ArrayList<>(Arrays.asList(7, 8, 9, 10, 11, 12))).build();
        return Arrays.asList(testPlayer1, testPlayer2);
    }
}
