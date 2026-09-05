package com.digviajay.taskflow.service;

import com.digviajay.taskflow.entity.Company;
import com.digviajay.taskflow.entity.CompanyInvite;
import com.digviajay.taskflow.entity.User;
import com.digviajay.taskflow.repository.CompanyInviteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompanyInviteService {
    private final CompanyInviteRepository companyInviteRepository;
    private final UserService userService;

    private static final int INVITE_EXPIRY_DAYS = 7;

    // Admin generates an invite link — returns the token
    public String generateInviteLink(User requestingUser) {
        if (requestingUser.getCompanyRole() != User.CompanyRole.ADMIN)
            throw new RuntimeException("Only company admins can generate invite links");

        // invalidate previous unused invites for this company to avoid stale links
        companyInviteRepository
                .findByCompanyAndUsedFalse(requestingUser.getCompany())
                .forEach(invite -> {
                    invite.setUsed(true);
                    companyInviteRepository.save(invite);
                });

        CompanyInvite invite = new CompanyInvite();
        invite.setCompany(requestingUser.getCompany());
        invite.setCreatedBy(requestingUser);
        invite.setToken(UUID.randomUUID().toString());
        invite.setExpiresAt(LocalDateTime.now().plusDays(INVITE_EXPIRY_DAYS));
        invite.setUsed(false);

        companyInviteRepository.save(invite);
        return invite.getToken();
    }

    // Called on GET /join/{token} to show the company name on the form
    public Company getCompanyByToken(String token) {
        CompanyInvite invite = findValidInvite(token);
        return invite.getCompany();
    }

    // Called on POST /join/{token} — registers user and marks invite used
    public void joinViaInvite(String token, String username,
                              String email, String password) {
        CompanyInvite invite = findValidInvite(token);

        userService.registerViainvite(username, email, password, invite.getCompany());

        invite.setUsed(true);
        companyInviteRepository.save(invite);
    }

    private CompanyInvite findValidInvite(String token) {
        CompanyInvite invite = companyInviteRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid invite link"));

        if (invite.isUsed())
            throw new RuntimeException("This invite link has already been used");

        if (invite.getExpiresAt().isBefore(LocalDateTime.now()))
            throw new RuntimeException("This invite link has expired");

        return invite;
    }
}
