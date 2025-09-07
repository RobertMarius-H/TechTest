package com.example.carins.web;

import com.example.carins.model.Car;
import com.example.carins.model.InsurancePolicy;
import com.example.carins.repo.InsurancePolicyRepository;
import com.example.carins.service.CarService;
import com.example.carins.service.InsurancePolicyService;
import com.example.carins.web.dto.CarDto;
import com.example.carins.web.dto.InsuranceDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class InsurancePolicyController {
    private final InsurancePolicyService service;
    private final CarService carService;

    public InsurancePolicyController(InsurancePolicyService service, CarService carService) {
        this.service = service;
        this.carService = carService;
    }

    @GetMapping("/policies")
    public List<InsuranceDto> getPolicies(){return service.getPolicies().stream().map(this::toDto).toList();}

    private InsuranceDto toDto(InsurancePolicy p){
        var c = p.getCar();
        return new InsuranceDto(
                p.getId(),
                c.getId(),
                p.getProvider(),
                p.getStartDate(),
                p.getEndDate()
        );
    }


    @PutMapping("/policies/updatePoliciesEndDate")
    public List<InsuranceDto> fixEndDates(){
        List<InsurancePolicy> policies = service.getPolicies();
        List<InsuranceDto> updated = new ArrayList<>();

        for(InsurancePolicy p : policies){
            if(p.getEndDate() == null){
                p.setEndDate(p.getStartDate().plusYears(1));
                service.savePolicy(p);
            }
            updated.add(toDto(p));
        }
        return updated;
    }


    @PostMapping("/policies/add")
    public InsuranceDto addPolicy(@RequestBody InsuranceDto dto) {

        if (dto.endDate() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "endDate is required for creating a policy");
        }

        InsurancePolicy p = new InsurancePolicy();
        p.setCar(carService.getCarById(dto.carId()));
        p.setProvider(dto.provider());
        p.setStartDate(dto.startDate());
        p.setEndDate(dto.endDate());

        service.savePolicy(p);

        return toDto(p);
    }




}
