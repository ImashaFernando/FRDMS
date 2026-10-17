
package com.frdms.frdms.finance_management.service;

import com.frdms.frdms.donation_management.entity.Donation;
import com.frdms.frdms.donation_management.repository.DonationRepository;
import com.frdms.frdms.finance_management.entity.Budget;
import com.frdms.frdms.finance_management.repository.BudgetRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class FinancialAnalyticsService {

    private final DonationRepository donationRepository;
    private final BudgetRepository budgetRepository;

    public FinancialAnalyticsService(
            DonationRepository donationRepository,
            BudgetRepository budgetRepository) {
        this.donationRepository = donationRepository;
        this.budgetRepository = budgetRepository;
    }

    public Map<String, Object> getMonthlyDonationTrends() {
        Map<YearMonth, Double> monthlyTotals = new TreeMap<>();

        LocalDate today = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(today);

        for (int i = 11; i >= 0; i--) {
            monthlyTotals.put(currentMonth.minusMonths(i), 0.0);
        }

        for (Donation donation : donationRepository.findAll()) {
            if (donation.getDonationDate() == null
                    || donation.getCashAmount() == null) {
                continue;
            }

            YearMonth month =
                    YearMonth.from(donation.getDonationDate());

            if (monthlyTotals.containsKey(month)) {
                monthlyTotals.merge(
                        month,
                        donation.getCashAmount(),
                        Double::sum
                );
            }
        }

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("MMM yyyy");

        List<String> labels = new ArrayList<>();
        List<Double> values = new ArrayList<>();

        monthlyTotals.forEach((month, amount) -> {
            labels.add(month.format(formatter));
            values.add(amount);
        });

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("labels", labels);
        result.put("values", values);

        return result;
    }

    public Map<String, Long> getDonationStatusBreakdown() {
        Map<String, Long> counts = new LinkedHashMap<>();

        for (Donation donation : donationRepository.findAll()) {
            String status = donation.getStatus();

            if (status == null || status.isBlank()) {
                status = "Unknown";
            }

            counts.merge(status.trim(), 1L, Long::sum);
        }

        return counts;
    }

    public Map<String, Object> getBudgetAllocationComparison() {
        List<Budget> budgets = budgetRepository.findAll();

        List<String> labels = new ArrayList<>();
        List<Double> requestedAmounts = new ArrayList<>();
        List<Double> allocatedAmounts = new ArrayList<>();

        for (Budget budget : budgets) {
            labels.add(
                    budget.getDisasterName() + " - "
                            + budget.getActivity()
            );

            requestedAmounts.add(
                    budget.getRequestedAmount() == null
                            ? 0.0 : budget.getRequestedAmount()
            );

            allocatedAmounts.add(
                    budget.getAllocatedAmount() == null
                            ? 0.0 : budget.getAllocatedAmount()
            );
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("labels", labels);
        result.put("requestedAmounts", requestedAmounts);
        result.put("allocatedAmounts", allocatedAmounts);

        return result;
    }
}
