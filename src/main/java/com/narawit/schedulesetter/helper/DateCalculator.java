package com.narawit.schedulesetter.helper;

import static java.time.temporal.ChronoUnit.DAYS;

import java.time.LocalDate;

public class DateCalculator {
	public static int CalculateDateDiff(LocalDate from, LocalDate to) {
        return (int) DAYS.between(from, to);
    }
}
