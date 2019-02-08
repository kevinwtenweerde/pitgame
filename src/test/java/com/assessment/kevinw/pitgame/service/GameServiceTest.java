package com.assessment.kevinw.pitgame.service;

import com.assessment.kevinw.pitgame.domain.Board;
import com.assessment.kevinw.pitgame.domain.Pit;
import com.assessment.kevinw.pitgame.exception.PitretrievalException;
import com.assessment.kevinw.pitgame.helpers.TestHelper;
import com.assessment.kevinw.pitgame.repository.PlayerRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.mockito.Mockito.when;

//RunWith to use Mock annotations
@RunWith(MockitoJUnitRunner.class)
public class GameServiceTest {


    Board board = new Board();
    GameService gameService;

    @Mock
    PlayerRepository playerRepository;

    @Before
    public void setup() {
        gameService = new GameService();
        when(playerRepository.findAll()).thenReturn(TestHelper.getPlayers());
        List<Pit> pitsToUse = TestHelper.getPits(12, 6, 2, 0);
        board = Board.builder()
                .boardId(1)
                .pits(pitsToUse)
                .boardLayout(TestHelper.getBoardLayout(pitsToUse))
                .players(playerRepository.findAll())
                .activePlayer(playerRepository.findAll().get(0))
                .build();
    }

    @Test
    public void whenPlayerRunsOutOfStones_thenGameShouldEnd() throws PitretrievalException {
        gameService.checkGameState(board);
    }
}
