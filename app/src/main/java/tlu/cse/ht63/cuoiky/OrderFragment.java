package tlu.cse.ht63.cuoiky;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class OrderFragment extends Fragment {

    private TextView textProcessing;
    private TextView textCompleted;

    public OrderFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        textProcessing = view.findViewById(R.id.textProcessing);
        textCompleted = view.findViewById(R.id.textCompleted);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Default fragment is ProcessingFragment
        if (savedInstanceState == null) {
            replaceFragment(new ProcessingFragment());
            textProcessing.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            textCompleted.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        }

        textProcessing.setOnClickListener(v -> {
            replaceFragment(new ProcessingFragment());
            textProcessing.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            textCompleted.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        });

        textCompleted.setOnClickListener(v -> {
            replaceFragment(new CompletedFragment());
            textCompleted.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            textProcessing.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.commit();
    }
}
