package com.assessment.kevinw.pitgame.service;

import com.assessment.kevinw.pitgame.domain.Board;
import com.assessment.kevinw.pitgame.domain.Pit;
import com.assessment.kevinw.pitgame.domain.Player;
import com.assessment.kevinw.pitgame.exceptions.PitretrievalException;
import com.assessment.kevinw.pitgame.helper.PitHelper;
import com.assessment.kevinw.pitgame.repository.BoardRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.IntStream;

import static com.assessment.kevinw.pitgame.helper.PitHelper.getPitFromList;

@Slf4j
@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    public Board processMove(int pitId) {
        Board activeBoard = boardRepository.findByBoardId(1);
        List<Pit> pits = activeBoard.getPits();

        // Retrieve active player
        Player activePlayer = activeBoard.getActivePlayer();

        // Retrieve selected pit
        Pit selectedPit = getSelectedPit(pits, pitId);

        // Retrieve values
        List<Pit> pitsToMoveIn = getPitsToMoveIn(pits, activePlayer);
        System.out.println(pitsToMoveIn);
        int amountOfPitsToMove = selectedPit.getAmountOfStonesInPit();
        
        // Move stones
        while (amountOfPitsToMove != 0) {
            //todo: implement
            amountOfPitsToMove--;
        }

        // Set new state of pits on board
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
