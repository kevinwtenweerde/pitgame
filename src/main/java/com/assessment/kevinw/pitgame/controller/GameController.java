package com.assessment.kevinw.pitgame.controller;

import com.assessment.kevinw.pitgame.PitgameApplication;
import com.assessment.kevinw.pitgame.domain.Board;
import com.assessment.kevinw.pitgame.domain.Game;
import com.assessment.kevinw.pitgame.exception.PitretrievalException;
import com.assessment.kevinw.pitgame.repository.BoardRepository;
import com.assessment.kevinw.pitgame.service.BoardService;
import com.assessment.kevinw.pitgame.service.GameService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
@Controller
@AllArgsConstructor
public class GameController {

    //TODO: implement testing - fix restart after 1 time - read up on DI

    /*
    Field injection hides class dependencies. Constructor injection on the other hand exposes them. So it’s enough to look at class API.
    Constructor injection doesn’t allow creation of circular dependencies.
    Constructor injection uses standard Java features to inject dependencies. It is definitely much cleaner than field injection which involves using reflection twice under the hood:
    Spring must use reflection to inject private field
    Mockito (during the test) must use reflection to inject mocks into testing object
    Developer would need to create awful non-default constructor with a lot of parameters for tightly coupled class. Nobody likes huge amount of parameters. So constructor injection naturally forces him to think about decoupling and reducing dependencies for the class. This is biggest advantage of constructor injection for me.
    */

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
        } catch (PitretrievalException prEx) {
            log.error("There was an error while moving the stones along the board.", prEx);
            return "error";
        }
        // Check game state
        Game gameState;
        try {
            gameState = gameService.checkGameState(board);
        } catch (PitretrievalException prEx) {
            log.error("There was an error while fetching the game state.", prEx);
            return "error";
        }

        if (gameState.isGameOver()) {
            model.addAttribute("game", gameState);
            return "game-over";
        }
        boardService.updateActivePlayer(gameState.getActivePlayer());
        model.addAttribute("board", board);
        return "game";
    }

    @GetMapping("/new-game")
    public void restartApplication() {
        PitgameApplication.restart();
    }
}
