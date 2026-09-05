package com.digviajay.taskflow.service;

import com.digviajay.taskflow.entity.Company;
import com.digviajay.taskflow.repository.CompanyRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final UserService userService;

    public Company registerCompanyWithAdmin(String companyName,String username,String email,String password)
    {
        if (companyName == null || companyName.trim().length() < 2)
            throw new RuntimeException("Company name must be at least 2 characters");

        String name = companyName.trim();
        String slug = generateSlug(name);

        if (companyRepository.existsByName(name))
            throw new RuntimeException("A company with this name already exists");

        if (companyRepository.existsBySlug(slug))
            throw new RuntimeException("A company with a similar name already exists, try a more specific name");

        Company company = new Company();
        company.setName(name);
        company.setSlug(slug);
        Company saved = companyRepository.save(company);

        // first user becomes ADMIN
        userService.registerCompanyAdmin(username, email, password, saved);

        return saved;
    }
    public Company findById(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found"));
    }

    public Company findBySlug(String slug) {
        return companyRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Company not found"));
    }

    // "Furtados School of Music" -> "furtados-school-of-music"
    private String generateSlug(String name) {
        return name.toLowerCase()
                .trim()
                .replaceAll("[^a-z0-9\\s-]", "")   // remove special chars
                .replaceAll("\\s+", "-")             // spaces to hyphens
                .replaceAll("-+", "-");              // collapse multiple hyphens
    }
}
