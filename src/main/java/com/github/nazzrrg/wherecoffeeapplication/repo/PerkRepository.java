package com.github.nazzrrg.wherecoffeeapplication.repo;

import com.github.nazzrrg.wherecoffeeapplication.enumerations.EPerk;
import com.github.nazzrrg.wherecoffeeapplication.model.Perk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface PerkRepository extends JpaRepository<Perk, Long> {
    Optional<Perk> findByTitle(EPerk title);
}
