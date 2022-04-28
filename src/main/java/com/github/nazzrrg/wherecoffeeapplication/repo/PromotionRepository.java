package com.github.nazzrrg.wherecoffeeapplication.repo;

import com.github.nazzrrg.wherecoffeeapplication.model.Promotion;
import com.github.nazzrrg.wherecoffeeapplication.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    @Modifying
    @Query(value =
            "delete from cafeterias_promotions " +
            "where promotion_id = :promoId", nativeQuery = true)
    int deletePromotionLinksByPromoId(Long promoId);
    Page<Promotion> findAllByUser(User user, Pageable pageable);

    @Modifying
    @Transactional
    @Query(value =
            "set TIMEZONE = 'Europe/Moscow'; " +
            "delete from cafeterias_promotions " +
            "where promotion_id = ( " +
            "        select id from promotions " +
            "        where to_date < NOW() " +
            "    ); ", nativeQuery = true)
    int deleteIrrelevantPromotions();
}
