package com.example.application.data.service;

import com.example.application.data.entity.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CrmService {

    private final ContactRepository contactRepository;
    private final CompanyRepository companyRepository;
    private final StatusRepository statusRepository;

    public CrmService(ContactRepository contactRepository,
                      CompanyRepository companyRepository,
                      StatusRepository statusRepository) {
        this.contactRepository = contactRepository;
        this.companyRepository = companyRepository;
        this.statusRepository = statusRepository;
    }

    // Existing methods...

    public List<Contact> findAllContacts(String filterText) {
        if (filterText == null || filterText.isEmpty()) {
            return contactRepository.findAll();
        } else {
            return contactRepository.search(filterText);
        }
    }

    public long countContacts() {
        return contactRepository.count();
    }

    public void deleteContact(Contact contact) {
        contactRepository.delete(contact);
    }

    public void saveContact(Contact contact) {
        if (contact == null) {
            System.err.println("Contact is null.");
        }
        contactRepository.save(contact);
    }

    public List<Company> findAllCompanies() {
        return companyRepository.findAll();
    }

    public void saveCompany(Company company) {
        if (company == null) {
            System.err.println("Company is null.");
            return;
        }
        companyRepository.save(company); // Save the company using the repository
    }

    public List<Status> findAllStatus() {
        return statusRepository.findAll();
    }

    public void saveStatus(Status status) {
        statusRepository.save(status);
    }
}