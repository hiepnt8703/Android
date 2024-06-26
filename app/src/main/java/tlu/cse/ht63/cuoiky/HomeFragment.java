package tlu.cse.ht63.cuoiky;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import tlu.cse.ht63.cuoiky.Adapter.ProductAdapter;
import tlu.cse.ht63.cuoiky.Model.Product;
import tlu.cse.ht63.cuoiky.Repo.ProductRepo;

public class HomeFragment extends Fragment {
    private EditText searchBar;
    private ImageView headerImage;
    private RecyclerView productList;
    private ProductAdapter productAdapter;
    private List<Product> products = new ArrayList<>();
    private ProductRepo productRepo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        searchBar = view.findViewById(R.id.search_bar);
        headerImage = view.findViewById(R.id.header_image);
        productList = view.findViewById(R.id.product_list);
        productAdapter = new ProductAdapter(requireContext(), products);
        productList.setLayoutManager(new GridLayoutManager(requireContext(), 2)); // 2 columns
        productList.setAdapter(productAdapter);

        productRepo = new ProductRepo();
        fetchProducts();

        return view;
    }

    private void fetchProducts() {
        productRepo.getAllProducts(new ProductRepo.ProductRepoCallback() {
            @Override
            public void onSuccess(List<Product> productList) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    products.clear();
                    products.addAll(productList);
                    productAdapter.notifyDataSetChanged();
                });
            }

            @Override
            public void onError(Exception e) {
                // Handle error fetching products
            }
        });
    }
}
