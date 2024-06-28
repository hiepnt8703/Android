package tlu.cse.ht63.cuoiky.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Map;

import tlu.cse.ht63.cuoiky.Model.Order;
import tlu.cse.ht63.cuoiky.R;

public class OrderTrackingAdapter extends RecyclerView.Adapter<OrderTrackingAdapter.OrderViewHolder> {
    private Context context;
    private List<Map<String, Object>> items;

    public OrderTrackingAdapter(Context context, List<Map<String, Object>> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tracking, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Map<String, Object> item = items.get(position);


        holder.textProductName.setText(String.valueOf(item.get("productName")));

        double price = ((Number) item.get("price")).doubleValue();
        int quantity = ((Number) item.get("quantity")).intValue();
        double totalPrice = price * quantity;
        holder.textTotalPrice.setText(String.valueOf(totalPrice));


        // Set quantity
        holder.textQuantity.setText("Quantity: " + item.get("quantity"));

        // Load image using Glide
        Glide.with(context)
                .load(String.valueOf(item.get("image")))
                .placeholder(R.drawable.ic_placeholder) // Placeholder image
                .into(holder.imageProduct);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        ImageView imageProduct;
        TextView textProductName, textTotalPrice, textQuantity;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProduct = itemView.findViewById(R.id.imageProduct);
            textProductName = itemView.findViewById(R.id.textProductName);
            textTotalPrice = itemView.findViewById(R.id.textTotalPrice);
            textQuantity = itemView.findViewById(R.id.textQuantity);
        }
    }
}
