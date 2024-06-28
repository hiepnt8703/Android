package tlu.cse.ht63.cuoiky.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import tlu.cse.ht63.cuoiky.Model.Product;
import tlu.cse.ht63.cuoiky.R;
import tlu.cse.ht63.cuoiky.Repo.CartRepo;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private Context context;
    private List<Product> productList;
    private List<Product> filteredList;
    private CartRepo cartRepo;

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
        this.filteredList = new ArrayList<>(productList);
        this.cartRepo = new CartRepo();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = filteredList.get(position);
        holder.productName.setText(product.getName());
        holder.productPrice.setText(String.format("$ %.2f", product.getPrice()));
        holder.productRating.setText(String.valueOf(product.getRating()));
        Glide.with(context).load(product.getImage()).into(holder.productImage);

        holder.add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cartRepo.addToCart(product.getId());
                Toast.makeText(context, "Thêm vào giỏ hàng thành công",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName;
        TextView productPrice;
        TextView productRating;
        ImageButton add_to_cart;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            productRating = itemView.findViewById(R.id.product_rating_text);
            add_to_cart = itemView.findViewById(R.id.add_to_cart_button);
        }
    }


    public void filterList(List<Product> filteredList) {
        this.filteredList = filteredList;
        notifyDataSetChanged();
    }
}
