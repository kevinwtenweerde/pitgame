package com.assessment.kevinw.pitgame.service;

import com.assessment.kevinw.pitgame.domain.Board;
import com.assessment.kevinw.pitgame.domain.Pit;
import com.assessment.kevinw.pitgame.repository.BoardRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    public Board processMove(int pitId) {
        Board activeBoard = boardRepository.findByBoardId(1);
        List<Pit> pits = activeBoard.getPits();
        pits.stream().filter(
                pit -> pitId == pit.getPitId()
        ).forEach(
                pit -> pit.setAmountOfStonesInPit(0)
        );
        activeBoard.setPits(pits);
        return activeBoard;
    }
}
