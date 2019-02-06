package com.assessment.kevinw.pitgame.service;

import com.assessment.kevinw.pitgame.domain.Board;
import com.assessment.kevinw.pitgame.domain.Pit;
import com.assessment.kevinw.pitgame.domain.Player;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

//RunWith to use Mock annotations
@RunWith(MockitoJUnitRunner.class)
public class BoardServiceTest {


    Board board = new Board();
    BoardService boardService;

    @Mock
    PlayerRepository playerRepository;

    @Mock
    BoardRepository boardRepository;

    @Before
    public void setup() {
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
        when(boardRepository.save(any())).thenReturn(board);
        when(boardRepository.findByBoardId(anyInt())).thenReturn(board);
    }

    @Test
    public void whenPlayerUpdated_thenPlayerIsUpdated() {
        // Given
        Player expectedActivePlayerAfterSwitch = board.getInactivePlayer();

        // When
        boardService.updateActivePlayer(expectedActivePlayerAfterSwitch);

        // Then
        assertThat(board.getActivePlayer(), is(expectedActivePlayerAfterSwitch));
    }

    @Test
    public void whenPitIsSelected_thenPitIsEmpty() throws PitretrievalException {
        // Given
        int selectedPitId = 2;
        Pit selectedPit = PitHelper.getPitFromList(board.getPits(), selectedPitId);

        // When
        boardService.processMove(selectedPitId);

        // Then
        assertThat(selectedPit.getAmountOfStonesInPit(), is(0));
    }

    @Test
    public void whenPitIsSelected_thenOtherPitsGetStones() throws PitretrievalException {
        // Pit layout for player1 is 1-2-3-4-5-6-101 where 101 is the big pit.
        // When picking up the 6 stones from pit 1, all other pits, including big pit, should have an extra stone

        // Given
        int startingPit = 1;
        int pitToTest1Id = 2;
        int pitToTest2Id = 5;
        int bigPitToTestId = 101;
        Pit pitToTest1 = PitHelper.getPitFromList(board.getPits(), pitToTest1Id);
        Pit pitToTest2 = PitHelper.getPitFromList(board.getPits(), pitToTest2Id);
        Pit bigPitToTest = PitHelper.getPitFromList(board.getPits(), bigPitToTestId);

        // When
        boardService.processMove(startingPit);

        // Then
        assertThat(pitToTest1.getAmountOfStonesInPit(), is(7));
        assertThat(pitToTest2.getAmountOfStonesInPit(), is(7));
        assertThat(bigPitToTest.getAmountOfStonesInPit(), is(1));
    }

    @Test
    public void whenListOfPitsEndsWithStonesLeft_thenStartOverAtStart() throws PitretrievalException {
        // Pit layout for player1 is 1-2-3-4-5-6-101 where 101 is the big pit.
        // When picking up the 6 stones from pit 1, all other pits, including big pit, should have an extra stone
        // When the layout is at the end but there are still stones left to move this will happen

        // Given
        int startingPitId = 5;
        int pitToTest1Id = 6;
        int pitToTest2Id = 1;
        int bigPitToTestId = 101;
        Pit pitToTest1 = PitHelper.getPitFromList(board.getPits(), pitToTest1Id);
        Pit pitToTest2 = PitHelper.getPitFromList(board.getPits(), pitToTest2Id);
        Pit bigPitToTest = PitHelper.getPitFromList(board.getPits(), bigPitToTestId);

        // When
        boardService.processMove(startingPitId);

        // Then
        assertThat(pitToTest1.getAmountOfStonesInPit(), is(7));
        assertThat(pitToTest2.getAmountOfStonesInPit(), is(7));
        assertThat(bigPitToTest.getAmountOfStonesInPit(), is(1));
    }

    @Test
    public void whenOwnPitIsEmpty_thenCaptureStones() throws PitretrievalException {
        // The opposite pit of of 1 is 7 as defined in the layout. The test will start at pit 2 with 6 stones which will end up in pit 1 with 0 stones.
        // This will cause the stone capture to start and put 6 (pit 7) + 1 (pit 1) + 1 (passed big pit) = 8 stones in big pit 101.

        // Given
        int startingPitId = 2;
        int ownPitThatShouldBeEmptyId = 1;
        int oppositePidThatShouldBeRaidedId = 7;
        int bigPitToTestId = 101;
        Pit pitToEndUpIn = PitHelper.getPitFromList(board.getPits(), ownPitThatShouldBeEmptyId);
        Pit raidedPit = PitHelper.getPitFromList(board.getPits(), oppositePidThatShouldBeRaidedId);
        Pit bigPitToTest = PitHelper.getPitFromList(board.getPits(), bigPitToTestId);
        pitToEndUpIn.setAmountOfStonesInPit(0);

        // When
        boardService.processMove(startingPitId);

        // Then
        assertThat(pitToEndUpIn.getAmountOfStonesInPit(), is(0));
        assertThat(raidedPit.getAmountOfStonesInPit(), is(0));
        assertThat(bigPitToTest.getAmountOfStonesInPit(), is(8));
    }

    @Test(expected = PitretrievalException.class)
    public void whenPitIsRequestedThatDoesNotExist_thenPREIsThrown() throws PitretrievalException {
        // Given
        board.getActivePlayer().getAssignedSmallPits().add(999);

        // When
        boardService.processMove(1);
    }
}
