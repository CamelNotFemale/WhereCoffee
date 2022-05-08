package com.github.nazzrrg.wherecoffeeapplication.repo;

import com.github.nazzrrg.wherecoffeeapplication.model.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GradeRepository extends JpaRepository<Grade, Long> {

    @Query(value =
            "select * from grades " +
            "where user_id = :userId and cafeteria_id = :cafeteriaId",
            nativeQuery = true)
    Grade findByUserAndCafeIds(Long cafeteriaId, Long userId);

    @Query(value =
            "select" +
                    "       case when count(*) > 0 then 'TRUE' else 'FALSE' end as exist_comment" +
                    "       from grades " +
                    "where cafeteria_id = :cafeId AND user_id = :userId " +
                    "limit 1",
            nativeQuery = true)
    boolean alreadyExistingComment(Long cafeId, Long userId);
}
