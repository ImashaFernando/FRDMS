
package com.frdms.frdms.feedback_management.repository;

import com.frdms.frdms.feedback_management.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    // Find feedback belonging to a person using their name and NIC.
    List<Feedback> findByNameIgnoreCaseAndNicIgnoreCase(
            String name,
            String nic
    );

    // Find feedback awaiting Project Manager review.
    List<Feedback> findByApprovalStatusIgnoreCase(
            String approvalStatus
    );

    // Verify ownership before allowing a particular feedback record
    // to be edited or deleted.
    Optional<Feedback> findByIdAndNameIgnoreCaseAndNicIgnoreCase(
            Long id,
            String name,
            String nic
    );
}
