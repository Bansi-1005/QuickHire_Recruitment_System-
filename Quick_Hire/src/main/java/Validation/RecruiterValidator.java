/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Validation;

import Entity.Tbljob;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;

public class RecruiterValidator {

    public static void validateJob(Tbljob job) {

        if (job == null) {
            throw new RuntimeException("Job data is missing");
        }

        // ================= COMPENSATION VALIDATION =================
        BigDecimal min = job.getJobCompensationMin();
        BigDecimal max = job.getJobCompensationMax();

        if (min != null && max != null) {

            if (max.compareTo(min) < 0) {

                throw new RuntimeException(
                        "compensationMax:Maximum compensation must be greater than minimum compensation"
                );
            }
        }

        // ================= EXPIRY DATE VALIDATION =================
        if (job.getJobExpiryDate() != null) {

            LocalDate expiryDate
                    = job.getJobExpiryDate()
                            .toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();

            if (!expiryDate.isAfter(LocalDate.now())) {

                throw new RuntimeException(
                        "jobExpiryDate:Expiry date must be a future date"
                );
            }
        }
    }
}