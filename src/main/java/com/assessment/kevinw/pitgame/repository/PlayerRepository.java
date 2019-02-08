package com.assessment.kevinw.pitgame.repository;

import com.assessment.kevinw.pitgame.domain.Player;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PlayerRepository extends CrudRepository<Player, Long> {

    List<Player> findAll();
}
