package yazlab.smartstock.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import yazlab.smartstock.service.CustomerService;
import yazlab.smartstock.service.OrderService;
import yazlab.smartstock.service.ProductService;

@Controller
@RequiredArgsConstructor
public class WebController {
    private final CustomerService customerService;
    private final ProductService productService;
    private final OrderService orderService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("products", productService.findAll());
        model.addAttribute("activeOrders", orderService.findActiveOrders());
        return "index";
    }

    @GetMapping("/customers")
    public String customers(Model model) {
        model.addAttribute("customers", customerService.findAll());
        return "customers";
    }

    @GetMapping("/products")
    public String products(Model model) {
        model.addAttribute("products", productService.findAll());
        return "products";
    }

    @GetMapping("/orders")
    public String orders(Model model) {
        model.addAttribute("orders", orderService.findAll());
        return "orders";
    }
    @GetMapping("/account")
    public String accountPage(HttpSession session) {
        if (session.getAttribute("currentCustomer") == null) {
            return "redirect:/auth/login";
        }
        return "account";  // account sayfasına yönlendir
    }
}