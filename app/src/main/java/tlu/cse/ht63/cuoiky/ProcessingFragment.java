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

import tlu.cse.ht63.cuoiky.Adapter.ProcessingAdapter;
import tlu.cse.ht63.cuoiky.Model.Order;
import tlu.cse.ht63.cuoiky.Repo.OrderRepo;

public class ProcessingFragment extends Fragment {
    private RecyclerView recyclerView;
    private ProcessingAdapter processingAdapter;
    private List<Order> orderList;
    private OrderRepo orderRepo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_processing, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        orderList = new ArrayList<>();
        processingAdapter = new ProcessingAdapter(getContext(), orderList);
        recyclerView.setAdapter(processingAdapter);
        orderRepo = new OrderRepo();

        loadOrders();

        return view;
    }

    private void loadOrders() {
        orderRepo.getOrders(new OrderRepo.GetOrdersCallback() {
            @Override
            public void onSuccess(List<Order> orders) {
                processingAdapter.setOrderListWithStatus(orders,"Processing", "Accept");
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("ProcessingFragment", "Failed to load orders: " + e.getMessage());
            }
        });
    }
}
