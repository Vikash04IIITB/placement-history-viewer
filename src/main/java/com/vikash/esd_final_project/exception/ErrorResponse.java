package com.vikash.esd_final_project.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private int status; // HTTP status code
    private String message; // Error message
    private LocalDateTime timestamp; // Time of error
}
