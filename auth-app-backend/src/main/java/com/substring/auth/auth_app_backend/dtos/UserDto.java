package com.substring.auth.auth_app_backend.dtos;

import com.substring.auth.auth_app_backend.entities.Provider;
import lombok.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private UUID id;
    private String email;
    private String name;
    private String password;
    private String image;
    private boolean enabled=true;
    private Instant created=Instant.now();
    private Instant updated=Instant.now();
    private Provider  provider=Provider.LOCAL;

    private Set<RoleDto> roles=new HashSet<>();
}
