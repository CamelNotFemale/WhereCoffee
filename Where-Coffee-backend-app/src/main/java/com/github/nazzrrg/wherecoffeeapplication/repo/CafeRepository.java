package com.github.nazzrrg.wherecoffeeapplication.repo;

import com.github.nazzrrg.wherecoffeeapplication.model.Cafe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CafeRepository extends PagingAndSortingRepository<Cafe, Long> {
    public Optional<Cafe> findById(Long id);
    public boolean existsByIdApi(Long idApi);
}
