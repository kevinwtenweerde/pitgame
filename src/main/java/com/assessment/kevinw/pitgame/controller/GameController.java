package com.assessment.kevinw.pitgame.controller;

import com.assessment.kevinw.pitgame.PitgameApplication;
import com.assessment.kevinw.pitgame.domain.Board;
import com.assessment.kevinw.pitgame.domain.Game;
import com.assessment.kevinw.pitgame.exception.PitretrievalException;
import com.assessment.kevinw.pitgame.repository.BoardRepository;
import com.assessment.kevinw.pitgame.service.BoardService;
import com.assessment.kevinw.pitgame.service.GameService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
@Controller
public class GameController {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private BoardService boardService;

    @Autowired
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
        } catch (PitretrievalException prEx) {
            log.error("There was an error while moving the stones along the board.", prEx);
            return "error";
        }
        boardRepository.save(board);

        // Check game state
        Game gameState;
        try {
            gameState = gameService.checkGameState(board);
        } catch (PitretrievalException prEx) {
            log.error("There was an error while determing the game state.", prEx);
            return "error";
        }

        if (gameState.isGameOver()) {
            model.addAttribute("game", gameState);
            return "game-over";
        }
        board = gameState.getBoard();
        boardRepository.save(board);
        model.addAttribute("board", board);
        return "game";
    }

    @GetMapping("/new-game")
    public void restartApplication() {
        PitgameApplication.restart();
    }
}
