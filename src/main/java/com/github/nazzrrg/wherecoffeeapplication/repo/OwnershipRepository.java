package com.github.nazzrrg.wherecoffeeapplication.repo;

import com.github.nazzrrg.wherecoffeeapplication.model.OwnershipClaim;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OwnershipRepository extends JpaRepository<OwnershipClaim, Long> {

    Page<OwnershipClaim> findAll(Pageable pageable);

    @Query(value =
            "select" +
            "       case when count(*) > 0 then 'TRUE' else 'FALSE' end as exist_claim" +
            "       from ownership_claims " +
            "where cafeteria_id = :cafeId AND user_id = :userId " +
            "limit 1", nativeQuery = true)
    boolean existsByCafeAndUserIds(long cafeId, long userId);
}
