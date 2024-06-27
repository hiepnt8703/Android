package tlu.cse.ht63.cuoiky;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import tlu.cse.ht63.cuoiky.Adapter.AdminHomeAdapter;
import tlu.cse.ht63.cuoiky.Model.Product;
import tlu.cse.ht63.cuoiky.Repo.ProductRepo;

public class AdminHomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private AdminHomeAdapter adapter;
    private List<Product> productList;
    private ProductRepo productRepo;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        productRepo = new ProductRepo();
        productList = new ArrayList<>();
        adapter = new AdminHomeAdapter(productList, getContext());
        // Load products from Firestore
        loadProducts();
    }

    private void loadProducts() {
        productRepo.getAllProducts(new ProductRepo.ProductRepoCallback() {
            @Override
            public void onSuccess(List<Product> products) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    productList.clear();
                    productList.addAll(products);
                    adapter.notifyDataSetChanged();
                });
            }

            @Override
            public void onError(Exception e) {
                // Handle error
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_admin_home, container, false);

        // Initialize RecyclerView
        recyclerView = rootView.findViewById(R.id.recycleAdminHome);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // Set up FloatingActionButton
        FloatingActionButton fabAdd = rootView.findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(v -> {
            // Start new activity
            Intent intent = new Intent(getActivity(), AddActivity.class);
            startActivity(intent);
        });


        return rootView;
    }
}
