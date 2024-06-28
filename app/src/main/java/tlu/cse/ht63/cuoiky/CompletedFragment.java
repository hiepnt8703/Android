package tlu.cse.ht63.cuoiky;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import tlu.cse.ht63.cuoiky.Adapter.CompletedAdapter;
import tlu.cse.ht63.cuoiky.Model.Order;
import tlu.cse.ht63.cuoiky.Repo.OrderRepo;

public class CompletedFragment extends Fragment {
    private RecyclerView recyclerView;
    private CompletedAdapter completedRating;
    private List<Order> orderList;
    private OrderRepo orderRepo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_completed, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        orderList = new ArrayList<>();
        completedRating = new CompletedAdapter(getContext(), orderList);
        recyclerView.setAdapter(completedRating);
        orderRepo = new OrderRepo();

        loadOrders();

        return view;
    }

    private void loadOrders() {
        orderRepo.getOrders(new OrderRepo.GetOrdersCallback() {
            @Override
            public void onSuccess(List<Order> orders) {
                completedRating.setOrderListWithStatus(orders,"Completed");
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("CompletedFragment", "Failed to load orders: " + e.getMessage());
            }
        });
    }
}
