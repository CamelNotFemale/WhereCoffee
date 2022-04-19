package com.github.nazzrrg.wherecoffeeapplication.repo;

import com.github.nazzrrg.wherecoffeeapplication.model.Cafe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CafeRepository extends JpaRepository<Cafe, Long> {
    Optional<Cafe> findById(Long id);
    boolean existsByIdApi(Long idApi);
    // заменить с native на JPQL? по двум совпадающем перкам возвращает две одинаковые кофейни
    @Query(value =
            "select distinct on (c.id) c.* from cafeterias c left join cafeterias_perks cp on c.id = cp.cafeteria_id left join perks p on p.id = cp.perk_id\n" +
            "where (:minRating = 0.0 or rating >= :minRating) and lower(name) like concat('%',lower(:name),'%') and confirmed = true\n" +
            "   and point_id in (\n" +
            "       select id from points\n" +
            "       where (111.2 * |/( (:lat - lat)^2 + ((:lng - lng)*cos(pi()*:lat/180))^2 ) <= :dist)\n" +
            "   )\n" +
            "GROUP BY c.id\n" +
            "HAVING coalesce(:perks) is null or string_to_array(:perks,',') && string_to_array(string_agg(p.title,','), ',')",
            nativeQuery = true)
    Page<Cafe> findNearbyCoffeeShops(Double lat, Double lng, Double dist, Double minRating, String name, List<String> perks, Pageable pageable);
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
    @Query(value =
            "select * from cafeterias " +
            "where manager = :managerId",
            nativeQuery = true)
    Page<Cafe> findManagedCoffeeShops(long managerId, Pageable pageable);
    /*@Query(value =
            "select " +
            "    case when count(*) > 0 then 'TRUE' else 'FALSE' end as is_manager " +
            "from cafeterias " +
            "where id = :cafeId AND manager = :userId " +
            "limit 1",
            nativeQuery = true)
    boolean isACafeteriaManager(Long cafeId, Long userId);*/
}
