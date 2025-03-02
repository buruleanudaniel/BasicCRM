package com.example.application.data.generator;

import com.example.application.data.entity.Contact;
import com.example.application.data.entity.Company;
import com.example.application.data.entity.Status;
import com.example.application.data.service.CrmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.HashSet;
import java.util.Set;

@Configuration
public class DataGenerator {

    private static final Logger log = LoggerFactory.getLogger(DataGenerator.class);

    private static final String[] FIRST_NAMES = {
            "John", "Jane", "Alice", "Bob", "Charlie", "David", "Eve", "Frank", "Grace", "Heidi"
    };

    private static final String[] LAST_NAMES = {
            "Doe", "Smith", "Johnson", "Williams", "Brown", "Jones", "Miller", "Davis", "Garcia", "Rodriguez"
    };

    private static final String[] EMAIL_DOMAINS = {
            "example.com", "test.com", "sample.org", "company.net", "mail.io"
    };

    @Bean
    public CommandLineRunner loadData(CrmService crmService) {
        return args -> {
            if (crmService.countContacts() == 0) {
                log.info("Generating sample data...");
                generateSampleData(crmService);
                log.info("Sample data generated successfully.");
            } else {
                log.info("Skipping data generation as there are existing records.");
            }
        };
    }

    @Transactional
    void generateSampleData(CrmService crmService) {
        Random random = new Random();
        Set<String> usedEmails = new HashSet<>();

        // Ensure we have some companies and statuses available
        ensureCompaniesExist(crmService);
        ensureStatusesExist(crmService);

        for (int i = 0; i < 50; i++) {
            Contact contact = new Contact();
            contact.setFirstName(FIRST_NAMES[random.nextInt(FIRST_NAMES.length)]);
            contact.setLastName(LAST_NAMES[random.nextInt(LAST_NAMES.length)]);
            contact.setEmail(generateUniqueEmail(random, usedEmails));

            // Assign a random company
            contact.setCompany(getRandomCompany(crmService, random));

            // Assign a random status
            contact.setStatus(getRandomStatus(crmService, random));

            // Optionally, set a birth date
            contact.setBirthDate(LocalDate.of(
                    1970 + random.nextInt(50),
                    1 + random.nextInt(12),
                    1 + random.nextInt(28)
            ));

            log.debug("Saving contact: {}", contact);
            crmService.saveContact(contact);
        }
    }

    private String generateUniqueEmail(Random random, Set<String> usedEmails) {
        String email;
        do {
            email = String.format("%s.%s@%s",
                    FIRST_NAMES[random.nextInt(FIRST_NAMES.length)].toLowerCase(),
                    LAST_NAMES[random.nextInt(LAST_NAMES.length)].toLowerCase(),
                    EMAIL_DOMAINS[random.nextInt(EMAIL_DOMAINS.length)]
            );
        } while (!usedEmails.add(email)); // Ensure uniqueness
        return email;
    }

    private void ensureCompaniesExist(CrmService crmService) {
        if (crmService.findAllCompanies().isEmpty()) {
            log.info("Generating sample companies...");

            // Hard-coded company instances
            Company acmeCorp = new Company();
            acmeCorp.setName("Acme Corporation");

            Company globalEnt = new Company();
            globalEnt.setName("Global Enterprises");

            Company techSolutions = new Company();
            techSolutions.setName("Tech Solutions Inc.");

            // Save the companies
            crmService.saveCompany(acmeCorp);
            crmService.saveCompany(globalEnt);
            crmService.saveCompany(techSolutions);
        }
    }

    private void ensureStatusesExist(CrmService crmService) {
        if (crmService.findAllStatus().isEmpty()) {
            log.info("Generating sample statuses...");
            crmService.saveStatus(new Status("Active"));
            crmService.saveStatus(new Status("Inactive"));
            crmService.saveStatus(new Status("Pending"));
        }
    }

    private Company getRandomCompany(CrmService crmService, Random random) {
        List<Company> companies = crmService.findAllCompanies();
        return companies.get(random.nextInt(companies.size()));
    }

    private Status getRandomStatus(CrmService crmService, Random random) {
        List<Status> statuses = crmService.findAllStatus();
        return statuses.get(random.nextInt(statuses.size()));
    }
}