package com.vikash.esd_final_project.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthResponse(
        @JsonProperty("access_token") String accessToken,

        @JsonProperty("token_type") String tokenType,

        @JsonProperty("message") String message) {
    public static AuthResponse success(String token) {
        return new AuthResponse(token, "Bearer", "Login successful");
    }
}
