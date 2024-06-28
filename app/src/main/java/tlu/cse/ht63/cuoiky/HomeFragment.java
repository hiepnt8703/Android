package tlu.cse.ht63.cuoiky;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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
    private List<Product> filteredList; // Danh sách sản phẩm được lọc
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


        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                filterProducts(s.toString());
            }
        });

        return view;
    }

    private void fetchProducts() {
        productRepo.getAllProducts(new ProductRepo.ProductRepoCallback() {
            @Override
            public void onSuccess(List<Product> productList) {
                products.clear();
                products.addAll(productList);
                showAllProducts();
            }

            @Override
            public void onError(Exception e) {
                // Xử lý lỗi khi tải sản phẩm
                Toast.makeText(requireContext(), "Failed to fetch products: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterProducts(String keyword) {
        if (filteredList == null) {
            filteredList = new ArrayList<>(products);
        }
        if (keyword.isEmpty()) {
            showAllProducts();
        } else {
            List<Product> tempFilteredList = new ArrayList<>();
            for (Product product : products) {
                if (product.getName().toLowerCase().contains(keyword.toLowerCase())) {
                    tempFilteredList.add(product);
                }
            }
            filteredList.clear();
            filteredList.addAll(tempFilteredList);
            productAdapter.filterList(filteredList);
        }
    }

    // Phương thức để hiển thị tất cả sản phẩm
    private void showAllProducts() {
        if (filteredList != null) {
            filteredList.clear();
        }
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                productAdapter.filterList(products);
            }
        });
    }

}
