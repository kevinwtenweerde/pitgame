package com.assessment.kevinw.pitgame.config;

import com.assessment.kevinw.pitgame.domain.Board;
import com.assessment.kevinw.pitgame.domain.Game;
import com.assessment.kevinw.pitgame.domain.Pit;
import com.assessment.kevinw.pitgame.domain.Player;
import com.assessment.kevinw.pitgame.exception.PitretrievalException;
import com.assessment.kevinw.pitgame.repository.BoardRepository;
import com.assessment.kevinw.pitgame.repository.GameRepository;
import com.assessment.kevinw.pitgame.repository.PlayerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static com.assessment.kevinw.pitgame.helper.PitHelper.getPitFromList;

@Component
@Slf4j
public class PlanetInitializer implements ApplicationRunner {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GameRepository gameRepository;

    // Small pit set up
    @Value("${board.amount.of.small.pits}")
    private int amountOfSmallPitsOnBoard;
    @Value("${board.amount.of.stones.in.small.pit}")
    private int amountOfStonesInSmallPit;

    // Big pit set up
    @Value("${board.amount.of.big.pits}")
    private int amountOfBigPitsOnBoard;
    @Value("${board.amount.of.stones.in.big.pit}")
    private int amountOfStonesInBigPit;

    // Player 1 assigned pits
    @Value("${player.1.assigned.small.pits}")
    private List<Integer> playerOneSmallPits;

    @Value("${player.1.assigned.big.pit}")
    private int playerOneBigPit;

    // Player 2 assigned pits
    @Value("${player.2.assigned.small.pits}")
    private List<Integer> playerTwoSmallPits;

    @Value("${player.2.assigned.big.pit}")
    private int playerTwoBigPit;

    @Override
    public void run(ApplicationArguments args) {
        // Initializing the board
        // When adding more pits add them to the layout as well
        // Adding 1 to the pitId to start at one and amount of pits in application.properties correct
        List<Pit> pitsToSet = new ArrayList<>();
        IntStream.range(0, amountOfSmallPitsOnBoard)
                .forEach(pitId -> pitsToSet.add(Pit.builder()
                        .pitId(1 + pitId)
                        .amountOfStonesInPit(amountOfStonesInSmallPit).build()));
        IntStream.range(0, amountOfBigPitsOnBoard)
                .forEach(pitId -> pitsToSet.add(Pit.builder()
                        .pitId(101 + pitId)
                        .amountOfStonesInPit(amountOfStonesInBigPit).build()));

        //Initialize players and assign pits
        Player neilArmstrong = Player.builder()
                .name("Neil Armstrong")
                .assignedBigPit(playerOneBigPit)
                .assignedSmallPits(playerOneSmallPits).build();
        Player chrisHadfield = Player.builder()
                .assignedBigPit(playerTwoBigPit)
                .name("Chris Hadfield")
                .assignedSmallPits(playerTwoSmallPits).build();
        playerRepository.saveAll(Arrays.asList(neilArmstrong, chrisHadfield));
        log.trace("Players initialized with id: [{} : {}] and [{} : {}].", neilArmstrong.getName(), neilArmstrong.getId(), chrisHadfield.getName(), chrisHadfield.getId());

        // Determine the board layout
        Map<Pit, Pit> boardLayout = getBoardLayout(pitsToSet);

        // Build the board
        Board board = Board.builder()
                .boardId(1)
                .pits(pitsToSet)
                .players(playerRepository.findAll())
                .activePlayer(neilArmstrong)
                .boardLayout(boardLayout)
                .build();
        boardRepository.save(board);
        log.trace("Board initialized with id: [{}].", board.getId());

        // Build the game
        Game game = Game.builder()
                .gameId(1)
                .board(board)
                .gameOver(false)
                .activePlayer(board.getActivePlayer())
                .build();
        gameRepository.save(game);
        log.trace("Game initialized with id: [{}].", game.getId());
    }

    private Map<Pit, Pit> getBoardLayout(List<Pit> pitsToSet) {
        /**
         * Board layout will be:
         +---+---+---+----+----+----+
         | 1 | 2 | 3 | 4  | 5  | 6  |
         +---+---+---+----+----+----+
         | 7 | 8 | 9 | 10 | 11 | 12 |
         +---+---+---+----+----+----+
         */
        // Define board layout
        Map<Integer, Integer> layoutIds = new HashMap<>();
        layoutIds.put(1, 7);
        layoutIds.put(2, 8);
        layoutIds.put(3, 9);
        layoutIds.put(4, 10);
        layoutIds.put(5, 11);
        layoutIds.put(6, 12);
        layoutIds.put(7, 1);
        layoutIds.put(8, 2);
        layoutIds.put(9, 3);
        layoutIds.put(10, 4);
        layoutIds.put(11, 5);
        layoutIds.put(12, 6);

        Map<Pit, Pit> boardLayout = new HashMap<>();
        layoutIds.entrySet().stream()
                .forEach(
                        pitPair -> {
                            try {
                                boardLayout.put(
                                        getPitFromList(pitsToSet, pitPair.getKey()),
                                        getPitFromList(pitsToSet, pitPair.getValue()));
                            } catch (PitretrievalException pIex) {
                                log.error("Could not start application because the pits declared in the layout do not exist on the board.", pIex);
                                System.exit(1);
                            }
                        }
                );
        return boardLayout;
    }
}
