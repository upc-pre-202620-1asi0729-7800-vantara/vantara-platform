package com.vantara.platform.profilemanagement.application.internal.commandservices;

import com.vantara.platform.profilemanagement.application.commandservices.ProfileCommandService;
import com.vantara.platform.profilemanagement.domain.model.aggregates.Profile;
import com.vantara.platform.profilemanagement.domain.model.commands.CreateOrUpdateProfileCommand;
import com.vantara.platform.profilemanagement.domain.repositories.ProfileRepository;
import com.vantara.platform.shared.application.result.ApplicationError;
import com.vantara.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

/**
 * Implements the profile use cases of the write side. A profile is created the first
 * time an account saves one, and updated afterwards.
 */
@Service
public class ProfileCommandServiceImpl implements ProfileCommandService {

    private final ProfileRepository profileRepository;
    private final Clock clock;

    public ProfileCommandServiceImpl(ProfileRepository profileRepository, Clock clock) {
        this.profileRepository = profileRepository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public Result<Profile, ApplicationError> handle(CreateOrUpdateProfileCommand command) {
        try {
            var userId = requireUserId(command.userId());
            var existing = profileRepository.findByUserId(userId).orElse(null);
            if (existing == null) {
                var created = Profile.create(userId, command.fullName(), command.organizationName(),
                        command.phone(), command.photoUrl(), command.theme(), command.locale(), clock.instant());
                return Result.success(profileRepository.save(created));
            }
            existing.update(command.fullName(), command.organizationName(), command.phone(),
                    command.photoUrl(), command.theme(), command.locale(), clock.instant());
            return Result.success(profileRepository.save(existing));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("profile", e.getMessage()));
        }
    }

    private static Long requireUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User id must be a positive number");
        }
        return userId;
    }
}
