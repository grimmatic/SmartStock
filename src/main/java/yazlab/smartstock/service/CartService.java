package yazlab.smartstock.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import yazlab.smartstock.entity.Cart;
import yazlab.smartstock.entity.Product;
import yazlab.smartstock.repository.CartRepository;
import yazlab.smartstock.repository.ProductRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final AuthService authService;

    public void addToCart(Long productId) {
        Cart cart = cartRepository.findByCustomer(authService.getCurrentCustomer())
                .orElseGet(() -> new Cart(authService.getCurrentCustomer()));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Ürün bulunamadı"));

        cart.getProducts().merge(product, 1, Integer::sum); // Mevcut ürün varsa miktarını artır
        cartRepository.save(cart);
    }

    public Cart getCart() {
        return cartRepository.findByCustomer(authService.getCurrentCustomer())
                .orElseGet(() -> new Cart(authService.getCurrentCustomer()));
    }

    public int getCartCount() {
        Cart cart = cartRepository.findByCustomer(authService.getCurrentCustomer())
                .orElseGet(() -> new Cart(authService.getCurrentCustomer()));

        return cart.getProducts().values().stream().mapToInt(Integer::intValue).sum(); // Toplam ürün miktarı
    }

    public void removeFromCart(Long productId) {
        Cart cart = cartRepository.findByCustomer(authService.getCurrentCustomer())
                .orElseThrow(() -> new RuntimeException("Sepet bulunamadı"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Ürün bulunamadı"));

        cart.getProducts().remove(product);
        cartRepository.save(cart);
    }
}
