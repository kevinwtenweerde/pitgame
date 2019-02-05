package com.assessment.kevinw.pitgame.service;

import com.assessment.kevinw.pitgame.domain.Board;
import com.assessment.kevinw.pitgame.domain.Game;
import com.assessment.kevinw.pitgame.domain.Pit;
import com.assessment.kevinw.pitgame.domain.Player;
import com.assessment.kevinw.pitgame.exception.PitretrievalException;
import com.assessment.kevinw.pitgame.repository.GameRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.assessment.kevinw.pitgame.helper.PitHelper.getPitFromList;

@Slf4j
@Service
public class GameService {

    @Autowired
    private GameRepository gameRepository;

    // This is the Game service
    // This service will have all the logic to determine the game state

    public Game checkGameState(Board board) {
        Game activeGame = gameRepository.findByGameId(1);
        // Check whether or not the active player switches
        // 1. If the active player does not switchs, no stones are captured
        // 2. If the active player does not switch, the game can not end
        if (board.getLastPitHit() == board.getActivePlayer().getAssignedBigPit()) {
            activeGame.setBoard(board);
            log.debug("User hit assigned big pit, returning board");
            return activeGame;
        } else {
            // Check whether or not stones are captured
            captureStones(board);
            // Change active player
            // Check whether or not the game ended
            if (didGameEnd(board)) {
                activeGame.setGameOver(true);
                activeGame.setWinnerScore(getPlayerScore(board.getPits(), board.getActivePlayer()));
                activeGame.setLoserScore(getPlayerScore(board.getPits(), board.getInactivePlayer()));
                activeGame.setBoard(board);
                return activeGame;
            }
            board.setActivePlayer(board.getInactivePlayer());
        }
        activeGame.setBoard(board);
        return activeGame;
    }

    private Board captureStones(Board board) {
        // This method will check what pit got hit last, if this pit is empty, all stones from this pit and the opposite pit will be put in the big pit.
        Pit lastPitHit;
        Pit oppositePit;
        Pit activePlayerBigPit;
        int oppositePitId = getOppositePitId(board);
        try {
            lastPitHit = getPitFromList(board.getPits(), board.getLastPitHit());
            oppositePit = getPitFromList(board.getPits(), oppositePitId);
            activePlayerBigPit = getPitFromList(board.getPits(), board.getActivePlayer().getAssignedBigPit());
        } catch (PitretrievalException prEx) {
            log.error("A pit was requested that does not exist " + board.getLastPitHit() + "]", prEx);
            return board;
        }
        // Criteria to steal pits
        // 1. The last pit hit has exactly 1 stone in it
        // When stealing both pits are emptied and all the stones are put in to the big pit of the player
        if (lastPitHit.getAmountOfStonesInPit() == 1) {
            int spoilsOfWar = lastPitHit.getAmountOfStonesInPit() + oppositePit.getAmountOfStonesInPit();
            lastPitHit.removeStones();
            oppositePit.removeStones();
            activePlayerBigPit.addStones(spoilsOfWar);
        }
        return board;
    }

    private int getOppositePitId(Board board) {
        return board.getBoardLayout().entrySet().stream()
                .filter(pitId -> pitId.getKey().getPitId() == board.getLastPitHit())
                .findFirst()
                .get()
                .getValue()
                .getPitId();
    }

    private boolean didGameEnd(Board board) {
        return getPlayerScore(board.getPits(), board.getInactivePlayer()) == 0 ||
                getPlayerScore(board.getPits(), board.getActivePlayer()) == 0;
    }

    private int getPlayerScore(List<Pit> pits, Player player) {
        return player
                .getAssignedSmallPits()
                .stream()
                .mapToInt(
                        pitId -> {
                            try {
                                return getPitFromList(pits, pitId).getAmountOfStonesInPit();
                            } catch (PitretrievalException prEx) {
                                log.error("A pit was requested that does not exist " + pitId + "]", prEx);
                                return 0;
                            }
                        }
                )
                .sum();
    }
}
