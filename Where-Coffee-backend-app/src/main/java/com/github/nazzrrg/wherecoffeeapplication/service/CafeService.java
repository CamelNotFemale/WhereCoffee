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
    public int getPageCount(String location, Double dist) {
        // точка центра поиска
        Double lat = Double.parseDouble(location.split(",")[0]);
        Double lng = Double.parseDouble(location.split(",")[1]);
        int cafeCount = repository.countNearbyCoffeeShops(lat, lng, dist);
        return (int)Math.ceil((double) cafeCount/itemsOnPage);
    }
    public Page<Cafe> getPage(int page, String location, Double dist) {
        Pageable pageable = PageRequest.of(page, itemsOnPage);
        // точка центра поиска
        Double lat = Double.parseDouble(location.split(",")[0]);
        Double lng = Double.parseDouble(location.split(",")[1]);
        return repository.findNearbyCoffeeShops(lat, lng, dist, pageable);
    }

    public ResponseEntity<MessageResponse> addRewiew(Long id, User user, GradeRequest gradeRequest) {
        if (perkRepository.alreadyExistingComment(id, user.getId())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: comment already left!"));
        }

        Cafe cafe = getById(id);

        Set<Perk> perks = new HashSet<>();
        gradeRequest.getPerks().forEach(perk -> {
            switch (perk) {
                case "FREE_WATER":
                    Perk freeWater = perkRepository.findByTitle(EPerk.FREE_WATER)
                            .orElseThrow(() -> new RuntimeException("Error: perk is not found."));
                    perks.add(freeWater);

                    break;
                case "TOILET":
                    Perk toilet = perkRepository.findByTitle(EPerk.TOILET)
                            .orElseThrow(() -> new RuntimeException("Error: perk is not found."));
                    perks.add(toilet);

                    break;
                case "STREET_TERRACE":
                    Perk streetTerrace = perkRepository.findByTitle(EPerk.STREET_TERRACE)
                            .orElseThrow(() -> new RuntimeException("Error: perk is not found."));
                    perks.add(streetTerrace);

                    break;
            }
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
