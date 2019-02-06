package com.assessment.kevinw.pitgame.service;

import com.assessment.kevinw.pitgame.domain.Board;
import com.assessment.kevinw.pitgame.domain.Game;
import com.assessment.kevinw.pitgame.domain.Pit;
import com.assessment.kevinw.pitgame.domain.Player;
import com.assessment.kevinw.pitgame.exception.PitretrievalException;
import com.assessment.kevinw.pitgame.repository.GameRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.assessment.kevinw.pitgame.helper.PitHelper.getPitFromList;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameService {

    @NonNull
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
            log.debug("User hit assigned big pit, returning board");
            return activeGame;
        } else {
            if (gameEnded) {
                // Gather the statistics and calculate final scores
                return createFinalGameState(activeBoard);
            }

            // If the player did not land in a big pit and the game did not end change active player
            activeGame.setActivePlayer(activeBoard.getInactivePlayer());
        }
        return activeGame;
    }

    private boolean didGameEnd(Board board) {
        return getTotalAmountOfStonesInPlayersSmallPits(board.getPits(), board.getInactivePlayer()) == 0 ||
                getTotalAmountOfStonesInPlayersSmallPits(board.getPits(), board.getActivePlayer()) == 0;
    }

    private Game createFinalGameState(Board activeBoard) throws PitretrievalException {
        Game finalGameState = Game.builder()
                .gameOver(true)
                .build();
        int activePlayerScore = calculateFinalScore(activeBoard.getPits(), activeBoard.getActivePlayer());
        int inActivePlayerScore = calculateFinalScore(activeBoard.getPits(), activeBoard.getInactivePlayer());
        // There is no draw, active player will always win if score is equal
        if (activePlayerScore >= inActivePlayerScore) {
            finalGameState.setWinner(activeBoard.getActivePlayer());
            finalGameState.setWinnerScore(activePlayerScore);
            finalGameState.setLoserScore(inActivePlayerScore);
        } else {
            finalGameState.setWinner(activeBoard.getInactivePlayer());
            finalGameState.setWinnerScore(inActivePlayerScore);
            finalGameState.setLoserScore(activePlayerScore);
        }
        return finalGameState;
    }

    private int calculateFinalScore(List<Pit> pits, Player player) throws PitretrievalException {
        int smallPits = getTotalAmountOfStonesInPlayersSmallPits(pits, player);
        int bigPits = getPitFromList(pits, player.getAssignedBigPit()).getAmountOfStonesInPit();
        return smallPits + bigPits;
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
}
