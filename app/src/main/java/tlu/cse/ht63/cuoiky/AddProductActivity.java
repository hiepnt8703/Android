package tlu.cse.ht63.cuoiky;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import tlu.cse.ht63.cuoiky.Model.Product;
import tlu.cse.ht63.cuoiky.Repo.ProductRepo;

public class AddProductActivity extends AppCompatActivity {

    private EditText editProductName, editProductDescription, editProductPrice;
    private RatingBar ratingProduct;
    private ImageView imageProduct;
    private Uri imageUri;

    private ProductRepo productRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

    }


}
