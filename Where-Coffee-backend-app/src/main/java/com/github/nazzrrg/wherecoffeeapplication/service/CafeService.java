package com.github.nazzrrg.wherecoffeeapplication.service;

import com.github.nazzrrg.wherecoffeeapplication.model.*;
import com.github.nazzrrg.wherecoffeeapplication.payload.request.CafeRequest;
import com.github.nazzrrg.wherecoffeeapplication.payload.request.GradeRequest;
import com.github.nazzrrg.wherecoffeeapplication.payload.response.MessageResponse;
import com.github.nazzrrg.wherecoffeeapplication.repo.CafeRepository;
import com.github.nazzrrg.wherecoffeeapplication.repo.GradeRepository;
import com.github.nazzrrg.wherecoffeeapplication.repo.PerkRepository;
import com.github.nazzrrg.wherecoffeeapplication.utils.DTOMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CafeService {
    @Value("${netcracker.app.itemsOnPage}")
    private int itemsOnPage;
    private final CafeRepository repository;
    private final PerkRepository perkRepository;
    private final GradeRepository gradeRepository;
    private final DTOMapper mapper;

    public CafeService(CafeRepository repository, PerkRepository perkRepository,
                       GradeRepository gradeRepository, DTOMapper mapper) {
        this.repository = repository;
        this.perkRepository = perkRepository;
        this.gradeRepository = gradeRepository;
        this.mapper = mapper;
    }
    /** создание кофеен из API */
    public boolean create(Cafe cafe) {
        if (!(cafe.getIdApi() != null && repository.existsByIdApi(cafe.getIdApi()))) {
            repository.save(cafe);
            return true;
        }
        return false;
    }
    /** создание собственных кофеен */
    public void create(CafeRequest cafeRequest) {
        Cafe cafe = mapper.toCafe(cafeRequest);
        repository.save(cafe);
    }
    public Cafe getById(long id) {
        return repository.findById(id).orElseThrow(RuntimeException::new);
    }
    public void update(long id, CafeRequest cafeRequest) {
        Cafe cafeToBeUpdated = getById(id);
        cafeToBeUpdated = mapper.fillCafeFromDTO(cafeToBeUpdated, cafeRequest);
        repository.save(cafeToBeUpdated);
    }
    public void confirm(long id) {
        Cafe cafe = getById(id);
        cafe.setConfirmed(true);
        repository.save(cafe);
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
        if (gradeRepository.alreadyExistingComment(id, user.getId())) {
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
    @PreAuthorize("#userId == authentication.principal.id")
    public ResponseEntity<MessageResponse> updateRewiew(Long id, Long userId, GradeRequest gradeRequest) {
        Grade grade = gradeRepository.findByUserAndCafeIds(id, userId);
        Set<Perk> perks = new HashSet<>();
        gradeRequest.getPerks().forEach(perkStr -> {
            Perk perk = perkRepository.findByTitle(EPerk.valueOf(perkStr))
                    .orElseThrow(() -> new RuntimeException("Error: perk is not found."));
            perks.add(perk);
        });
        grade.setGrade(gradeRequest.getGrade());
        grade.setComment(gradeRequest.getComment());
        grade.setPerks(perks);
        grade.setDate(new Date());

        gradeRepository.save(grade);
        return ResponseEntity
                .ok()
                .body(new MessageResponse("Grade updated successfully"));
    }
    @PreAuthorize("#userId == authentication.principal.id")
    public ResponseEntity<MessageResponse> deleteRewiew(Long id, Long userId) {
        Grade grade = gradeRepository.findByUserAndCafeIds(id, userId);
        gradeRepository.deleteById(grade.getId());

        return ResponseEntity
                .ok()
                .body(new MessageResponse("Grade deleted successfully"));
    }
}
