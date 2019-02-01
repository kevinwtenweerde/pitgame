package com.assessment.kevinw.pitgame.controller;

import com.assessment.kevinw.pitgame.PitgameApplication;
import com.assessment.kevinw.pitgame.domain.Board;
import com.assessment.kevinw.pitgame.repository.BoardRepository;
import com.assessment.kevinw.pitgame.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class GameController {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private BoardService boardService;

    @GetMapping("/")
    public String getGame(Model model) {
        Board board = boardRepository.findByBoardId(1);
        model.addAttribute("board", board);
        return "game";
    }

    @GetMapping("/board")
    public ResponseEntity<Board> getBoard() {
        return ResponseEntity.ok(boardRepository.findByBoardId(1));
    }

    @GetMapping("/move/{pitId}")
    public String getBoard(Model model, @PathVariable(value = "pitId") final int pitId) {
        Board board = boardService.processMove(pitId);
        boardRepository.save(board);
        model.addAttribute("board", board);
        return "game";
    }

    @GetMapping("/new-game")
    public void restartApplication() {
        PitgameApplication.restart();
    }
}
