package com.assessment.kevinw.pitgame.service;

import com.assessment.kevinw.pitgame.domain.Board;
import com.assessment.kevinw.pitgame.domain.Pit;
import com.assessment.kevinw.pitgame.domain.Player;
import com.assessment.kevinw.pitgame.exception.PitretrievalException;
import com.assessment.kevinw.pitgame.repository.BoardRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.assessment.kevinw.pitgame.helper.PitHelper.getPitFromList;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

    // This is the board service.
    // This service will manage everything that happens on the board
    // It will not contain any logic for keeping the game state but just execute board movement and capturing.

    @NonNull
    private BoardRepository boardRepository;

    // Variable to keep track of what pit got hit last
    private int lastPitHitId;

    public Board processMove(int startingPitId) throws PitretrievalException {
        // Retrieve game board and pits in current state
        Board activeBoard = boardRepository.findByBoardId(1);
        List<Pit> pits = activeBoard.getPits();

        // Retrieve active player
        Player activePlayer = activeBoard.getActivePlayer();

        // Prevent moving of inactive players stones
        if (!activePlayer.getAssignedSmallPits().contains(startingPitId)) {
            throw new IllegalArgumentException("The pit value [" + startingPitId + "] is not a pit that belongs to the active user");
        }
        // Retrieve selected pit
        Pit selectedPit = getPitFromList(pits, startingPitId);

        // Retrieve values
        int amountOfPitsToMove = selectedPit.getAmountOfStonesInPit();

        // Move stones
        selectedPit.removeStones();
        List<Pit> pitsAfterMoving = moveStones(activePlayer, pits, amountOfPitsToMove, startingPitId);

        // Set new state of pits on board
        activeBoard.setLastPitHit(lastPitHitId);
        activeBoard.setPits(pitsAfterMoving);

        // Capture stones only when the last pit is no big pit
        if (lastPitHitId != activePlayer.getAssignedBigPit()) {
            captureStones(activeBoard);
        }
        boardRepository.save(activeBoard);
        return activeBoard;
    }

    private List<Pit> moveStones(Player activePlayer, List<Pit> pits, int amountOfPitsToMove, int startingPitId) throws PitretrievalException {
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
                lastPitHitId = pit.getPitId();
            }
        }
        if (amountOfPitsToMove != 0) {
            // The end of the list is reached but there are still pits to be distributed.
            // The method to move the pits is called again, this time the starting pit is -1 so all pits are applicable
            moveStones(activePlayer, pits, amountOfPitsToMove, -1);
        }
        return pits;
    }

    private List<Pit> getPitsToMoveIn(List<Pit> pits, Player activePlayer) throws PitretrievalException {
        List<Pit> pitsToMoveIn = new ArrayList<>();
        List<Integer> pitIds = activePlayer.getAssignedSmallPits();
        for (Integer playerPitId : pitIds) {
            pitsToMoveIn.add(getPitFromList(pits, playerPitId));
        }
        pitsToMoveIn.add(getPitFromList(pits, activePlayer.getAssignedBigPit()));
        return pitsToMoveIn;
    }

    private Board captureStones(Board board) throws PitretrievalException {
        // This method will check what pit got hit last, if this pit is empty, all stones from this pit and the opposite pit will be put in the big pit.
        int oppositePitId = getOppositePitId(board);
        Pit lastPitHit = getPitFromList(board.getPits(), board.getLastPitHit());
        Pit oppositePit = getPitFromList(board.getPits(), oppositePitId);
        Pit activePlayerBigPit = getPitFromList(board.getPits(), board.getActivePlayer().getAssignedBigPit());

        // When stealing both pits are emptied and all the stones are put in to the big pit of the player
        // Criteria to steal pits
        // 1. The last pit hit has exactly 1 stone in it
        if (lastPitHit.getAmountOfStonesInPit() == 1) {
            int spoilsOfWar = lastPitHit.getAmountOfStonesInPit() + oppositePit.getAmountOfStonesInPit();
            lastPitHit.removeStones();
            oppositePit.removeStones();
            activePlayerBigPit.addStones(spoilsOfWar);
        }
        return board;
    }

    private int getOppositePitId(Board board) throws PitretrievalException {
        return board.getBoardLayout().entrySet().stream()
                .filter(pitId -> pitId.getKey().getPitId() == board.getLastPitHit())
                .findFirst()
                .orElseThrow(() -> new PitretrievalException("No opposite pit found for pit, please check the configuration and make sure all pits have opposites."))
                .getValue()
                .getPitId();
    }

    public void updateActivePlayer(Player activePlayer) {
        // Retrieve game board and pits in current state
        Board activeBoard = boardRepository.findByBoardId(1);
        activeBoard.setActivePlayer(activePlayer);
        boardRepository.save(activeBoard);
    }
}
