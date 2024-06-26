package tlu.cse.ht63.cuoiky.Repo;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import tlu.cse.ht63.cuoiky.Model.Product;

public class ProductRepo {
    private static final String COLLECTION_NAME = "products";
    private FirebaseFirestore db;
    private Executor executor = Executors.newSingleThreadExecutor();

    public ProductRepo() {
        db = FirebaseFirestore.getInstance();
    }

    public interface ProductRepoCallback {
        void onSuccess(List<Product> products);
        void onError(Exception e);
    }

    public void getAllProducts(ProductRepoCallback callback) {
        CollectionReference productsRef = db.collection(COLLECTION_NAME);
        productsRef.get()
                .addOnSuccessListener(executor, queryDocumentSnapshots -> {
                    List<Product> products = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Product product = Product.fromDocumentSnapshot(document);
                        products.add(product);
                    }
                    callback.onSuccess(products);
                })
                .addOnFailureListener(executor, e -> callback.onError(e));
    }
}
