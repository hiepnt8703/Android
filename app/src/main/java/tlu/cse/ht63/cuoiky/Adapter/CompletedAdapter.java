package tlu.cse.ht63.cuoiky.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import tlu.cse.ht63.cuoiky.Model.Order;
import tlu.cse.ht63.cuoiky.RatingFragment;
import tlu.cse.ht63.cuoiky.R;

public class CompletedAdapter extends RecyclerView.Adapter<CompletedAdapter.OrderViewHolder> {
    private Context context;
    private List<Order> orderList;

    public CompletedAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_completed, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.textId.setText(order.getOrderId());
        holder.textTotalQuantity.setText("Số lượng: " + order.getTotalQuantity());
        holder.textTotalPrice.setText("Tổng tiền: " + order.getTotalAmount());
        holder.textProductRating.setOnClickListener(view -> {
            FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
            RatingFragment ratingFragment = new RatingFragment();
            Bundle args = new Bundle();
            args.putSerializable("order", order); // Correct usage of putSerializable
            ratingFragment.setArguments(args);
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, ratingFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public void setOrderList(List<Order> orderList) {
        this.orderList = orderList;
        notifyDataSetChanged();
    }

    public void setOrderListWithStatus(List<Order> orders, String... statuses) {
        List<Order> filteredOrders = new ArrayList<>();
        for (Order order : orders) {
            for (String status : statuses) {
                if (order.getStatus().equalsIgnoreCase(status)) {
                    filteredOrders.add(order);
                    break;
                }
            }
        }
        setOrderList(filteredOrders);
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView textId, textTotalQuantity, textTotalPrice, textProductRating;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            textId = itemView.findViewById(R.id.textId);
            textTotalQuantity = itemView.findViewById(R.id.textTotalQuantity);
            textTotalPrice = itemView.findViewById(R.id.textTotalPrice);
            textProductRating = itemView.findViewById(R.id.product_rating);
        }
    }
}
