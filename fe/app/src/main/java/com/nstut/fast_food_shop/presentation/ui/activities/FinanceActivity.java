package com.nstut.fast_food_shop.presentation.ui.activities;

import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.graphics.Color;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.nstut.fast_food_shop.R;
import com.nstut.fast_food_shop.adapter.TransactionAdapter;
import com.nstut.fast_food_shop.data.local.db.AppDatabase;
import com.nstut.fast_food_shop.model.Order;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FinanceActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private TransactionAdapter transactionAdapter;
    private List<Order> orderList;
    private AppDatabase db;
    private LineChart lineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finance);
        setupHeader(findViewById(R.id.secondary_header), false);

        db = AppDatabase.getInstance(this);

        recyclerView = findViewById(R.id.recycler_view_transactions);
        lineChart = findViewById(R.id.line_chart);
        orderList = new ArrayList<>();
        transactionAdapter = new TransactionAdapter(this, orderList);
        recyclerView.setAdapter(transactionAdapter);

        loadOrders();
    }

    private void loadOrders() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<Order> orders = db.orderDao().getAllOrders();
            runOnUiThread(() -> {
                orderList.clear();
                orderList.addAll(orders);
                transactionAdapter.notifyDataSetChanged();
                setupChart(orders);
            });
        });
    }

    private void setupChart(List<Order> orders) {
        Map<String, Double> dailyRevenue = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", Locale.getDefault());

        for (Order order : orders) {
            String date = sdf.format(order.getOrderDate());
            dailyRevenue.put(date, dailyRevenue.getOrDefault(date, 0.0) + order.getTotalAmount());
        }

        List<Entry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>(dailyRevenue.keySet());
        java.util.Collections.sort(labels);

        for (int i = 0; i < labels.size(); i++) {
            entries.add(new Entry(i, dailyRevenue.get(labels.get(i)).floatValue()));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Daily Revenue");
        dataSet.setColor(Color.BLUE);
        dataSet.setValueTextColor(Color.BLACK);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index >= 0 && index < labels.size()) {
                    return labels.get(index);
                }
                return "";
            }
        });
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);

        lineChart.getDescription().setEnabled(false);
        lineChart.invalidate(); // refresh
    }
}