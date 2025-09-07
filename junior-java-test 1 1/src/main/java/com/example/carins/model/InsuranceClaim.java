package com.example.carins.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "insuranceclaim")

public class InsuranceClaim {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private LocalDate claimDate;
    private String description;
    private double amount;


    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Car car;

    public long getId() {return id;}
    public LocalDate getClaimDate() {return claimDate;}
    public double getAmount() {return amount;}
    public Car getCar() {return car;}
    public String getDescription() {return description;}

    public void setId(long id) {this.id = id;}
    public void setClaimDate(LocalDate claimDate) {this.claimDate = claimDate;}
    public void setAmount(double amount) {this.amount = amount;}
    public void setDescription(String description) {this.description = description;}
    public void setCar(Car car) {this.car = car;}
}
