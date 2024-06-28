package tlu.cse.ht63.cuoiky.Adapter;

import static androidx.core.app.ActivityCompat.getReferrer;
import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import tlu.cse.ht63.cuoiky.AdminActivity;
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
                        .setExpanded(true,2100)
                        .create();

                View view = dialogPlus.getHolderView();
                ImageView imageView = view.findViewById(R.id.edit_image_product);
                EditText name = view.findViewById(R.id.edit_product_name);
                EditText description = view.findViewById(R.id.edit_product_description);
                EditText price = view.findViewById(R.id.edit_product_price);
                EditText imageUrl = view.findViewById(R.id.edit_product_url);

                Button btnUpdate = view.findViewById(R.id.button_edit_product);
                Button btnDel = view.findViewById(R.id.btnDel);

                name.setText(product.getName());
                description.setText(product.getDescription());
                price.setText(String.valueOf(product.getPrice()));
                imageUrl.setText(product.getImage());

                String url = imageUrl.getText().toString().trim();
                Glide.with(imageView.getContext())
                        .load(url)
                        .into(imageView);
                dialogPlus.show();

                btnUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Map<String,Object> map = new HashMap<>();
                        map.put("name",name.getText().toString());
                        map.put("description",description.getText().toString());
                        map.put("picture",imageUrl.getText().toString());
                        map.put("price", Double.parseDouble(price.getText().toString()));
                        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("products").child(product.getId());
                        // Cập nhật dữ liệu lên Firebase
                        productRef.updateChildren(map)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(context, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                                        // Đóng dialog (nếu có)
                                        if (dialogPlus != null && dialogPlus.isShowing()) {
                                            dialogPlus.dismiss();
                                        }
                                        // Quay về màn hình chính (nếu cần)
                                        Intent intent = new Intent(context, AdminActivity.class);
                                        context.startActivity(intent);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "Cập nhật thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });

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
            textProductPrice.setText(String.valueOf(product.getPrice())); // Sử dụng giá trị giá của sản phẩm (nếu có)
            // Để đặt hình ảnh sản phẩm: imageView.setImageResource(product.getImageResId()); (nếu có)
            Glide.with(context).load(product.getImage()).into(imageView);
        }
    }
}
