package tlu.cse.ht63.cuoiky.Repo;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RatingRepo {
    private static final String RATINGS_COLLECTION = "ratings";
    private static final String PRODUCTS_COLLECTION = "products";
    private FirebaseFirestore db;

    public RatingRepo() {
        db = FirebaseFirestore.getInstance();
    }

    public interface OnRatingSubmissionListener {
        void onSuccess();
        void onFailure(Exception e);
    }

    public void submitRating(String productId, String userId, long rating, OnRatingSubmissionListener listener) {
        CollectionReference ratingsCollection = db.collection(RATINGS_COLLECTION);
        DocumentReference ratingDoc = ratingsCollection.document(productId);

        ratingDoc.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                ratingDoc.update("ratings." + userId, rating).addOnCompleteListener(updateTask -> {
                    if (updateTask.isSuccessful()) {
                        updateProductRating(productId, listener);
                    } else {
                        listener.onFailure(updateTask.getException());
                    }
                });
            } else {
                Map<String, Object> ratings = new HashMap<>();
                ratings.put(userId, rating);

                Map<String, Object> ratingData = new HashMap<>();
                ratingData.put("ratings", ratings);

                ratingDoc.set(ratingData).addOnCompleteListener(setTask -> {
                    if (setTask.isSuccessful()) {
                        updateProductRating(productId, listener);
                    } else {
                        listener.onFailure(setTask.getException());
                    }
                });
            }
        });
    }

    private void updateProductRating(String productId, OnRatingSubmissionListener listener) {
        db.collection(RATINGS_COLLECTION).document(productId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Map<String, Object> ratings = (Map<String, Object>) task.getResult().get("ratings");
                if (ratings != null) {
                    double totalRating = 0;
                    for (Object value : ratings.values()) {
                        totalRating += ((Number) value).doubleValue();
                    }
                    double averageRating = totalRating / ratings.size();

                    db.collection(PRODUCTS_COLLECTION).document(productId)
                            .update("rating", averageRating)
                            .addOnCompleteListener(updateTask -> {
                                if (updateTask.isSuccessful()) {
                                    listener.onSuccess();
                                } else {
                                    listener.onFailure(updateTask.getException());
                                }
                            });
                }
            } else {
                listener.onFailure(task.getException());
            }
        });
    }
}
