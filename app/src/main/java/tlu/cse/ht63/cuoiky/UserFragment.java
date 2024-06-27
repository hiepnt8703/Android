package tlu.cse.ht63.cuoiky;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

public class UserFragment extends Fragment {

    private TextView btnEditProfile;
    private TextView btnFeedback;
    private TextView btnContact;
    private TextView btnChangePassword;
    private TextView btnLogout;

    public UserFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnFeedback = view.findViewById(R.id.btnFeedback);
        btnContact = view.findViewById(R.id.btnContact);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnLogout = view.findViewById(R.id.btnLogout);

        // Set click listeners for each button (implementing their respective functionalities)
        btnEditProfile.setOnClickListener(v -> {
            // Code to navigate to EditProfileFragment
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new EditProfileFragment())
                    .addToBackStack(null)
                    .commit();
        });

        btnFeedback.setOnClickListener(v -> {
            // Code to provide feedback
        });

        btnContact.setOnClickListener(v -> {
            // Code to contact
        });

        btnChangePassword.setOnClickListener(v -> {
            // Code to change password
        });

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }
}
