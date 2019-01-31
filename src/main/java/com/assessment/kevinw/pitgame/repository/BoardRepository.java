package com.assessment.kevinw.pitgame.repository;

import com.assessment.kevinw.pitgame.domain.Board;
import org.springframework.data.repository.CrudRepository;

public interface BoardRepository extends CrudRepository<Board, Long> {

    Board findByBoardId(int i);
}
