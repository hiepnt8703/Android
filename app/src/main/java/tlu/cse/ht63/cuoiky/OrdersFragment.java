package tlu.cse.ht63.cuoiky;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import tlu.cse.ht63.cuoiky.Model.Product;
import tlu.cse.ht63.cuoiky.R;
import tlu.cse.ht63.cuoiky.Repo.ProductRepo;

public class OrdersFragment extends Fragment {
    private RecyclerView cartRec;
    private TextView resultSum;
    private List<Cart> cartList;
    private CartAdapter cartAdapter;
    private ProductRepo productRepo;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        cartRec = view.findViewById(R.id.cartRec);
        resultSum = view.findViewById(R.id.resultSum);

        cartList = new ArrayList<>();
        cartAdapter = new CartAdapter(getContext(), cartList);
        cartAdapter.setOnCartChangeListener(this::updateTotalSum); // Thiết lập listener

        cartRec.setLayoutManager(new LinearLayoutManager(getContext()));
        cartRec.setAdapter(cartAdapter);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        productRepo = new ProductRepo();

        loadCartItems();

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
                        });
                    }
                } else {
                    Log.e("OrdersFragment", "Error getting cart items", task.getException());
                }
            });
        }
    }

    // Phương thức cập nhật tổng số tiền
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
