package com.example.carins.web.dto;

import java.time.LocalDate;

public record HistoryDto(
    LocalDate eventDate,
    String desc
    )
{}
