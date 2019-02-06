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
    public Game checkGameState(Board activeBoard) throws PitretrievalException {
        Game activeGame = gameRepository.findByGameId(1);

        // Check whether or not the game ended after just moving stones on the board
        boolean gameEnded = didGameEnd(activeBoard);

        // Check whether or not the active player switches
        // 1. If the active player does not switch, no stones are captured
        // 2. If the last pit on the players board is put in a big pit, the game should also end
        if (activeBoard.getLastPitHit() == activeBoard.getActivePlayer().getAssignedBigPit() && !gameEnded) {
            activeGame.setBoard(activeBoard);
            log.debug("User hit assigned big pit, returning board");
            return activeGame;
        } else {
            if (gameEnded) {
                // Gather the statistics and calculate final scores
                activeGame = finalizeGame(activeBoard, activeGame);
                return activeGame;
            }

            // If the player did not land in a big pit and the game did not end change active player
            activeGame.setActivePlayer(activeBoard.getInactivePlayer());
        }
        activeGame.setBoard(activeBoard);
        return activeGame;
    }

    private boolean didGameEnd(Board board) {
        return getTotalAmountOfStonesInPlayersSmallPits(board.getPits(), board.getInactivePlayer()) == 0 ||
                getTotalAmountOfStonesInPlayersSmallPits(board.getPits(), board.getActivePlayer()) == 0;
    }

    private int getTotalAmountOfStonesInPlayersSmallPits(List<Pit> pits, Player player) {
        return player
                .getAssignedSmallPits()
                .stream()
                .mapToInt(
                        pitId -> {
                            try {
                                return getPitFromList(pits, pitId).getAmountOfStonesInPit();
                            } catch (PitretrievalException prEx) {
                                log.error("The score of a pit that does not exist was requested: [" + pitId + "]. 0 will be returned.", prEx);
                                return 0;
                            }
                        }
                )
                .sum();
    }

    private Game finalizeGame(Board activeBoard, Game activeGame) throws PitretrievalException {
        activeGame.setGameOver(true);
        int activePlayerScore = calculateFinalScore(activeBoard.getPits(), activeBoard.getActivePlayer());
        int inActivePlayerScore = calculateFinalScore(activeBoard.getPits(), activeBoard.getInactivePlayer());
        // There is no draw, active player will always win if score is equal
        if (activePlayerScore >= inActivePlayerScore) {
            activeGame.setWinner(activeBoard.getActivePlayer());
            activeGame.setWinnerScore(activePlayerScore);
            activeGame.setLoserScore(inActivePlayerScore);
        } else {
            activeGame.setWinner(activeBoard.getInactivePlayer());
            activeGame.setWinnerScore(inActivePlayerScore);
            activeGame.setLoserScore(activePlayerScore);
        }
        activeGame.setBoard(activeBoard);
        return activeGame;
    }

    private int calculateFinalScore(List<Pit> pits, Player player) throws PitretrievalException {
        int smallPits = getTotalAmountOfStonesInPlayersSmallPits(pits, player);
        int bigPits = getPitFromList(pits, player.getAssignedBigPit()).getAmountOfStonesInPit();
        return smallPits + bigPits;
    }
}
