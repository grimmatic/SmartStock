package yazlab.smartstock.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import yazlab.smartstock.entity.Log;
import yazlab.smartstock.entity.Order;
import yazlab.smartstock.service.AuthService;
import yazlab.smartstock.service.OrderService;
import yazlab.smartstock.service.LogService;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final OrderService orderService;
    private final LogService logService;
    private final AuthService authService;

    @GetMapping("/orders")
    public String viewOrders(Model model) {
        List<Order> pendingOrders = orderService.getPendingOrdersSortedByPriority();

        // Debug için log ekleyelim
        System.out.println("--- Sending Orders to View ---");
        pendingOrders.forEach(order -> {
            System.out.println("Order ID: " + order.getId() +
                    " Priority Score: " + order.getPriorityScore() +
                    " Customer Type: " + order.getCustomer().getCustomerType());
        });

        model.addAttribute("pendingOrders", pendingOrders);
        model.addAttribute("currentCustomer", authService.getCurrentCustomer());
        return "admin/orders";
    }




    @GetMapping
    public String adminPage(Model model) {
        model.addAttribute("pendingOrders", orderService.getPendingOrdersSortedByPriority());
        model.addAttribute("currentCustomer", authService.getCurrentCustomer());
        return "admin";
    }

    @PostMapping("/orders/approve-all")
    @ResponseBody
    public String approveAllOrders() {
        try {
            List<Order> pendingOrders = orderService.getPendingOrders();
            int count = 0;
            for (Order order : pendingOrders) {
                orderService.updateOrderStatus(order.getId(), Order.OrderStatus.COMPLETED);
                count++;
            }
            String message = count + " sipariş onaylandı ve tamamlandı";
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