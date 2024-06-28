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

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import tlu.cse.ht63.cuoiky.R;

public class ChangePasswordFragment extends Fragment {

    private EditText editTextCurrentPassword, editTextNewPassword, editTextConfirmPassword;
    private Button btnChangePassword;
    private ImageView btnBack;

    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        editTextCurrentPassword = view.findViewById(R.id.editTextCurrentPassword);
        editTextNewPassword = view.findViewById(R.id.editTextNewPassword);
        editTextConfirmPassword = view.findViewById(R.id.editTextConfirmPassword);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnBack = view.findViewById(R.id.btnBack);

        btnChangePassword.setOnClickListener(v -> changePassword());
        btnBack.setOnClickListener(v -> getActivity().onBackPressed());
    }

    private void changePassword() {
        String currentPassword = editTextCurrentPassword.getText().toString().trim();
        String newPassword = editTextNewPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();


        if (TextUtils.isEmpty(currentPassword)) {
            editTextCurrentPassword.setError("Vui lòng nhập mật khẩu hiện tại");
            return;
        }

        if (TextUtils.isEmpty(newPassword)) {
            editTextNewPassword.setError("Vui lòng nhập mật khẩu mới");
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            editTextConfirmPassword.setError("Vui lòng nhập lại mật khẩu mới");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            editTextConfirmPassword.setError("Mật khẩu xác nhận không khớp");
            return;
        }


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);

            user.reauthenticate(credential)
                    .addOnSuccessListener(aVoid -> {
                        // Update password
                        user.updatePassword(newPassword)
                                .addOnSuccessListener(aVoid1 -> {
                                    Toast.makeText(getContext(), "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                                    getActivity().onBackPressed(); // Go back to previous screen
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "Đổi mật khẩu thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Xác thực thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(getContext(), "Không tìm thấy người dùng hiện tại", Toast.LENGTH_SHORT).show();
        }
    }

}
