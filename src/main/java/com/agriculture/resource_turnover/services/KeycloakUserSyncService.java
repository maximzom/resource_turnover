package com.agriculture.resource_turnover.services;

import com.agriculture.resource_turnover.entity.UserEntity;
import com.agriculture.resource_turnover.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class KeycloakUserSyncService {

    private final UserRepository userRepository;

    @Autowired
    public KeycloakUserSyncService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void syncUser(Jwt jwt) {
        String username = jwt.getClaimAsString("preferred_username");
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");

        Set<String> roles = Collections.emptySet();
        if (realmAccess != null) {
            Object rolesObj = realmAccess.get("roles");
            if (rolesObj instanceof Collection<?>) {
                roles = ((Collection<?>) rolesObj).stream()
                        .filter(String.class::isInstance)
                        .map(String.class::cast)
                        .collect(Collectors.toSet());
            }
        }

        UserEntity user = userRepository.findByUsername(username)
                .orElseGet(() -> new UserEntity(username));

        user.setRoles(roles);
        userRepository.save(user);
    }
}