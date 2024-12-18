package yazlab.smartstock.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import yazlab.smartstock.entity.Customer;
import yazlab.smartstock.service.AuthService;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;
    private final HttpSession session;

    @GetMapping("/login")
    public String loginPage() {
        if (session.getAttribute("currentCustomer") != null) {
            return "redirect:/";
        }
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        if (session.getAttribute("currentCustomer") != null) {
            return "redirect:/";
        }
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute Customer customer, RedirectAttributes redirectAttributes) {
        try {
            authService.register(customer);
            redirectAttributes.addFlashAttribute("success", "Kayıt başarılı. Lütfen giriş yapın.");
            return "redirect:/auth/login";
        } catch (Exception e) {
            log.error("Registration error", e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/auth/register";
        }
    }
    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        RedirectAttributes redirectAttributes) {
        try {
            Customer customer = authService.login(username, password);

            // Set authentication in security context
            Authentication auth = new UsernamePasswordAuthenticationToken(customer, null, null);
            SecurityContextHolder.getContext().setAuthentication(auth);

            // Set in session
            session.setAttribute("currentCustomer", customer);

            log.info("Login successful for user: {}", username);
            return "redirect:/";

        } catch (Exception e) {
            log.error("Login failed for user: {}", username, e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/auth/login?error";
        }
    }

    @GetMapping("/logout")
    public String logout() {
        session.invalidate();
        return "redirect:/auth/login";
    }
}