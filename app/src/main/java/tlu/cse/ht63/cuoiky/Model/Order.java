package tlu.cse.ht63.cuoiky.Model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Order implements Serializable {
    private String orderId;
    private String userId;
    private double totalAmount;
    private int totalQuantity;
    private String status;
    private List<Map<String, Object>> items;

    // Default constructor (required for Firestore)
    public Order() {}

    // Constructor with parameters
    public Order(String orderId, String userId, double totalAmount, int totalQuantity, String status, List<Map<String, Object>> items) {
        this.orderId = orderId;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.totalQuantity = totalQuantity;
        this.status = status;
        this.items = items;
    }

    // Getters and setters
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Map<String, Object>> getItems() {
        return items;
    }

    public void setItems(List<Map<String, Object>> items) {
        this.items = items;
    }
}
