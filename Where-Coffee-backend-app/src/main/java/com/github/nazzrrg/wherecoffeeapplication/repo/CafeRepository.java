package com.github.nazzrrg.wherecoffeeapplication.repo;

import com.github.nazzrrg.wherecoffeeapplication.model.Cafe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CafeRepository extends JpaRepository<Cafe, Long> {
    Optional<Cafe> findById(Long id);
    @Query("select c from Cafe c join fetch c.promotions p where c.id = :id and p.toDate > current_date and p.fromDate < current_date")
    Optional<Cafe> findByIdAndRelevantPromotions(Long id);
    boolean existsByIdApi(Long idApi);

    @Query(value =
            "select distinct on (c.id) c.* from cafeterias c " +
            "       left join hours h on c.id = h.cafe_id\n" +
            "       left join cafeterias_perks cp on c.id = cp.cafeteria_id\n" +
            "       left join perks p on p.id = cp.perk_id\n" +
            "where (:minRating = 0.0 or rating >= :minRating) and lower(name) like concat('%',lower(:name),'%') and confirmed = true\n" +
            "   and (not :isOpened or (h.weekday = TRIM(To_Char(now(), 'Day')) and (current_time between h.start_time and h.end_time)))\n" +
            "   and point_id in (\n" +
            "       select id from points\n" +
            "       where (111.2 * |/( (:lat - lat)^2 + ((:lng - lng)*cos(pi()*:lat/180))^2 ) <= :dist)\n" +
            "   )\n" +
            "group by c.id\n" +
            "having char_length(:perks) = 0 or (string_to_array(:perks,',') <@ string_to_array(string_agg(p.title,','), ','))",
            nativeQuery = true)
    Page<Cafe> findNearbyCoffeeShops(Double lat, Double lng, Double dist, Double minRating, String name, String perks, boolean isOpened, Pageable pageable);

    @Query(value =
            "select * from cafeterias " +
            "where confirmed = false",
            nativeQuery = true)
    Page<Cafe> findUnconfirmedCoffeeShops(Pageable pageable);

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
