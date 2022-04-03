package com.github.nazzrrg.wherecoffeeapplication.repo;

import com.github.nazzrrg.wherecoffeeapplication.model.Cafe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CafeRepository extends PagingAndSortingRepository<Cafe, Long> {
    Optional<Cafe> findById(Long id);
    boolean existsByIdApi(Long idApi);
    @Query(value =
            "select * from cafeterias " +
            "where confirmed = true and point_id in (" +
            "        select id from points" +
            "        where (111.2 * |/( (:lat - lat)^2 + ((:lng - lng)*cos(pi()*:lat/180))^2 ) <= :dist)" +
            "    )",
            nativeQuery = true)
    Page<Cafe> findNearbyCoffeeShops(Double lat, Double lng, Double dist, Pageable pageable);
    @Query(value =
            "select count(*) from cafeterias " +
            "where confirmed = true and point_id in (" +
            "        select id from points" +
            "        where (111.2 * |/( (:lat - lat)^2 + ((:lng - lng)*cos(pi()*:lat/180))^2 ) <= :dist)" +
            "    )",
            nativeQuery = true)
    int countNearbyCoffeeShops(Double lat, Double lng, Double dist);
    @Query(value =
            "select * from cafeterias " +
            "where confirmed = false",
            nativeQuery = true)
    Page<Cafe> findUnconfirmedCoffeeShops(Pageable pageable);
    @Query(value =
            "select count(*) from cafeterias " +
            "where confirmed = false",
            nativeQuery = true)
    int countUnconfirmedCoffeeShops();
}
