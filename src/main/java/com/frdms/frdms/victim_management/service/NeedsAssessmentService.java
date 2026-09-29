package com.frdms.frdms.victim_management.service;

import com.frdms.frdms.victim_management.entity.NeedsAssessment;
import com.frdms.frdms.victim_management.entity.Victim;
import com.frdms.frdms.victim_management.repository.NeedsAssessmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NeedsAssessmentService {

    private final NeedsAssessmentRepository needsAssessmentRepository;

    public NeedsAssessmentService(
            NeedsAssessmentRepository needsAssessmentRepository) {

        this.needsAssessmentRepository = needsAssessmentRepository;
    }


    // =========================================================
    // SAVE ASSESSMENT
    // =========================================================

    public NeedsAssessment saveAssessment(
            NeedsAssessment assessment) {

        return needsAssessmentRepository.save(assessment);
    }


    // =========================================================
    // GET LATEST ASSESSMENT FOR A VICTIM
    // =========================================================

    public NeedsAssessment getLatestAssessmentByVictim(
            Victim victim) {

        return needsAssessmentRepository
                .findTopByVictimOrderByIdDesc(victim)
                .orElse(null);
    }


    // =========================================================
    // GET ASSESSMENT BY ID
    // =========================================================

    public NeedsAssessment getAssessmentById(Long id) {

        return needsAssessmentRepository
                .findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Needs Assessment not found with ID: " + id
                        )
                );
    }


    // =========================================================
    // GET ALL ASSESSMENTS
    // =========================================================

    public List<NeedsAssessment> getAllAssessments() {

        return needsAssessmentRepository.findAll();
    }


    // =========================================================
    // UPDATE ASSESSMENT
    // =========================================================

    public NeedsAssessment updateAssessment(
            Long id,
            String priority,
            String assessmentRemarks,
            String assessedBy,
            String status) {

        NeedsAssessment assessment =
                getAssessmentById(id);

        assessment.setPriority(priority);
        assessment.setAssessmentRemarks(assessmentRemarks);
        assessment.setAssessedBy(assessedBy);
        assessment.setStatus(status);

        return needsAssessmentRepository.save(assessment);
    }
}