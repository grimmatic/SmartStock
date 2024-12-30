package yazlab.smartstock.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import yazlab.smartstock.entity.Customer;
import yazlab.smartstock.entity.Log;
import yazlab.smartstock.entity.Order;
import yazlab.smartstock.entity.Product;
import yazlab.smartstock.repository.LogRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LogService {
    private final LogRepository logRepository;

    public List<Log> getRecentLogs() {
        // Son 50 logu getir, tarihe göre sırala
        return logRepository.findTop50ByOrderByCreatedAtDesc();
    }

    public void logOrderCreation(Order order) {
        Log log = new Log();
        log.setCustomerId(order.getCustomer().getId());
        log.setOrderId(order.getId());
        log.setLogType(Log.LogType.INFO);

        String action = switch (order.getOrderStatus()) {
            case PENDING -> "Sipariş oluşturuldu";
            case COMPLETED -> {
                if (order.getCustomer().getCustomerType() == Customer.CustomerType.STANDARD
                        && order.getCustomer().getTotalSpent() >= 2000) {
                    yield "Sipariş onaylandı ve müşteri Premium üyeliğe yükseltildi";
                }
                yield "Sipariş onaylandı";
            }
            case CANCELLED -> "Sipariş iptal edildi";
        };

        log.setLogDetails(String.format("%s - Müşteri: %s (%s), Ürün: %s, Miktar: %d",
                action,
                order.getCustomer().getCustomerName(),
                order.getCustomer().getCustomerType(),
                order.getProduct().getProductName(),
                order.getQuantity()));

        logRepository.save(log);
    }

    public void logError(String message) {
        Log log = new Log();
        log.setLogType(Log.LogType.ERROR);
        log.setLogDetails(message);
        logRepository.save(log);
    }

    public void logWarning(String message) {
        Log log = new Log();
        log.setLogType(Log.LogType.WARNING);
        log.setLogDetails(message);
        logRepository.save(log);
    }


    public void logOrderDeletion(Order order, String reason) {
        Log log = new Log();
        log.setLogType(Log.LogType.WARNING); // Silme işlemi genelde uyarı olarak loglanır
        log.setCustomerId(order.getCustomer().getId());
        log.setOrderId(order.getId());
        log.setLogDetails(String.format(
                "Sipariş silindi - ID: %d, Müşteri: %s, Ürün: %s, Miktar: %d, Sebep: %s",
                order.getId(),
                order.getCustomer().getCustomerName(),
                order.getProduct().getProductName(),
                order.getQuantity(),
                reason
        ));
        log.setCreatedAt(LocalDateTime.now());
        logRepository.save(log);
    }


    public void logProductAction(String action, Product product) {
        Log log = new Log();
        log.setLogType(Log.LogType.INFO);
        log.setLogDetails(String.format(
                "Ürün İşlemi: %s - Ürün ID: %d, İsim: %s, Stok: %d, Fiyat: %.2f",
                action,
                product.getId(),
                product.getProductName(),
                product.getStock(),
                product.getPrice()
        ));
        log.setCreatedAt(LocalDateTime.now());
        logRepository.save(log);
    }
    public void logInfo(String message) {
        Log log = new Log();
        log.setLogType(Log.LogType.INFO);
        log.setLogDetails(message);
        logRepository.save(log);
    }
}