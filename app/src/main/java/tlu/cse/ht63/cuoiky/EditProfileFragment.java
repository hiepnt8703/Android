package tlu.cse.ht63.cuoiky;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import tlu.cse.ht63.cuoiky.Model.Information;
import tlu.cse.ht63.cuoiky.R;

public class EditProfileFragment extends Fragment {

    private EditText editTextName, editTextPhone, editTextAddress;
    private Button btnSaveChanges;
    private ImageView btnBack;
    private FirebaseFirestore db;
    private String userId;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editTextName = view.findViewById(R.id.editTextName);
        editTextPhone = view.findViewById(R.id.editTextPhone);
        editTextAddress = view.findViewById(R.id.editTextAddress);
        btnSaveChanges = view.findViewById(R.id.btnSaveChanges);
        btnBack = view.findViewById(R.id.btnBack);

        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        loadUserInfo();

        btnSaveChanges.setOnClickListener(v -> {

            String name = editTextName.getText().toString().trim();
            String phone = editTextPhone.getText().toString().trim();
            String address = editTextAddress.getText().toString().trim();

            // Validate input fields
            if (TextUtils.isEmpty(name)) {
                editTextName.setError("Tên không được rỗng");
                return;
            }
            if (TextUtils.isEmpty(phone)) {
                editTextPhone.setError("Số điện thoại không được rỗng");
                return;
            }
            if (TextUtils.isEmpty(address)) {
                editTextAddress.setError("Địa chỉ không được rỗng");
                return;
            }

            // Save or update user information
            saveOrUpdateUserInfo(name, phone, address);
        });

        // Set click listener for btnBack
        btnBack.setOnClickListener(v -> getActivity().onBackPressed());
    }

    private void loadUserInfo() {
        DocumentReference userDocRef = db.collection("users").document(userId);
        DocumentReference infoDocRef = userDocRef.collection("information").document("profile");

        infoDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Information userInfo = documentSnapshot.toObject(Information.class);
                if (userInfo != null) {
                    editTextName.setText(userInfo.getName());
                    editTextPhone.setText(userInfo.getPhoneNumber());
                    editTextAddress.setText(userInfo.getAddress());
                }
            } else {
              //  Toast.makeText(getContext(), "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Lỗi khi tải thông tin: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void saveOrUpdateUserInfo(String name, String phone, String address) {
        Information userInfo = new Information(name, phone, address);

        DocumentReference userDocRef = db.collection("users").document(userId);


        CollectionReference infoCollectionRef = userDocRef.collection("information");


        infoCollectionRef.document("profile").set(userInfo)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Thông tin đã được cập nhật", Toast.LENGTH_SHORT).show();
                    getActivity().onBackPressed();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Cập nhật thông tin thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
