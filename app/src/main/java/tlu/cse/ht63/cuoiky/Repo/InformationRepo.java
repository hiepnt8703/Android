package tlu.cse.ht63.cuoiky.Repo;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import tlu.cse.ht63.cuoiky.Model.Information;

public class InformationRepo {
    private static final String USERS_COLLECTION = "users";
    private static final String INFORMATION_COLLECTION = "information";
    private static final String PROFILE_DOCUMENT = "profile";

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    public InformationRepo() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    public interface InformationCallback {
        void onSuccess(Information information);
        void onFailure(Exception e);
    }

    public void getUserInformation(final InformationCallback callback) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DocumentReference profileRef = db.collection(USERS_COLLECTION)
                    .document(userId)
                    .collection(INFORMATION_COLLECTION)
                    .document(PROFILE_DOCUMENT);

            profileRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Information information = document.toObject(Information.class);
                            callback.onSuccess(information);
                        } else {
                            callback.onFailure(new Exception("No such document"));
                        }
                    } else {
                        callback.onFailure(task.getException());
                    }
                }
            });
        } else {
            callback.onFailure(new Exception("User not authenticated"));
        }
    }
}
