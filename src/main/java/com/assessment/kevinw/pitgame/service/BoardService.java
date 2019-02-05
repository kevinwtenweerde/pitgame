package com.assessment.kevinw.pitgame.service;

import com.assessment.kevinw.pitgame.domain.Board;
import com.assessment.kevinw.pitgame.domain.Pit;
import com.assessment.kevinw.pitgame.domain.Player;
import com.assessment.kevinw.pitgame.exception.PitretrievalException;
import com.assessment.kevinw.pitgame.helper.PitHelper;
import com.assessment.kevinw.pitgame.repository.BoardRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.assessment.kevinw.pitgame.helper.PitHelper.getPitFromList;

@Slf4j
@Service
public class BoardService {

    // This is the board service.
    // This service will manage everything that happens on the board
    // It will not contain any logic for keeping the game state but just execute board movement.

    @Autowired
    private BoardRepository boardRepository;

    // Variable to keep track of what pit got hit last
    private int lastPitHit;

    public Board processMove(int startingPitId) {
        Board activeBoard = boardRepository.findByBoardId(1);
        List<Pit> pits = activeBoard.getPits();

        // Retrieve active player
        Player activePlayer = activeBoard.getActivePlayer();

        // Retrieve selected pit
        Pit selectedPit = getSelectedPit(pits, startingPitId);

        // Retrieve values
        int amountOfPitsToMove = selectedPit.getAmountOfStonesInPit();

        // Move stones
        selectedPit.removeStones();
        pits = moveStones(activePlayer, pits, amountOfPitsToMove, startingPitId);

        // Set new state of pits on board
        activeBoard.setLastPitHit(lastPitHit);
        activeBoard.setPits(pits);
        return activeBoard;
    }

    private Pit getSelectedPit(List<Pit> pits, int pitId) {
        try {
            return PitHelper.getPitFromList(pits, pitId);
        } catch (PitretrievalException pRex) {
            log.error("A pit was selected to move that is not on the board [" + pitId + "]", pRex);
        }
        return null;
    }

    private List<Pit> moveStones(Player activePlayer, List<Pit> pits, int amountOfPitsToMove, int startingPitId) {
        List<Pit> pitsToMoveIn = getPitsToMoveIn(pits, activePlayer);
        for (Pit pit : pits) {
            // Criteria to add a stone to a pit:
            // 1. The pit is present in the list of pits to move in
            // 2. The pit is not the pit that is selected
            // 3. The pit is not before the selected pit (on the initial run)
            if (amountOfPitsToMove != 0
                    && pitsToMoveIn.contains(pit)
                    && startingPitId != pit.getPitId()
                    && startingPitId <= pit.getPitId()) {
                pit.addStone();
                amountOfPitsToMove--;
                lastPitHit = pit.getPitId();
            }
        }
        if (amountOfPitsToMove != 0) {
            // The end of the list is reached but there are still pits to be distributed.
            // The method to move the pits is called again, this time the starting pit is -1 so all pits are applicable
            moveStones(activePlayer, pits, amountOfPitsToMove, -1);
        }
        return pits;
    }

    private List<Pit> getPitsToMoveIn(List<Pit> pits, Player activePlayer) {
        List<Pit> pitsToMoveIn = new ArrayList<>();
        activePlayer.getAssignedSmallPits().stream()
                .forEach(
                        playerPitId -> {
                            try {
                                pitsToMoveIn.add(getPitFromList(pits, playerPitId));
                            } catch (PitretrievalException pRex) {
                                log.error("Player has a small pit assigned that is not on the board [" + playerPitId + "]", pRex);
                            }
                        }
                );
        try {
            pitsToMoveIn.add(getPitFromList(pits, activePlayer.getAssignedBigPit()));
        } catch (PitretrievalException pRex) {
            log.error("Player has a big pit assigned that is not on the board [" + activePlayer.getAssignedBigPit() + "]", pRex);
        }
        return pitsToMoveIn;
    }
}
