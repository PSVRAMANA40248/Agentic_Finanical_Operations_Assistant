package com.finops.agent;

import com.finops.agent.model.FraudRecord;
import com.finops.agent.model.Ticket;
import com.finops.agent.model.Transaction;
import com.finops.agent.repository.FraudRecordRepository;
import com.finops.agent.repository.TicketRepository;
import com.finops.agent.repository.TransactionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@SpringBootApplication
public class FinOpsApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinOpsApplication.class, args);
    }

    @Bean
    public CommandLineRunner seedDatabase(TicketRepository ticketRepo, TransactionRepository txnRepo, FraudRecordRepository fraudRepo) {
        return args -> {
            if (ticketRepo.count() > 0) return;

            // Seed Transactions
            txnRepo.save(new Transaction(null, "TXN-1001", "CUST-8812", 450.0, "FAILED", "GATEWAY_TIMEOUT", "RAZORPAY", LocalDateTime.now().minusHours(2)));
            txnRepo.save(new Transaction(null, "TXN-1002", "CUST-9923", 14500.0, "DISPUTED", "NETWORK_ERROR", "STRIPE", LocalDateTime.now().minusHours(5)));
            txnRepo.save(new Transaction(null, "TXN-1003", "CUST-4411", 1200.0, "SUCCESS", "NONE", "PAYTM", LocalDateTime.now().minusDays(1)));
            txnRepo.save(new Transaction(null, "TXN-1004", "CUST-7733", 8500.0, "FAILED", "INSUFFICIENT_FUNDS", "RAZORPAY", LocalDateTime.now().minusHours(1)));

            // Seed Fraud Records
            fraudRepo.save(new FraudRecord(null, "CUST-8812", false, 0, 12.0, "LOW_RISK", LocalDateTime.now().minusDays(10)));
            fraudRepo.save(new FraudRecord(null, "CUST-9923", true, 4, 82.0, "IP_MISMATCH, HIGH_VELOCITY, BLACKLISTED", LocalDateTime.now().minusDays(1)));
            fraudRepo.save(new FraudRecord(null, "CUST-4411", false, 1, 20.0, "NORMAL", LocalDateTime.now().minusDays(30)));
            fraudRepo.save(new FraudRecord(null, "CUST-7733", false, 3, 58.0, "MULTIPLE_CHARGEBACKS", LocalDateTime.now().minusDays(3)));

            // Seed Tickets (with Indian PII for Privacy Redactor testing)
            ticketRepo.save(new Ticket(null, "TCK-8011", "Ananya Sharma", "CUST-8812", "PAYMENTS",
                    "My UPI payment of INR 450 failed on Swiggy but money deducted. My Aadhaar 2345 6789 0123 and phone +91 9876543210. Please refund.",
                    450.0, "INR", "PENDING", "LOW", 12.0, "Pending Routing", LocalDateTime.now().minusMinutes(45), null));

            ticketRepo.save(new Ticket(null, "TCK-8012", "Rajesh Kumar", "CUST-9923", "FRAUD",
                    "Urgent! Unauthorized charge of INR 14,500 on my debit card 4111222233334444. My PAN is ABCDE1234F. Freeze account!",
                    14500.0, "INR", "PENDING", "HIGH", 82.0, "Pending Routing", LocalDateTime.now().minusMinutes(20), null));

            ticketRepo.save(new Ticket(null, "TCK-8013", "Vikram Patel", "CUST-4411", "SUPPORT",
                    "Want clarification on monthly transaction fee charged to my UPI id vikram@okaxis.",
                    120.0, "INR", "PENDING", "LOW", 20.0, "Pending Routing", LocalDateTime.now().minusMinutes(10), null));

            ticketRepo.save(new Ticket(null, "TCK-8014", "Priya Verma", "CUST-7733", "PAYMENTS",
                    "Requesting manual refund for failed merchant payment of INR 8,500 via Razorpay.",
                    8500.0, "INR", "PENDING", "HIGH", 58.0, "Pending Routing", LocalDateTime.now().minusMinutes(5), null));

            System.out.println(">>> FinOps Agent Database initialized with synthetic tickets, transactions, and fraud records.");
        };
    }
}
