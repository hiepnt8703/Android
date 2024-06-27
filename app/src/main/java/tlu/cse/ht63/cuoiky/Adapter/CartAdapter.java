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

import tlu.cse.ht63.cuoiky.Model.Cart;
import tlu.cse.ht63.cuoiky.Model.Product;
import tlu.cse.ht63.cuoiky.R;
import tlu.cse.ht63.cuoiky.Repo.CartRepo;
import tlu.cse.ht63.cuoiky.Repo.ProductRepo;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    // Thêm interface để thông báo thay đổi giỏ hàng
    public interface OnCartChangeListener {
        void onCartChanged();
    }

    private OnCartChangeListener onCartChangeListener;

    public void setOnCartChangeListener(OnCartChangeListener listener) {
        this.onCartChangeListener = listener;
    }

    // Các thành viên khác
    private Context context;
    private List<Cart> cartList;
    private CartRepo cartRepo;
    private ProductRepo productRepo;

    public CartAdapter(Context context, List<Cart> cartList) {
        this.context = context;
        this.cartList = cartList;
        this.cartRepo = new CartRepo();
        this.productRepo = new ProductRepo();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Cart cart = cartList.get(position);
        productRepo.getProductById(cart.getProductId(), product -> {
            if (product != null) {
                holder.productName.setText(product.getName());
                holder.productPrice.setText(String.format("Price: $ %.2f", product.getPrice()));
                holder.tvTotal.setText(String.format("Total: $ %.2f", cart.getQuantity() * product.getPrice()));
                Glide.with(context).load(product.getImage()).into(holder.productImage);
            }
        });

        holder.productQuantity.setText(String.valueOf(cart.getQuantity()));

        holder.buttonIncrease.setOnClickListener(v -> {
            cartRepo.updateCartItemQuantity(cart.getProductId(), cart.getQuantity() + 1);
            cart.setQuantity(cart.getQuantity() + 1);
            notifyItemChanged(position);
            // Gọi callback khi giỏ hàng thay đổi
            if (onCartChangeListener != null) {
                onCartChangeListener.onCartChanged();
            }
        });

        holder.buttonDecrease.setOnClickListener(v -> {
            if (cart.getQuantity() > 1) {
                cartRepo.updateCartItemQuantity(cart.getProductId(), cart.getQuantity() - 1);
                cart.setQuantity(cart.getQuantity() - 1);
                notifyItemChanged(position);
                // Gọi callback khi giỏ hàng thay đổi
                if (onCartChangeListener != null) {
                    onCartChangeListener.onCartChanged();
                }
            } else {
                cartRepo.removeFromCart(cart.getProductId());
                cartList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, cartList.size());
                // Gọi callback khi giỏ hàng thay đổi
                if (onCartChangeListener != null) {
                    onCartChangeListener.onCartChanged();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName;
        TextView productPrice;
        TextView productQuantity;
        TextView tvTotal;
        TextView buttonDecrease;
        TextView buttonIncrease;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            productQuantity = itemView.findViewById(R.id.product_quantity);
            tvTotal = itemView.findViewById(R.id.tvTotal);
            buttonDecrease = itemView.findViewById(R.id.button_decrease);
            buttonIncrease = itemView.findViewById(R.id.button_increase);
        }
    }
}
