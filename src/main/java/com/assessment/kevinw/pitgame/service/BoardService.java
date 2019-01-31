package com.assessment.kevinw.pitgame.service;

import com.assessment.kevinw.pitgame.domain.Pit;
import com.assessment.kevinw.pitgame.domain.Player;
import com.assessment.kevinw.pitgame.repository.BoardRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    public void move(Player activePlayer, Pit selectedPit) {

    }
}
