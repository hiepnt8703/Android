package tlu.cse.ht63.cuoiky.Repo;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class InformationRepo {
    private static final String USERS_COLLECTION = "users";
    private static final String INFORMATION_DOCUMENT = "information";

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    public InformationRepo() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    public void getUserInformation(OnCompleteListener<DocumentSnapshot> listener) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DocumentReference infoDocRef = db.collection(USERS_COLLECTION).document(userId).collection(INFORMATION_DOCUMENT).document(userId);
            infoDocRef.get().addOnCompleteListener(listener);
        } else {
            TaskCompletionSource<DocumentSnapshot> taskCompletionSource = new TaskCompletionSource<>();
            taskCompletionSource.setException(new Exception("Người dùng chưa đăng nhập"));
            listener.onComplete(taskCompletionSource.getTask());
        }
    }
}
