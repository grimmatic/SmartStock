package yazlab.smartstock.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import yazlab.smartstock.entity.Log;
import yazlab.smartstock.entity.Order;
import yazlab.smartstock.service.OrderService;
import yazlab.smartstock.service.LogService;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final OrderService orderService;
    private final LogService logService;

    @GetMapping("/orders")
    public String viewOrders(Model model) {
        model.addAttribute("pendingOrders", orderService.getPendingOrders());
        return "admin/orders";
    }

    @PostMapping("/orders/approve-all")
    @ResponseBody
    public String approveAllOrders() {
        try {
            List<Order> pendingOrders = orderService.getPendingOrders();
            int count = 0;
            for (Order order : pendingOrders) {
                orderService.updateOrderStatus(order.getId(), Order.OrderStatus.PROCESSING);
                count++;
            }
            String message = count + " sipariş onaylandı";
            logService.logOrderCreation(pendingOrders.get(0)); // İlk siparişi örnek olarak logla
            return message;
        } catch (Exception e) {
            logService.logError("Toplu sipariş onaylama hatası: " + e.getMessage());
            return "Hata: " + e.getMessage();
        }
    }

    @GetMapping("/logs")
    @ResponseBody
    public List<Log> getLogs() {
        return logService.getRecentLogs();
    }
}