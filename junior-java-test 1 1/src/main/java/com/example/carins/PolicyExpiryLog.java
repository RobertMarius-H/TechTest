package com.example.carins;

import com.example.carins.model.InsurancePolicy;
import com.example.carins.service.InsurancePolicyService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class PolicyExpiryLog {

    private final InsurancePolicyService policyService;
    private final Set<Long> alreadyLogged = new HashSet<>(); // păstrează polițele

    public PolicyExpiryLog(InsurancePolicyService policyService) {
        this.policyService = policyService;
    }

    //In cerinta se specifica de faptul ca trebuie sa se activeze maxim intr-o ora dupa ce o polita expira;
    //eu am setat dupa 5 secunde de la expirare sa se activeze pentru a vedea daca cronul functioneaza.

    @Scheduled(cron = "*/5 * * * * *")
    public void logExpiredPolicies(){
        LocalDate today = LocalDate.now();
        List<InsurancePolicy> policies = policyService.getPolicies();

        for(InsurancePolicy p : policies){
            if(p.getEndDate() != null && //end date trebuie sa nu fie null
                !alreadyLogged.contains(p.getId()) && //nu trebuie sa se afle in set
                !p.getEndDate().isAfter(today) && // endDate <= today
                !p.getEndDate().isBefore(today.minusDays(1))
                ) {
                System.out.println("Policy with ID " + p.getId() + " for carID " + p.getCar().getId() + " expired on " + p.getEndDate());
                alreadyLogged.add(p.getId());
            }
        }
    }
}
