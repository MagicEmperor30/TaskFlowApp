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
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final CompanyService companyService;
    private final CompanyInviteService companyInviteService;

    // --- Company Registration (new company + admin user) ---

    @GetMapping("/register")
    public String registerPage() {
        return "auth/register";  // form with: companyName, username, email, password
    }

    @PostMapping("/register")
    public String register(@RequestParam String companyName,
                           @RequestParam String username,
                           @RequestParam String email,
                           @RequestParam String password,
                           Model model) {
        try {
            companyService.registerCompanyWithAdmin(companyName, username, email, password);
            return "redirect:/login?registered";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }
    }

    // --- Invite-based Registration ---

    @GetMapping("/join/{token}")
    public String joinPage(@PathVariable String token, Model model) {
        try {
            Company company = companyInviteService.getCompanyByToken(token);
            model.addAttribute("token", token);
            model.addAttribute("companyName", company.getName());
            return "auth/join";  // form with: username, email, password (company pre-filled)
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/invite-invalid";
        }
    }

    @PostMapping("/join/{token}")
    public String join(@PathVariable String token,
                       @RequestParam String username,
                       @RequestParam String email,
                       @RequestParam String password,
                       Model model) {
        try {
            companyInviteService.joinViaInvite(token, username, email, password);
            return "redirect:/login?joined";
        } catch (RuntimeException e) {
            model.addAttribute("token", token);
            model.addAttribute("error", e.getMessage());
            return "auth/join";
        }
    }

    // --- Login / Logout (unchanged) ---

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        try {
            User user = userService.login(email, password);
            session.setAttribute("loggedInUser", user);
            return "redirect:/dashboard";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}