package com.example.carins.service;

import com.example.carins.model.InsurancePolicy;
import com.example.carins.repo.CarRepository;
import com.example.carins.repo.InsurancePolicyRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Service
@RequestMapping("/api/policies")

public class InsurancePolicyService {

    private final InsurancePolicyRepository repository;
    private final CarRepository carRepository;

    public InsurancePolicyService(InsurancePolicyRepository repository, CarRepository carRepository) {
        this.repository = repository;
        this.carRepository = carRepository;
    }

    public List<InsurancePolicy> getPolicies(){
        return repository.findAll();
    };

    public InsurancePolicy savePolicy(InsurancePolicy p){
        return repository.save(p);
    }





}
