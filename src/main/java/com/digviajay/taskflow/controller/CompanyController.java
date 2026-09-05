package com.digviajay.taskflow.controller;

import com.digviajay.taskflow.entity.Company;
import com.digviajay.taskflow.entity.User;
import com.digviajay.taskflow.service.CompanyInviteService;
import com.digviajay.taskflow.service.CompanyService;
import com.digviajay.taskflow.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/company")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;
    private final CompanyInviteService companyInviteService;
    private final UserService userService;

    // Company overview — members list
    @GetMapping
    public String companyPage(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/login";

        Company company = companyService.findById(loggedInUser.getCompany().getId()); // ← fix
        List<User> members = userService.getCompanyMembers(company);
        boolean isAdmin = loggedInUser.getCompanyRole() == User.CompanyRole.ADMIN;

        model.addAttribute("company", company);
        model.addAttribute("members", members);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("user", loggedInUser);
        return "company/members";
    }


    // Admin generates a new invite link
    @PostMapping("/invite")
    public String generateInvite(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/login";

        try {
            String token = companyInviteService.generateInviteLink(loggedInUser);
            String inviteUrl = "/join/" + token;

            Company company = companyService.findById(loggedInUser.getCompany().getId()); // ← fix
            List<User> members = userService.getCompanyMembers(company);

            model.addAttribute("company", company);
            model.addAttribute("members", members);
            model.addAttribute("isAdmin", true);
            model.addAttribute("inviteUrl", inviteUrl);
            model.addAttribute("user", loggedInUser);
            return "company/members";

        } catch (RuntimeException e) {
            return "redirect:/company";
        }
    }
}