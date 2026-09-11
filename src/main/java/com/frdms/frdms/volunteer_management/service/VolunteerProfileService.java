package com.frdms.frdms.volunteer_management.service;

import com.frdms.frdms.volunteer_management.entity.VolunteerAccount;
import com.frdms.frdms.volunteer_management.entity.VolunteerProfile;
import com.frdms.frdms.volunteer_management.repository.VolunteerAccountRepository;
import com.frdms.frdms.volunteer_management.repository.VolunteerProfileRepository;
import org.springframework.stereotype.Service;

import java.util.List;
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

    // =========================
    // VM-US5
    // GET ALL SUBMITTED PROFILES
    // =========================

    public List<VolunteerProfile> getAllVolunteerProfiles() {

        return volunteerProfileRepository.findAll();
    }

    // =========================
    // VM-US5
    // GET ONE PROFILE BY ID
    // =========================

    public VolunteerProfile getVolunteerProfileById(Long id) {

        return volunteerProfileRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Volunteer profile not found."
                        )
                );
    }
}