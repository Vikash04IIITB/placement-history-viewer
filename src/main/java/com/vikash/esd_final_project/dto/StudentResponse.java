package com.vikash.esd_final_project.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vikash.esd_final_project.entity.Domains;
import com.vikash.esd_final_project.entity.Specialisation;

public record StudentResponse(
                @JsonProperty("first_name") String firstName,

                @JsonProperty("last_name") String lastName,

                @JsonProperty("email") String email,

                @JsonProperty("domain") Domains domain,

                @JsonProperty("graduation_year") int gradYear,

                @JsonProperty("org") String organization,

                @JsonProperty("specialisation") Specialisation specialisation) {
}
