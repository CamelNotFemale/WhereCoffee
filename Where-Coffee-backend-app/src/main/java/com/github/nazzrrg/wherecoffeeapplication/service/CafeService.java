package com.github.nazzrrg.wherecoffeeapplication.service;

import com.github.nazzrrg.wherecoffeeapplication.model.*;
import com.github.nazzrrg.wherecoffeeapplication.payload.request.GradeRequest;
import com.github.nazzrrg.wherecoffeeapplication.payload.response.MessageResponse;
import com.github.nazzrrg.wherecoffeeapplication.repo.CafeRepository;
import com.github.nazzrrg.wherecoffeeapplication.repo.PerkRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CafeService {
    @Value("${netcracker.app.itemsOnPage}")
    private int itemsOnPage;
    private final CafeRepository repository;
    private final PerkRepository perkRepository;

    public CafeService(CafeRepository repository, PerkRepository perkRepository) {
        this.repository = repository;
        this.perkRepository = perkRepository;
    }

    public boolean create(Cafe cafe) {
        if (!(cafe.getIdApi() != null && repository.existsByIdApi(cafe.getIdApi()))) {
            repository.save(cafe);
            return true;
        }
        return false;
    }
    public Cafe getById(long id) {
        return repository.findById(id).orElseThrow(RuntimeException::new);
    }
    public void delete(long id) {
        repository.deleteById(id);
    }

    public Page<Cafe> getPage(int page) {
        Pageable pageable = PageRequest.of(page, itemsOnPage);
        return repository.findAll(pageable);
    }
    public int getPageCount(String location, Double dist, boolean confirmed) {
        if (confirmed) {
            // точка центра поиска
            Double lat = Double.parseDouble(location.split(",")[0]);
            Double lng = Double.parseDouble(location.split(",")[1]);
            int cafeCount = repository.countNearbyCoffeeShops(lat, lng, dist);
            return (int)Math.ceil((double) cafeCount/itemsOnPage);
        }
        else {
            int cafeCount = repository.countUnconfirmedCoffeeShops();
            return (int)Math.ceil((double) cafeCount/itemsOnPage);
        }
    }
    public Page<Cafe> getPage(int page, String location, Double dist, boolean confirmed) {
        Pageable pageable = PageRequest.of(page, itemsOnPage);
        if (confirmed) {
            // точка центра поиска
            Double lat = Double.parseDouble(location.split(",")[0]);
            Double lng = Double.parseDouble(location.split(",")[1]);
            return repository.findNearbyCoffeeShops(lat, lng, dist, pageable);
        }
        else {
            return repository.findUnconfirmedCoffeeShops(pageable);
        }
    }

    public ResponseEntity<MessageResponse> addRewiew(Long id, User user, GradeRequest gradeRequest) {
        if (perkRepository.alreadyExistingComment(id, user.getId())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: comment already left!"));
        }

        Cafe cafe = getById(id);

        Set<Perk> perks = new HashSet<>();
        gradeRequest.getPerks().forEach(perkStr -> {
            Perk perk = perkRepository.findByTitle(EPerk.valueOf(perkStr))
                    .orElseThrow(() -> new RuntimeException("Error: perk is not found."));
            perks.add(perk);
        });

        Grade grade = new Grade(gradeRequest.getComment(),
                                gradeRequest.getGrade(),
                                perks,
                                user
                                );
        cafe.getGrades().add(grade);
        repository.save(cafe);

        return ResponseEntity
                .created(URI.create("http://localhost/cafeterias/"+id))
                .body(new MessageResponse("Grade added successfully"));
    }
}
