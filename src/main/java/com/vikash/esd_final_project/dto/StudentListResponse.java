package com.vikash.esd_final_project.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record StudentListResponse(
        @JsonProperty("id") Long id,

        @JsonProperty("first_name") String firstName,

        @JsonProperty("last_name") String lastName,

        @JsonProperty("email") String email,

        @JsonProperty("placement_status") String placementStatus) {
}
