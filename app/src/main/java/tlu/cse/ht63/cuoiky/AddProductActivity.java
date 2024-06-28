package tlu.cse.ht63.cuoiky;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

import tlu.cse.ht63.cuoiky.Model.Product;

public class AddProductActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText editProductName, editProductDescription, editProductPrice;
    private ImageView imageProduct;
    private Uri imageUri;
    private Button btnAddProduct, btnBack;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        editProductName = findViewById(R.id.add_product_name);
        editProductDescription = findViewById(R.id.add_product_description);
        editProductPrice = findViewById(R.id.add_product_price);
        imageProduct = findViewById(R.id.image_product);
        btnAddProduct = findViewById(R.id.button_add_product);
        btnBack = findViewById(R.id.btnback);

        imageProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadProduct();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageProduct.setImageURI(imageUri);
        }
    }

    private void uploadProduct() {
        if (imageUri != null) {
            // Generate a random unique ID for the image file
            String imageId = UUID.randomUUID().toString();
            StorageReference imageRef = storageReference.child("images/" + imageId);

            // Upload image to Firebase Storage
            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Image uploaded successfully, now get the download URL
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            // URI of uploaded image
                            String imageUrl = uri.toString();

                            // Create Product object
                            String productName = editProductName.getText().toString().trim();
                            String productDescription = editProductDescription.getText().toString().trim();
                            String productPriceStr = editProductPrice.getText().toString().trim();

                            if (!productName.isEmpty() && !productDescription.isEmpty() && !productPriceStr.isEmpty()) {
                                double productPrice = Double.parseDouble(productPriceStr);

                                Product product = new Product(null, productName, productDescription, productPrice, imageUrl, 0);

                                // Add product to Firestore
                                db.collection("products")
                                        .add(product)
                                        .addOnSuccessListener(documentReference -> {
                                            Toast.makeText(AddProductActivity.this, "Thêm sản phẩm thành công", Toast.LENGTH_SHORT).show();
                                            finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(AddProductActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            } else {
                                Toast.makeText(AddProductActivity.this, "Vui lòng nhập đầy đủ thông tin sản phẩm", Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(AddProductActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "Vui lòng chọn hình ảnh sản phẩm", Toast.LENGTH_SHORT).show();
        }
    }
}
