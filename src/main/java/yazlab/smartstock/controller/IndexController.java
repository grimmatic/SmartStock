package yazlab.smartstock.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import yazlab.smartstock.service.AuthService;
import yazlab.smartstock.service.ProductService;

@Controller
public class IndexController {
    private final AuthService authService;
    private final ProductService productService;

    public IndexController(AuthService authService, ProductService productService) {
        this.authService = authService;
        this.productService = productService;
    }

    @GetMapping("/index")
    public String index(Model model) {
        try {
            // Giriş yapan kullanıcının bilgilerini ekle
            model.addAttribute("currentCustomer", authService.getCurrentCustomer());
        } catch (Exception e) {
            // Eğer kullanıcı giriş yapmamışsa null olarak ekle
            model.addAttribute("currentCustomer", null);
        }

        // Ürün listesini modele ekle
        model.addAttribute("products", productService.getAllProducts());
        return "index";
    }
}
