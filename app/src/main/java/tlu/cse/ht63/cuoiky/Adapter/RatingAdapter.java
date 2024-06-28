package tlu.cse.ht63.cuoiky.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Map;

import tlu.cse.ht63.cuoiky.R;

public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.RatingViewHolder> {
    private Context context;
    private List<Map<String, Object>> items;

    public RatingAdapter(Context context, List<Map<String, Object>> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public RatingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_rating, parent, false);
        return new RatingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RatingViewHolder holder, int position) {
        Map<String, Object> item = items.get(position);
        holder.textProductName.setText((String) item.get("productName"));
        Glide.with(context).load((String) item.get("image")).into(holder.imageProduct);

        long starRating = (long) item.get("rating");
        setStarRating(holder, starRating);

        for (int i = 0; i < holder.stars.length; i++) {
            final int starPosition = i;
            holder.stars[i].setOnClickListener(v -> {
                for (int j = 0; j <= starPosition; j++) {
                    holder.stars[j].setColorFilter(ContextCompat.getColor(context, R.color.gold));
                }
                for (int j = starPosition + 1; j < holder.stars.length; j++) {
                    holder.stars[j].setColorFilter(ContextCompat.getColor(context, android.R.color.darker_gray));
                }
                item.put("rating", starPosition + 1);
            });
        }
    }

    private void setStarRating(RatingViewHolder holder, long starRating) {
        for (int i = 0; i < holder.stars.length; i++) {
            if (i < starRating) {
                holder.stars[i].setColorFilter(ContextCompat.getColor(context, R.color.gold));
            } else {
                holder.stars[i].setColorFilter(ContextCompat.getColor(context, android.R.color.darker_gray));
            }
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class RatingViewHolder extends RecyclerView.ViewHolder {
        ImageView imageProduct;
        TextView textProductName;
        ImageView[] stars;

        public RatingViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProduct = itemView.findViewById(R.id.imageProduct);
            textProductName = itemView.findViewById(R.id.textProductName);
            stars = new ImageView[]{
                    itemView.findViewById(R.id.star1),
                    itemView.findViewById(R.id.star2),
                    itemView.findViewById(R.id.star3),
                    itemView.findViewById(R.id.star4),
                    itemView.findViewById(R.id.star5)
            };
        }
    }
}
