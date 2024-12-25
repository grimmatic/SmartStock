package yazlab.smartstock.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import yazlab.smartstock.entity.Cart;
import yazlab.smartstock.service.CartService;

import java.util.Map;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<String> addToCart(@RequestParam Long productId) {
        try {
            cartService.addToCart(productId);
            return ResponseEntity.ok("Ürün sepete eklendi");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Ürün eklenirken hata oluştu");
        }
    }

    @GetMapping
    public ResponseEntity<Cart> getCart() {
        return ResponseEntity.ok(cartService.getCart());
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Integer>> getCartCount() {
        int count = cartService.getCartCount();
        return ResponseEntity.ok(Map.of("count", count));
    }

    @PostMapping("/remove")
    public ResponseEntity<String> removeFromCart(@RequestParam Long productId) {
        try {
            cartService.removeFromCart(productId);
            return ResponseEntity.ok("Ürün sepetten kaldırıldı");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Ürün kaldırılırken hata oluştu");
        }
    }
}
