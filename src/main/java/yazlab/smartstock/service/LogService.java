package yazlab.smartstock.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import yazlab.smartstock.entity.Log;
import yazlab.smartstock.entity.Order;
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
        log.setLogDetails(String.format("Sipariş oluşturuldu - Müşteri: %s, Ürün: %s, Miktar: %d",
                order.getCustomer().getCustomerName(),
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
}