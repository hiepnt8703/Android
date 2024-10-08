package tlu.cse.ht63.cuoiky.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tlu.cse.ht63.cuoiky.AdminFragment;
import tlu.cse.ht63.cuoiky.AdminHomeFragment;
import tlu.cse.ht63.cuoiky.MainActivity;
import tlu.cse.ht63.cuoiky.Model.Product;
import tlu.cse.ht63.cuoiky.R;
import tlu.cse.ht63.cuoiky.Repo.ProductRepo;

public class AdminHomeAdapter extends RecyclerView.Adapter<AdminHomeAdapter.ViewHolder> {
    private List<Product> productList;
    private Context context;


    public AdminHomeAdapter(List<Product> productList , Context context) {
        this.context = context;
        this.productList = productList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.textProductName.setText(product.getName());
        holder.textProductPrice.setText(String.format("$ %.2f", product.getPrice()));
        Glide.with(context).load(product.getImage()).into(holder.imageView);
        holder.bind(product);

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DialogPlus dialogPlus = DialogPlus.newDialog(holder.imageView.getContext())
                        .setContentHolder(new com.orhanobut.dialogplus.ViewHolder(R.layout.activity_update_product))
                        .setExpanded(true, 2100)
                        .create();

                View view = dialogPlus.getHolderView();
                ImageView imageView = view.findViewById(R.id.edit_image_product);
                EditText name = view.findViewById(R.id.edit_product_name);
                EditText description = view.findViewById(R.id.edit_product_description);
                EditText price = view.findViewById(R.id.edit_product_price);

                Button btnUpdate = view.findViewById(R.id.button_edit_product);

                name.setText(product.getName());
                description.setText(product.getDescription());
                price.setText(String.valueOf(product.getPrice()));
                Glide.with(imageView.getContext())
                        .load(product.getImage())
                        .into(imageView);
                dialogPlus.show();

                btnUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Create a new Product object with updated values
                        Product updatedProduct = new Product(
                                product.getId(),
                                name.getText().toString(),
                                description.getText().toString(),
                                Double.parseDouble(price.getText().toString()),
                                product.getImage(), // Assuming the image URL doesn't change
                                product.getRating() // Keep the original rating
                        );

                        ProductRepo productRepo = new ProductRepo();
                        productRepo.updateProductById(product.getId(), updatedProduct, new ProductRepo.UpdateProductCallback() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(context, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                                if (dialogPlus != null && dialogPlus.isShowing()) {
                                    dialogPlus.dismiss();
                                }

                                productList.set(holder.getAdapterPosition(), updatedProduct);
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        notifyDataSetChanged();
                                    }
                                });
                                FragmentActivity activity = (FragmentActivity) context;
                                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.nav_admin, new AdminFragment());
                                fragmentTransaction.commit();
                            }

                            @Override
                            public void onError(Exception e) {
                                Toast.makeText(context, "Cập nhật thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });

        holder.btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Are you sure?");
                builder.setMessage("Delete cannot be undone!");

                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ProductRepo productRepo = new ProductRepo();
                        productRepo.deleteProductById(product.getId(), new ProductRepo.DeleteProductCallback() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(context, "Product deleted successfully", Toast.LENGTH_SHORT).show();
                                productList.remove(holder.getAdapterPosition());
                                notifyDataSetChanged();

                                FragmentActivity activity = (FragmentActivity) context;
                                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.nav_admin, new AdminFragment());
                                fragmentTransaction.commit();
                            }

                            @Override
                            public void onError(Exception e) {
                                Toast.makeText(context, "Failed to delete product: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView textProductName;
        private TextView textProductPrice;

        Button btnEdit, btnDel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_product);
            textProductName = itemView.findViewById(R.id.text_product_name);
            textProductPrice = itemView.findViewById(R.id.text_product_price);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDel = itemView.findViewById(R.id.btnDel);
        }

        public void bind(Product product) {
            textProductName.setText(product.getName());
            textProductPrice.setText(String.valueOf(product.getPrice()));
            Glide.with(context).load(product.getImage()).into(imageView);
        }
    }
}
