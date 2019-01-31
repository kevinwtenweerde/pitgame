package com.assessment.kevinw.pitgame.repository;

import com.assessment.kevinw.pitgame.domain.Game;
import org.springframework.data.repository.CrudRepository;

public interface GameRepository extends CrudRepository<Game, Long> {

}
