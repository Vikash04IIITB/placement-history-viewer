package com.vikash.esd_final_project.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record EmployeeResponse(
        @JsonProperty("id") Long id,

        @JsonProperty("first_name") String firstName,

        @JsonProperty("last_name") String lastName,

        @JsonProperty("title") String title,

        @JsonProperty("email") String email,

        @JsonProperty("department") String departmentName,

        @JsonProperty("photograph_path") String photographPath) {
}
