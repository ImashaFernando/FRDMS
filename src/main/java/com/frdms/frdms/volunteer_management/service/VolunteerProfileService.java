package com.frdms.frdms.volunteer_management.service;

import com.frdms.frdms.volunteer_management.entity.VolunteerAccount;
import com.frdms.frdms.volunteer_management.entity.VolunteerProfile;
import com.frdms.frdms.volunteer_management.repository.VolunteerAccountRepository;
import com.frdms.frdms.volunteer_management.repository.VolunteerProfileRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VolunteerProfileService {

    private final VolunteerProfileRepository volunteerProfileRepository;
    private final VolunteerAccountRepository volunteerAccountRepository;

    public VolunteerProfileService(
            VolunteerProfileRepository volunteerProfileRepository,
            VolunteerAccountRepository volunteerAccountRepository
    ) {
        this.volunteerProfileRepository = volunteerProfileRepository;
        this.volunteerAccountRepository = volunteerAccountRepository;
    }

    public VolunteerProfile createProfile(
            String email,
            VolunteerProfile volunteerProfile
    ) {

        VolunteerAccount volunteerAccount =
                volunteerAccountRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Volunteer account not found."
                                )
                        );

        if (volunteerProfileRepository.existsByVolunteerAccount(volunteerAccount)) {
            throw new IllegalArgumentException(
                    "A volunteer profile has already been submitted for this account."
            );
        }

        volunteerProfile.setVolunteerAccount(volunteerAccount);

        volunteerProfile.setRegistrationStatus(
                VolunteerProfile.RegistrationStatus.PENDING
        );

        return volunteerProfileRepository.save(volunteerProfile);
    }

    public Optional<VolunteerProfile> getProfileByVolunteerEmail(String email) {

        return volunteerAccountRepository.findByEmail(email)
                .flatMap(
                        volunteerProfileRepository::findByVolunteerAccount
                );
    }
}