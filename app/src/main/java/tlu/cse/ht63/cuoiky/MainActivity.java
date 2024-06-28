package tlu.cse.ht63.cuoiky;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                if (item.getItemId() == R.id.nav_home) {
                    selectedFragment = new HomeFragment();
                } else if (item.getItemId() == R.id.nav_cart) {
                    selectedFragment = new CartFragment();
                } else if (item.getItemId() == R.id.nav_user) {
                    selectedFragment = new UserFragment();
                } else if (item.getItemId() == R.id.nav_order) {
                    selectedFragment = new OrderFragment();
                } else if (item.getItemId() == R.id.nav_admin) {
                    selectedFragment = new AdminHomeFragment();
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment)
                            .commit();
                }

                return true;
            }
        });

        // Load HomeFragment as the default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }
        // Check user role to show admin menu item if necessary
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            checkUserRole(userId);
        }

    }
    private void checkUserRole(String userId) {
        db.collection("users").document(userId)
                .collection("information").document("profile")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String role = document.getString("role");
                            if ("Admin".equals(role)) {
                                // Show admin menu item
                                BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
                                bottomNavigationView.getMenu().findItem(R.id.nav_admin).setVisible(true);
                            } else {
                                // Hide admin menu item
                                BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
                                bottomNavigationView.getMenu().findItem(R.id.nav_admin).setVisible(false);
                            }
                        } else {
                            // Handle the case where document does not exist
                        }
                    } else {
                        // Handle errors
                    }
                });
    }
}
