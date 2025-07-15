package com.nstut.fast_food_shop.presentation.ui.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.gson.Gson;
import com.nstut.fast_food_shop.R;
import com.nstut.fast_food_shop.adapter.TransactionAdapter;
import com.nstut.fast_food_shop.data.models.User;
import com.nstut.fast_food_shop.model.Order;
import java.util.ArrayList;
import java.util.List;

public class TransactionHistoryActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private TransactionAdapter transactionAdapter;
    private List<Order> orderList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        db = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.recycler_view_transactions);
        orderList = new ArrayList<>();
        transactionAdapter = new TransactionAdapter(this, orderList);
        recyclerView.setAdapter(transactionAdapter);

        loadOrders();
    }

    private void loadOrders() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String userJson = sharedPreferences.getString("user", null);
        if (userJson == null) {
            // Handle user not logged in
            return;
        }
        User user = new Gson().fromJson(userJson, User.class);
        String userId = String.valueOf(user.userId);
        db.collection("orders")
                .whereEqualTo("userId", userId)
                .orderBy("orderDate", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        orderList.clear();
                        orderList.addAll(task.getResult().toObjects(Order.class));
                        transactionAdapter.notifyDataSetChanged();
                    }
                });
    }
}