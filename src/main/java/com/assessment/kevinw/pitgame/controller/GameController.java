package com.assessment.kevinw.pitgame.controller;

import com.assessment.kevinw.pitgame.PitgameApplication;
import com.assessment.kevinw.pitgame.domain.Board;
import com.assessment.kevinw.pitgame.domain.Game;
import com.assessment.kevinw.pitgame.repository.BoardRepository;
import com.assessment.kevinw.pitgame.service.BoardService;
import com.assessment.kevinw.pitgame.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class GameController {


    //TODO: Check where to throw and where to catch exceptions
    //TODO: Both active and inactive player can win
    //TODO: Correct calculation of score

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
        Board board = boardService.processMove(pitId);
        Game gameState = gameService.checkGameState(board);
        board = gameState.getBoard();
        boardRepository.save(board);
        model.addAttribute("board", board);
        if (gameState.isGameOver()) {
            model.addAttribute("game", gameState);
            return "game-over";
        }
        return "game";
    }

    @GetMapping("/new-game")
    public void restartApplication() {
        PitgameApplication.restart();
    }
}
