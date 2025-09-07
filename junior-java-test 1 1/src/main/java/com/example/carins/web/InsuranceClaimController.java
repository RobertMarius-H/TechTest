package com.example.carins.web;

import com.example.carins.model.Car;
import com.example.carins.model.InsuranceClaim;
import com.example.carins.repo.InsuranceClaimRepository;
import com.example.carins.service.CarService;
import com.example.carins.service.InsurancePolicyService;
import com.example.carins.web.dto.ClaimDto;
import com.example.carins.web.dto.HistoryDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/cars")
public class InsuranceClaimController {
    private final CarService carService;
    private final InsuranceClaimRepository insuranceClaimRepository;
    private final InsurancePolicyService insurancePolicyService;

    public InsuranceClaimController(CarService carService, InsuranceClaimRepository insuranceClaimRepository, InsurancePolicyService insurancePolicyService) {
        this.carService = carService;
        this.insuranceClaimRepository = insuranceClaimRepository;
        this.insurancePolicyService = insurancePolicyService;
    }

    //Register a claim
    @PostMapping("/{carId}/claims")
    public ResponseEntity<ClaimDto>registerClaim(@PathVariable Long carId, @RequestBody ClaimDto dto){
        Car car = carService.getCarById(carId);
        if(car == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Car not found");
        }

        if(dto.claimDate() == null || dto.description() == null || dto.description().isEmpty() || dto.amount()<=0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"All fields are required");
        }

        InsuranceClaim claim = new InsuranceClaim();
        claim.setCar(car);
        claim.setClaimDate(dto.claimDate());
        claim.setDescription(dto.description());
        claim.setAmount(dto.amount());

        InsuranceClaim saved = insuranceClaimRepository.save(claim);

        ClaimDto response = new ClaimDto(
                saved.getId(),
                carId,
                saved.getClaimDate(),
                saved.getDescription(),
                saved.getAmount()
        );

        return ResponseEntity.created(URI.create("/api/cars/"+carId +"/claims"+saved.getId())).body(response);
    }


    //pentru istoria masinii am considerat ca data,descrierea si asiguratorul ar trebui sa fie afisati.
    @GetMapping("/{carId}/history")
    public List<HistoryDto> getCarHistory(@PathVariable Long carId){
        Car car = carService.getCarById(carId);
        if(car == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Car not found");
        }

        List<InsuranceClaim> claims = insuranceClaimRepository.findByCarIdOrderByClaimDate(carId);

        return claims.stream().map(c->new HistoryDto(c.getClaimDate(),c.getDescription())).toList();
        }
}

