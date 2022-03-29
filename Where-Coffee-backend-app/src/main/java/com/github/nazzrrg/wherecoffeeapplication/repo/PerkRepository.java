package com.github.nazzrrg.wherecoffeeapplication.repo;

import com.github.nazzrrg.wherecoffeeapplication.model.EPerk;
import com.github.nazzrrg.wherecoffeeapplication.model.Perk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface PerkRepository extends JpaRepository<Perk, Long> {
    Optional<Perk> findByTitle(EPerk title);
    @Query(value =
            "select" +
            "       case when count(*) > 0 then 'TRUE' else 'FALSE' end as exist_comment" +
            "       from grades " +
            "where cafeteria_id = :cafeId AND user_id = :userId",
            nativeQuery = true)
    boolean alreadyExistingComment(Long cafeId, Long userId);
}
