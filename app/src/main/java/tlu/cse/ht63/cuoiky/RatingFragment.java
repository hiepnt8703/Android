package tlu.cse.ht63.cuoiky;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;
import java.util.Map;

import tlu.cse.ht63.cuoiky.Adapter.RatingAdapter;
import tlu.cse.ht63.cuoiky.Model.Order;
import tlu.cse.ht63.cuoiky.Repo.OrderRepo;
import tlu.cse.ht63.cuoiky.Repo.RatingRepo;

public class RatingFragment extends Fragment {

    private RecyclerView recyclerViewRatings;
    private ImageView backArrow;
    private RatingAdapter ratingAdapter;
    private List<Map<String, Object>> items;
    private Button btnSubmitRating;
    private RatingRepo ratingRepo;
    private OrderRepo orderRepo;
    private String orderId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rating, container, false);

        btnSubmitRating = view.findViewById(R.id.buttonSubmitRating);
        recyclerViewRatings = view.findViewById(R.id.recyclerViewRatings);
        backArrow = view.findViewById(R.id.back_arrow);

        ratingRepo = new RatingRepo();
        orderRepo = new OrderRepo();

        recyclerViewRatings.setLayoutManager(new LinearLayoutManager(getContext()));

        Bundle args = getArguments();
        if (args != null && args.containsKey("order")) {
            Order order = (Order) args.getSerializable("order");
            if (order != null) {
                items = order.getItems();
                orderId = order.getOrderId();
                setupRecyclerView();
            }
        }

        backArrow.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        btnSubmitRating.setOnClickListener(v -> submitRatings());

        return view;
    }

    private void setupRecyclerView() {
        ratingAdapter = new RatingAdapter(getContext(), items);
        recyclerViewRatings.setAdapter(ratingAdapter);
    }

    private void submitRatings() {
        if (items != null) {
            for (Map<String, Object> item : items) {
                String productId = (String) item.get("productId");
                Number ratingNumber = (Number) item.get("rating");
                long rating = ratingNumber.longValue();
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                ratingRepo.submitRating(productId, userId, rating, new RatingRepo.OnRatingSubmissionListener() {
                    @Override
                    public void onSuccess() {
                        if (getContext() != null) {
                    //        Toast.makeText(getContext(), "Rating submitted successfully", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        if (getContext() != null) {
                        //    Toast.makeText(getContext(), "Failed to submit rating: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            orderRepo.deleteOrder(orderId, new OrderRepo.DeleteOrderCallback() {
                @Override
                public void onSuccess() {
                    if (getContext() != null) {
                    //    Toast.makeText(getContext(), "Order deleted successfully", Toast.LENGTH_SHORT).show();
                        if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                            getParentFragmentManager().popBackStack();
                        }
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    if (getContext() != null) {
                 //       Toast.makeText(getContext(), "Failed to delete order: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            Toast.makeText(getContext(),"Cảm ơn bạn đã đánh giá",Toast.LENGTH_SHORT).show();
        }
    }
}
