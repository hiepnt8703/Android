package tlu.cse.ht63.cuoiky.Repo;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import tlu.cse.ht63.cuoiky.Model.Cart;
import tlu.cse.ht63.cuoiky.Model.Order;

public class OrderRepo {
    private static final String ORDERS_COLLECTION = "orders";
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ProductRepo productRepo;

    public OrderRepo() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        productRepo = new ProductRepo();
    }

    public interface GetOrdersCallback {
        void onSuccess(List<Order> orders);
        void onFailure(Exception e);
    }

    public interface AddOrderCallback {
        void onSuccess(Order order);
        void onFailure(Exception e);
    }

    public interface DeleteOrderCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    public void getOrders(GetOrdersCallback callback) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.d("OrderRepo", "User is not logged in.");
            callback.onFailure(new Exception("User is not logged in."));
            return;
        }

        String userId = currentUser.getUid();
        CollectionReference ordersCollection = db.collection(ORDERS_COLLECTION);

        ordersCollection.whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Order> orders = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            Order order = document.toObject(Order.class);
                            if (order != null) {
                                order.setOrderId(document.getId());
                                orders.add(order);
                            }
                        }
                        callback.onSuccess(orders);
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    public void addOrder(List<Cart> cartItems, double totalAmount, final AddOrderCallback callback) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.d("OrderRepo", "User is not logged in.");
            return;
        }

        String userId = currentUser.getUid();
        CollectionReference ordersCollection = db.collection(ORDERS_COLLECTION);

        List<Map<String, Object>> items = new ArrayList<>();
        List<Task<Void>> tasks = new ArrayList<>();

        final AtomicReference<Double> totalQuantity = new AtomicReference<>(0.0);
        for (Cart cart : cartItems) {
            TaskCompletionSource<Void> taskCompletionSource = new TaskCompletionSource<>();
            tasks.add(taskCompletionSource.getTask());

            productRepo.getProductById(cart.getProductId(), product -> {
                if (product != null) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("productId", cart.getProductId());
                    item.put("productName", product.getName());
                    item.put("quantity", cart.getQuantity());
                    totalQuantity.updateAndGet(v -> v + cart.getQuantity());
                    item.put("price", product.getPrice());
                    item.put("image", product.getImage());
                    item.put("rating", 0);
                    items.add(item);
                }
                taskCompletionSource.setResult(null);
            });
        }

        Tasks.whenAll(tasks).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Map<String, Object> orderData = new HashMap<>();
                orderData.put("userId", userId);
                orderData.put("totalAmount", totalAmount);
                orderData.put("totalQuantity", totalQuantity.get());
                orderData.put("status", "Processing");
                orderData.put("items", items);

                ordersCollection.add(orderData).addOnCompleteListener(orderTask -> {
                    if (orderTask.isSuccessful()) {
                        Order order = new Order();
                        order.setOrderId(orderTask.getResult().getId());
                        order.setUserId(userId);
                        order.setTotalAmount(totalAmount);
                        order.setTotalQuantity(totalQuantity.get().intValue());
                        order.setStatus("Processing");
                        order.setItems(items);
                        callback.onSuccess(order);
                    } else {
                        callback.onFailure(orderTask.getException());
                    }
                });
            } else {
                callback.onFailure(task.getException());
            }
        });
    }


    public void deleteOrder(String orderId, DeleteOrderCallback callback) {
        DocumentReference orderDoc = db.collection(ORDERS_COLLECTION).document(orderId);
        orderDoc.delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callback.onSuccess();
            } else {
                callback.onFailure(task.getException());
            }
        });
    }
}
