package tlu.cse.ht63.cuoiky;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

import tlu.cse.ht63.cuoiky.Adapter.OrderTrackingAdapter;
import tlu.cse.ht63.cuoiky.Model.Information;
import tlu.cse.ht63.cuoiky.Model.Order;
import tlu.cse.ht63.cuoiky.Repo.InformationRepo;

public class OrderTrackingFragment extends Fragment {

    private TextView textUserName, textUserPhone, textUserAddress;
    private RecyclerView recyclerViewOrders;
    private ImageView backArrow,imageViewAccept;

    private Order order;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_tracking, container, false);

        textUserName = view.findViewById(R.id.textUserName);
        textUserPhone = view.findViewById(R.id.textUserPhone);
        textUserAddress = view.findViewById(R.id.textUserAddress);
        recyclerViewOrders = view.findViewById(R.id.recyclerViewOrders);
        backArrow = view.findViewById(R.id.back_arrow);

        imageViewAccept = view.findViewById(R.id.imageViewAccept);
        // Handle back button click event
        backArrow.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        // Retrieve Order object from arguments
        if (getArguments() != null) {
            order = (Order) getArguments().getSerializable("order");
            if (order != null) {
                // Load user information
                loadInformation(order.getUserId());

                // Setup RecyclerView with order items
                setupRecyclerView(order.getItems());
                updateUIBasedOnStatus(order.getStatus());
            }
        }

        return view;
    }

    private void loadInformation(String userId) {
        InformationRepo informationRepo = new InformationRepo();
        informationRepo.getUserInformation(new InformationRepo.InformationCallback() {
            @Override
            public void onSuccess(Information information) {
                // Handle success, e.g., update UI with user information
                textUserName.setText("Tên: " + information.getName());
                textUserPhone.setText("Số điện thoại: " + information.getPhoneNumber());
                textUserAddress.setText("Địa chỉ: " + information.getAddress());
            }

            @Override
            public void onFailure(Exception e) {
                // Handle failure, e.g., show error message
                Toast.makeText(getContext(), "Failed to get user information: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecyclerView(List<Map<String, Object>> items) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerViewOrders.setLayoutManager(layoutManager);
        OrderTrackingAdapter adapter = new OrderTrackingAdapter(getContext(), items);
        recyclerViewOrders.setAdapter(adapter);
    }

    private void updateUIBasedOnStatus(String status) {
        // Update Accept icon color based on status
        if ("Accept".equalsIgnoreCase(status)) {
            imageViewAccept.setColorFilter(getResources().getColor(R.color.green)); // Change to desired color
        }
    }
}
