package com.assessment.kevinw.pitgame.controller;

import com.assessment.kevinw.pitgame.domain.Board;
import com.assessment.kevinw.pitgame.domain.GameState;
import com.assessment.kevinw.pitgame.exception.PitRetrievalException;
import com.assessment.kevinw.pitgame.repository.BoardRepository;
import com.assessment.kevinw.pitgame.service.BoardService;
import com.assessment.kevinw.pitgame.service.GameService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.devtools.restart.Restarter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@Controller
@AllArgsConstructor
public class GameController {

    @NonNull
    private BoardRepository boardRepository;

    @NonNull
    private BoardService boardService;

    @NonNull
    private GameService gameService;

    @GetMapping("/")
    public String getGame(Model model) {
        Board board = boardRepository.findByBoardId(1);
        model.addAttribute("board", board);
        return "game";
    }

    @GetMapping("/move/{pitId}")
    public String processMove(Model model, @PathVariable(value = "pitId") final int pitId) {
        // Update board
        Board board;
        try {
            board = boardService.processMove(pitId);
        } catch (PitRetrievalException prEx) {
            log.error("There was an error while moving the stones along the board.", prEx);
            return "game-crashed";
        } catch (IllegalArgumentException iaEx) {
            log.debug("User tried to empty a pit that does not belong to it");
            return "input-error";
        }
        // Check game state
        GameState gameState;
        try {
            gameState = gameService.checkGameState(board);
        } catch (PitRetrievalException prEx) {
            log.error("There was an error while fetching the game state.", prEx);
            return "game-crashed";
        }

        if (gameState.isGameOver()) {
            model.addAttribute("game", gameState);
            return "game-finished";
        }
        boardService.updateActivePlayer(gameState.getActivePlayer());
        model.addAttribute("board", board);
        return "game";
    }

    @GetMapping("/new-game")
    public void restartApplication() {
        Restarter.getInstance().restart();
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public String handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException matmEx, WebRequest request, Model model) {
        String errorMessage = "User entered [" + matmEx.getName() + "] while [int] is required.";
        log.debug("Invalid input recieved on []. Error is: {}", this.getClass(), errorMessage);
        return "input-error";

    }
}
