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


    // CREATE VOLUNTEER PROFILE


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


    // GET PROFILE BY EMAIL


    public Optional<VolunteerProfile> getProfileByVolunteerEmail(String email) {

        return volunteerAccountRepository.findByEmail(email)
                .flatMap(
                        volunteerProfileRepository::findByVolunteerAccount
                );
    }


    // =========================
    // UPDATE PENDING PROFILE
    // =========================

    public VolunteerProfile updatePendingProfile(
            String email,
            VolunteerProfile updatedProfile
    ) {

        VolunteerAccount volunteerAccount =
                volunteerAccountRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Volunteer account not found."
                                )
                        );

        VolunteerProfile existingProfile =
                volunteerProfileRepository
                        .findByVolunteerAccount(volunteerAccount)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Volunteer profile not found."
                                )
                        );

        if (existingProfile.getRegistrationStatus()
                != VolunteerProfile.RegistrationStatus.PENDING) {

            throw new IllegalArgumentException(
                    "Volunteer registration can only be edited while it is pending."
            );
        }

        existingProfile.setAge(
                updatedProfile.getAge()
        );

        existingProfile.setWorkExperience(
                updatedProfile.getWorkExperience()
        );

        existingProfile.setSkills(
                updatedProfile.getSkills()
        );

        existingProfile.setAvailability(
                updatedProfile.getAvailability()
        );

        existingProfile.setPreferredWorkingArea(
                updatedProfile.getPreferredWorkingArea()
        );

        return volunteerProfileRepository.save(existingProfile);
    }


    // =========================
    // WITHDRAW PENDING PROFILE
    // =========================

    public void withdrawPendingProfile(String email) {

        VolunteerAccount volunteerAccount =
                volunteerAccountRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Volunteer account not found."
                                )
                        );

        VolunteerProfile existingProfile =
                volunteerProfileRepository
                        .findByVolunteerAccount(volunteerAccount)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Volunteer profile not found."
                                )
                        );

        if (existingProfile.getRegistrationStatus()
                != VolunteerProfile.RegistrationStatus.PENDING) {

            throw new IllegalArgumentException(
                    "Volunteer registration can only be withdrawn while it is pending."
            );
        }

        volunteerProfileRepository.delete(existingProfile);
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


    // =========================
    // VM-US6
    // SAVE PROJECT MANAGER REVIEW NOTES
    // =========================

    public VolunteerProfile saveReviewNotes(
            Long profileId,
            String reviewNotes
    ) {

        VolunteerProfile volunteerProfile =
                volunteerProfileRepository.findById(profileId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Volunteer profile not found."
                                )
                        );

        volunteerProfile.setReviewNotes(reviewNotes);

        return volunteerProfileRepository.save(volunteerProfile);
    }


    // =========================
    // VM-US7
    // APPROVE VOLUNTEER
    // =========================

    public VolunteerProfile approveVolunteer(Long profileId) {

        VolunteerProfile volunteerProfile =
                volunteerProfileRepository.findById(profileId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Volunteer profile not found."
                                )
                        );

        volunteerProfile.setRegistrationStatus(
                VolunteerProfile.RegistrationStatus.APPROVED
        );

        return volunteerProfileRepository.save(volunteerProfile);
    }


    // =========================
    // VM-US8
    // REJECT VOLUNTEER
    // =========================

    public VolunteerProfile rejectVolunteer(
            Long profileId,
            String rejectionReason
    ) {

        VolunteerProfile volunteerProfile =
                volunteerProfileRepository.findById(profileId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Volunteer profile not found."
                                )
                        );

        if (rejectionReason == null || rejectionReason.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Rejection reason is required."
            );
        }

        volunteerProfile.setRejectionReason(
                rejectionReason.trim()
        );

        volunteerProfile.setRegistrationStatus(
                VolunteerProfile.RegistrationStatus.REJECTED
        );

        return volunteerProfileRepository.save(volunteerProfile);
    }
}
