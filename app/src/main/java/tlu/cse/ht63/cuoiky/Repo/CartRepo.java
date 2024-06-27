package tlu.cse.ht63.cuoiky.Repo;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import tlu.cse.ht63.cuoiky.Model.Cart;

public class CartRepo {
    private static final String USERS_COLLECTION = "users";
    private static final String CART_ITEMS_COLLECTION = "cartItems";

    private FirebaseFirestore db;
    private CollectionReference usersCollection;

    public CartRepo() {
        db = FirebaseFirestore.getInstance();
        usersCollection = db.collection(USERS_COLLECTION);
    }

    public void addToCart(String productId) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Log.d("CartRepo", "Người dùng chưa đăng nhập.");
            return;
        }

        String userId = currentUser.getUid();
        DocumentReference userDocRef = usersCollection.document(userId);
        DocumentReference cartItemDocRef = userDocRef.collection(CART_ITEMS_COLLECTION).document(productId);

        userDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (!document.exists()) {
                    // User document does not exist, create it
                    Map<String, Object> userData = new HashMap<>();
                    userDocRef.set(userData).addOnCompleteListener(userCreateTask -> {
                        if (userCreateTask.isSuccessful()) {
                            Log.d("CartRepo", "User document created.");
                            addOrUpdateCartItem(cartItemDocRef, productId);
                        } else {
                            Log.d("CartRepo", "Error creating user document: ", userCreateTask.getException());
                        }
                    });
                } else {
                    Log.d("CartRepo", "User document exists.");
                    addOrUpdateCartItem(cartItemDocRef, productId);
                }
            } else {
                Log.d("CartRepo", "Error getting user document: ", task.getException());
            }
        });
    }

    private void addOrUpdateCartItem(DocumentReference cartItemDocRef, String productId) {
        cartItemDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Product already exists, update quantity
                    Long currentQuantity = document.getLong("quantity");
                    if (currentQuantity != null) {
                        cartItemDocRef.update("quantity", currentQuantity + 1)
                                .addOnSuccessListener(aVoid -> Log.d("CartRepo", "Quantity updated."))
                                .addOnFailureListener(e -> Log.d("CartRepo", "Error updating quantity: ", e));
                    }
                } else {
                    // Product does not exist, add new cart item
                    cartItemDocRef.set(new Cart(productId, 1), SetOptions.merge())
                            .addOnSuccessListener(aVoid -> Log.d("CartRepo", "Cart item added."))
                            .addOnFailureListener(e -> Log.d("CartRepo", "Error adding cart item: ", e));
                }
            } else {
                Log.d("CartRepo", "Error getting cart item: ", task.getException());
            }
        });
    }

    public void updateCartItemQuantity(String productId, int newQuantity) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Log.d("CartRepo", "Người dùng chưa đăng nhập.");
            return;
        }

        String userId = currentUser.getUid();
        DocumentReference cartItemDocRef = usersCollection.document(userId).collection(CART_ITEMS_COLLECTION).document(productId);

        cartItemDocRef.update("quantity", newQuantity)
                .addOnSuccessListener(aVoid -> Log.d("CartRepo", "Quantity updated."))
                .addOnFailureListener(e -> Log.d("CartRepo", "Error updating quantity: ", e));
    }

    public void removeFromCart(String productId) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Log.d("CartRepo", "Người dùng chưa đăng nhập.");
            return;
        }

        String userId = currentUser.getUid();
        DocumentReference cartItemDocRef = usersCollection.document(userId).collection(CART_ITEMS_COLLECTION).document(productId);

        cartItemDocRef.delete()
                .addOnSuccessListener(aVoid -> Log.d("CartRepo", "Cart item removed."))
                .addOnFailureListener(e -> Log.d("CartRepo", "Error removing cart item: ", e));
    }

    public void deleteCartItems(OnCompleteListener<Void> listener) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Log.d("CartRepo", "Người dùng chưa đăng nhập.");
            return;
        }

        String userId = currentUser.getUid();
        CollectionReference cartItemsRef = usersCollection.document(userId).collection(CART_ITEMS_COLLECTION);

        cartItemsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    document.getReference().delete();
                }
                TaskCompletionSource<Void> taskCompletionSource = new TaskCompletionSource<>();
                taskCompletionSource.setResult(null);
                listener.onComplete(taskCompletionSource.getTask());
            } else {
                TaskCompletionSource<Void> taskCompletionSource = new TaskCompletionSource<>();
                taskCompletionSource.setException(task.getException());
                listener.onComplete(taskCompletionSource.getTask());
            }
        });
    }
}
