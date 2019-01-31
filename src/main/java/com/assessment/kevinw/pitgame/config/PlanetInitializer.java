package com.assessment.kevinw.pitgame.config;

import com.assessment.kevinw.pitgame.domain.Board;
import com.assessment.kevinw.pitgame.domain.Pit;
import com.assessment.kevinw.pitgame.domain.Player;
import com.assessment.kevinw.pitgame.repository.BoardRepository;
import com.assessment.kevinw.pitgame.repository.PlayerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

@Component
@Slf4j
public class PlanetInitializer implements ApplicationRunner {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private PlayerRepository playerRepository;

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
        // Adding 1 to the pitId to start at one and amount of pits in application.properties correct
        List<Pit> pitsToSet = new ArrayList<>();
        IntStream.range(0, amountOfSmallPitsOnBoard)
                .forEach(pitId -> pitsToSet.add(new Pit(1 + pitId, amountOfStonesInSmallPit)));
        IntStream.range(0, amountOfBigPitsOnBoard)
                .forEach(pitId -> pitsToSet.add(new Pit(101 + pitId, amountOfStonesInBigPit)));

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

        // Build the board
        Board board = Board.builder()
                .pits(pitsToSet)
                .players(playerRepository.findAll())
                .activePlayer(neilArmstrong).build();
        boardRepository.save(board);
        log.trace("Board initialized with id: [{}].", board.getId());
    }
}
