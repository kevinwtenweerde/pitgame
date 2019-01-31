package com.assessment.kevinw.pitgame.controller;

import com.assessment.kevinw.pitgame.PitgameApplication;
import com.assessment.kevinw.pitgame.domain.Board;
import com.assessment.kevinw.pitgame.domain.Game;
import com.assessment.kevinw.pitgame.domain.Player;
import com.assessment.kevinw.pitgame.repository.BoardRepository;
import com.assessment.kevinw.pitgame.repository.GameRepository;
import com.assessment.kevinw.pitgame.repository.PlayerRepository;
import javafx.application.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameController {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @GetMapping("/")
    public ResponseEntity<Iterable<Board>> getNewGame() {
        return ResponseEntity.ok(boardRepository.findAll());
    }

    @GetMapping("/game")
    public ResponseEntity<Iterable<Game>> getFullGameState() {
        return ResponseEntity.ok(gameRepository.findAll());
    }

    @GetMapping("/board")
    public ResponseEntity<Iterable<Board>> getFullBoardState() {
        return ResponseEntity.ok(boardRepository.findAll());
    }

    @GetMapping("/player")
    public ResponseEntity<Iterable<Player>> getAllPlayers() {
        return ResponseEntity.ok(playerRepository.findAll());
    }

    @GetMapping("new-game")
    public void restartApplication() {
        PitgameApplication.restart();
    }
}
