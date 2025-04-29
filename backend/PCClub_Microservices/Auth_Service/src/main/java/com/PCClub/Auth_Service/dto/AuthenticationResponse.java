package com.PCClub.Auth_Service.dto;

import com.PCClub.Auth_Service.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    private String message;
    private String token;
    private Role role;
}
