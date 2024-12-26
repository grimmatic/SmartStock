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

    // Ana sayfa için endpoint
    @GetMapping("/")
    public String root(Model model) {
        return getIndexPage(model);
    }

    // /index için endpoint
    @GetMapping("/index")
    public String index(Model model) {
        return getIndexPage(model);
    }

    private String getIndexPage(Model model) {
        // Giriş yapmış kullanıcının bilgilerini ekle
        try {
            model.addAttribute("currentCustomer", authService.getCurrentCustomer());
        } catch (Exception e) {
            // Kullanıcı giriş yapmamışsa null olarak ekle
            model.addAttribute("currentCustomer", null);
        }

        // Tüm ürünleri ekle (giriş yapılıp yapılmadığına bakmaksızın)
        model.addAttribute("products", productService.getAllProducts());

        return "index";
    }
}