
package com.vantara.platform.veterinaryandhealth.application.internal.queryservices;

import com.vantara.platform.iam.domain.model.valueobjects.Roles;
import com.vantara.platform.iam.domain.repositories.UserRepository;
import com.vantara.platform.profilemanagement.application.queryservices.ProfileManagementQueryService;
import com.vantara.platform.profilemanagement.domain.model.queries.GetProfileByUserIdQuery;
import com.vantara.platform.veterinaryandhealth.application.queryservices.VeterinarianQueryService;
import com.vantara.platform.veterinaryandhealth.interfaces.rest.resources.VeterinarianResource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VeterinarianQueryServiceImpl implements VeterinarianQueryService {

    private final UserRepository userRepository;
    private final ProfileManagementQueryService profileManagementQueryService;

    public VeterinarianQueryServiceImpl(
            UserRepository userRepository,
            ProfileManagementQueryService profileManagementQueryService) {
        this.userRepository = userRepository;
        this.profileManagementQueryService = profileManagementQueryService;
    }

    @Override
    public List<VeterinarianResource> getAllVeterinarians() {

        return userRepository.findAll().stream()
                .filter(user -> user.canSignIn())
                .filter(user -> user.getRoles().stream()
                        .anyMatch(role -> role.getName() == Roles.ROLE_VETERINARIAN))
                .map(user -> profileManagementQueryService.handle(
                                new GetProfileByUserIdQuery(user.getId()))
                        .map(profile -> new VeterinarianResource(
                                user.getId(),
                                profile.getFullName(),
                                user.getEmail().address()
                        ))
                        .orElse(null))
                .filter(veterinarian -> veterinarian != null)
                .toList();
    }
}
