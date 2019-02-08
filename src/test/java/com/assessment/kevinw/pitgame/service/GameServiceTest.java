package com.assessment.kevinw.pitgame.service;

import com.assessment.kevinw.pitgame.domain.Board;
import com.assessment.kevinw.pitgame.domain.GameState;
import com.assessment.kevinw.pitgame.domain.Pit;
import com.assessment.kevinw.pitgame.exception.PitretrievalException;
import com.assessment.kevinw.pitgame.helper.PitHelper;
import com.assessment.kevinw.pitgame.helpers.TestHelper;
import com.assessment.kevinw.pitgame.repository.BoardRepository;
import com.assessment.kevinw.pitgame.repository.PlayerRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

//RunWith to use Mock annotations
@RunWith(MockitoJUnitRunner.class)
public class GameServiceTest {

    @Mock
    BoardRepository boardRepository;

    Board board = new Board();
    GameService gameService;
    BoardService boardService;

    @Mock
    PlayerRepository playerRepository;

    @Before
    public void setup() {
        gameService = new GameService();
        boardService = new BoardService(boardRepository);
        when(playerRepository.findAll()).thenReturn(TestHelper.getPlayers());
        List<Pit> pitsToUse = TestHelper.getPits(12, 6, 2, 0);
        board = Board.builder()
                .boardId(1)
                .pits(pitsToUse)
                .boardLayout(TestHelper.getBoardLayout(pitsToUse))
                .players(playerRepository.findAll())
                .activePlayer(playerRepository.findAll().get(0))
                .build();
        when(boardRepository.findByBoardId(anyInt())).thenReturn(board);
    }

    @Test
    public void whenSmallPitsAreEmptyAndLastPitIsSmallPit_thenGameShouldEnd() throws PitretrievalException {
        // This test will verify the game ending when the amount of stones run out and the last pit hit is a small one

        // Given
        List<Pit> pitsOnBoard = board.getPits();
        pitsOnBoard.stream().forEach(
                pit -> pit.removeStones()
        );

        // When
        GameState gameState = gameService.checkGameState(board);

        assertThat(gameState.isGameOver(), is(true));
    }

    @Test
    public void whenSmallPitsAreEmptyAndLastPitIsBigPit_thenGameShouldEnd() throws PitretrievalException {
        // This test will verify the game ending when the amount of stones run out and the last pit hit is a big one

        // Given
        List<Pit> pitsOnBoard = board.getPits();
        Pit lastPitBeforeBigPit = PitHelper.getPitFromList(pitsOnBoard, 6);
        pitsOnBoard.stream().forEach(
                pit -> pit.removeStones()
        );
        lastPitBeforeBigPit.addStone();

        // When
        boardService.processMove(6);
        GameState gameState = gameService.checkGameState(board);

        assertThat(gameState.isGameOver(), is(true));
        assertThat(gameState.getWinner(), is(board.getActivePlayer()));
        assertThat(gameState.getWinnerScore(), is(1));
    }

    @Test
    public void whenPlayerRunsOutOfStones_thenGameShouldEnd() throws PitretrievalException {
        // This test will verify the game ending when one of the players runs out of stones
        // Given
        List<Pit> pitsOnBoard = board.getPits();
        pitsOnBoard.stream().filter(
                pit -> board.getActivePlayer().getAssignedSmallPits().contains(pit.getPitId())
        ).forEach(Pit::removeStones);

        // When
        GameState gameState = gameService.checkGameState(board);

        // Then
        assertThat(gameState.isGameOver(), is(true));
    }

    @Test
    public void whenLastStoneIsCaptured_thenGameShouldEnd() throws PitretrievalException {
        // Given
        List<Pit> pitsOnBoard = board.getPits();
         /*
          Board layout is:
         +---+---+---+----+----+----+
         | 1 | 2 | 3 | 4  | 5  | 6  |
         +---+---+---+----+----+----+
         | 7 | 8 | 9 | 10 | 11 | 12 |
         +---+---+---+----+----+----+
         All bottom pits except pit 8 will be empty, pit 8 will contain 1 stone.
         Top pits will have 6 stones, except pit 1 and 2. Pit 1 will have 1 stone and pit 2 will have 0 stones.
         When pit one is selected it will end up capturing the stones in pit 8. This will cause the inactive player to have 0 stones, and the game to end.
         */
        Pit pit1 = PitHelper.getPitFromList(pitsOnBoard, 1);
        Pit pit2 = PitHelper.getPitFromList(pitsOnBoard, 2);
        Pit pit8 = PitHelper.getPitFromList(pitsOnBoard, 8);
        pitsOnBoard.stream().filter(
                pit -> board.getInactivePlayer().getAssignedSmallPits().contains(pit.getPitId())
        ).forEach(Pit::removeStones);
        pit1.setAmountOfStonesInPit(1);
        pit2.setAmountOfStonesInPit(0);
        pit8.setAmountOfStonesInPit(1);

        // all stones in small pits
        int expectedWinnerScore = pitsOnBoard.stream().filter(
                pit -> board.getActivePlayer().getAssignedSmallPits().contains(pit.getPitId())
        ).mapToInt(Pit::getAmountOfStonesInPit).sum();
        // all stones in players big pit
        expectedWinnerScore += PitHelper.getPitFromList(pitsOnBoard, board.getActivePlayer().getAssignedBigPit()).getAmountOfStonesInPit();
        // the captured stone from pit 8
        expectedWinnerScore += pit8.getAmountOfStonesInPit();


        // When
        boardService.processMove(1);
        GameState gameState = gameService.checkGameState(board);

        // Then
        assertThat(gameState.getLoserScore(), is(0));
        assertThat(gameState.getWinnerScore(), is(expectedWinnerScore));
        assertThat(gameState.isGameOver(), is(true));
    }

    @Test
    public void whenGameEnds_thenRemainingStonesInSmallPitsGoInBigPit() throws PitretrievalException {
        // Given
        List<Pit> pitsOnBoard = board.getPits();
        pitsOnBoard.stream().filter(
                pit -> board.getInactivePlayer().getAssignedSmallPits().contains(pit.getPitId())
        ).forEach(Pit::removeStones);

        // all stones in small pits
        int expectedWinnerScore = pitsOnBoard.stream().filter(
                pit -> board.getActivePlayer().getAssignedSmallPits().contains(pit.getPitId())
        ).mapToInt(Pit::getAmountOfStonesInPit).sum();
        // all stones in players big pit
        expectedWinnerScore += PitHelper.getPitFromList(pitsOnBoard, board.getActivePlayer().getAssignedBigPit()).getAmountOfStonesInPit();

        // When
        boardService.processMove(1);
        GameState gameState = gameService.checkGameState(board);

        // Then
        assertThat(gameState.isGameOver(), is(true));
        assertThat(gameState.getWinnerScore(), is(expectedWinnerScore));
    }

    @Test
    public void whenGameEnds_playerWithMostStonesInBigPitWins() throws PitretrievalException {
        // Given
        List<Pit> pitsOnBoard = board.getPits();
        Pit inactivePlayerBigPit = PitHelper.getPitFromList(pitsOnBoard, 102);
        // empty all pits
        pitsOnBoard.stream().filter(
                pit -> board.getInactivePlayer().getAssignedSmallPits().contains(pit.getPitId())
        ).forEach(Pit::removeStones);

        // Put stones in big pit
        inactivePlayerBigPit.setAmountOfStonesInPit(37);

        // When
        boardService.processMove(1);
        GameState gameState = gameService.checkGameState(board);

        // Then
        assertThat(gameState.isGameOver(), is(true));
        assertThat(gameState.getWinner(), is(board.getInactivePlayer()));
    }

    @Test
    public void whenPitIsRequestedThatDoesNotExist_thenPREIsCaught() {
        boolean exceptionThrown = false;
        // Given
        board.getActivePlayer().getAssignedSmallPits().add(999);

        // When
        try {
            boardService.processMove(1);
        } catch (PitretrievalException prEx) {
            exceptionThrown = true;
        }

        // Then
        assertThat(exceptionThrown, is(true));
    }
}
