package tlu.cse.ht63.cuoiky;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import tlu.cse.ht63.cuoiky.Adapter.CartAdapter;
import tlu.cse.ht63.cuoiky.Model.Cart;
import tlu.cse.ht63.cuoiky.Repo.CartRepo;
import tlu.cse.ht63.cuoiky.Repo.OrderRepo;
import tlu.cse.ht63.cuoiky.Repo.ProductRepo;

public class CartFragment extends Fragment {
    private RecyclerView cartRec;
    private TextView resultSum;
    private Button payBtn;
    private List<Cart> cartList;
    private CartAdapter cartAdapter;
    private ProductRepo productRepo;
    private CartRepo cartRepo;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        cartRec = view.findViewById(R.id.cartRec);
        resultSum = view.findViewById(R.id.resultSum);
        payBtn = view.findViewById(R.id.payBtn);

        cartList = new ArrayList<>();
        cartAdapter = new CartAdapter(getContext(), cartList);
        cartAdapter.setOnCartChangeListener(this::updateTotalSum);

        cartRec.setLayoutManager(new LinearLayoutManager(getContext()));
        cartRec.setAdapter(cartAdapter);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        productRepo = new ProductRepo();
        cartRepo = new CartRepo();

        loadCartItems();

        payBtn.setOnClickListener(v -> checkUserInfoAndProceed());

        return view;
    }

    private void loadCartItems() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            CollectionReference cartItemsRef = db.collection("users").document(userId).collection("cartItems");

            cartItemsRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null) {
                        getActivity().runOnUiThread(() -> {
                            cartList.clear();
                            AtomicReference<Double> totalSum = new AtomicReference<>(0.0);

                            for (DocumentSnapshot document : querySnapshot) {
                                Cart cartItem = document.toObject(Cart.class);
                                if (cartItem != null) {
                                    cartItem.setProductId(document.getId());
                                    cartList.add(cartItem);

                                    productRepo.getProductById(cartItem.getProductId(), product -> {
                                        if (product != null) {
                                            totalSum.updateAndGet(v -> v + cartItem.getQuantity() * product.getPrice());
                                            resultSum.setText(String.format("Total: $ %.2f", totalSum.get()));
                                            cartAdapter.notifyDataSetChanged();
                                        }
                                    });
                                }
                            }
                        });
                    }
                } else {
                    Log.e("OrdersFragment", "Error getting cart items", task.getException());
                }
            });
        }
    }

    private void checkUserInfoAndProceed() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            CollectionReference infoRef = db.collection("users").document(userId).collection("information");

            infoRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        // Proceed to payment if collection "information" exists and is not empty
                        proceedToPayment();
                    } else {
                        // Navigate to EditProfileFragment if collection "information" does not exist or is empty
                        getParentFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new EditProfileFragment())
                                .addToBackStack(null)
                                .commit();
                    }
                } else {
                    Log.e("OrdersFragment", "Error checking user information", task.getException());
                }
            });
        }
    }


    private void proceedToPayment() {
        cartRepo.getCartItems(task -> {
            if (task.isSuccessful()) {
                List<Cart> cartItems = task.getResult();
                calculateTotalAmount(cartItems).addOnCompleteListener(amountTask -> {
                    if (amountTask.isSuccessful()) {
                        double totalAmount = amountTask.getResult();

                        // Extract sum from resultSum
                        String resultSumText = resultSum.getText().toString();
                       // double sum = Double.parseDouble(resultSumText.replace("Total: $", "").trim());

                        OrderRepo orderRepo = new OrderRepo();
                        orderRepo.addOrder(cartItems, totalAmount, new OrderRepo.AddOrderCallback() {
                            @Override
                            public void onSuccess() {
                                Log.d("OrdersFragment", "Order added successfully.");
                                cartRepo.deleteCartItems(cartDeleteTask -> {
                                    if (cartDeleteTask.isSuccessful()) {
                                        Log.d("OrdersFragment", "Cart items deleted successfully.");
                                        resultSum.setText("Total: $0.00");
                                        cartList.clear();
                                        cartAdapter.notifyDataSetChanged();
                                        Log.d("OrdersFragment", "Proceeding to payment...");
                                    } else {
                                        Log.e("OrdersFragment", "Error deleting cart items", cartDeleteTask.getException());
                                    }
                                });
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Log.e("OrdersFragment", "Error adding order.", e);
                            }
                        });
                    } else {
                        Log.e("OrdersFragment", "Error calculating total amount", amountTask.getException());
                    }
                });
            } else {
                Log.e("OrdersFragment", "Error getting cart items", task.getException());
            }
        });
    }


    private Task<Double> calculateTotalAmount(List<Cart> cartItems) {
        TaskCompletionSource<Double> tcs = new TaskCompletionSource<>();
        final AtomicReference<Double> totalAmount = new AtomicReference<>(0.0);
        final int[] processedItems = {0};

        for (Cart cart : cartItems) {
            productRepo.getProductById(cart.getProductId(), product -> {
                if (product != null) {
                    totalAmount.updateAndGet(v -> v + product.getPrice() * cart.getQuantity());
                }

                processedItems[0]++;
                if (processedItems[0] == cartItems.size()) {
                    tcs.setResult(totalAmount.get());
                }
            });
        }

        return tcs.getTask();
    }



    private void updateTotalSum() {
        AtomicReference<Double> totalSum = new AtomicReference<>(0.0);
        for (Cart cartItem : cartList) {
            productRepo.getProductById(cartItem.getProductId(), product -> {
                if (product != null) {
                    totalSum.updateAndGet(v -> v + cartItem.getQuantity() * product.getPrice());
                    getActivity().runOnUiThread(() -> resultSum.setText(String.format("Total: $ %.2f", totalSum.get())));
                }
            });
        }
    }
}
