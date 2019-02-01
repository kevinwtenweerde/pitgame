package com.assessment.kevinw.pitgame.service;

import com.assessment.kevinw.pitgame.domain.Board;
import com.assessment.kevinw.pitgame.domain.Pit;
import com.assessment.kevinw.pitgame.domain.Player;
import com.assessment.kevinw.pitgame.exceptions.PitretrievalException;
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

    @Autowired
    private BoardRepository boardRepository;

    public Board processMove(int pitId) {
        Board activeBoard = boardRepository.findByBoardId(1);
        List<Pit> pits = activeBoard.getPits();

        Player activePlayer = activeBoard.getActivePlayer();
        List<Pit> pitsToMoveIn = getPitsToMoveIn(pits, activePlayer);

        System.out.println(pitsToMoveIn);

        // Retrieve pits to move
        int amountOfPitsToMove;
        try {
            amountOfPitsToMove = getPitFromList(pits, pitId).getAmountOfStonesInPit();
        } catch (PitretrievalException pRex) {
            log.error("Error while handling pit [" + pitId + "].", pRex);
        }

        


        // Move pits around
        pits.stream().filter(
                pit -> pitId == pit.getPitId()
        ).forEach(
                pit -> pit.setAmountOfStonesInPit(0)
        );

        // Set new state of pits on board
        activeBoard.setPits(pits);
        return activeBoard;
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
