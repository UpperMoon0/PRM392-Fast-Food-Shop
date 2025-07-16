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
import com.nstut.fast_food_shop.model.DailyRevenue;
import com.nstut.fast_food_shop.model.Order;
import com.nstut.fast_food_shop.repository.OrderRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FinanceActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private TransactionAdapter transactionAdapter;
    private List<Order> orderList;
    private LineChart lineChart;
    private OrderRepository orderRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finance);
        setupHeader(findViewById(R.id.secondary_header), false);


        recyclerView = findViewById(R.id.recycler_view_transactions);
        lineChart = findViewById(R.id.line_chart);
        orderList = new ArrayList<>();
        orderRepository = new OrderRepository();
        transactionAdapter = new TransactionAdapter(this, orderList);
        recyclerView.setAdapter(transactionAdapter);

        loadAllOrders();
        loadDailyRevenue();
    }

    private void loadAllOrders() {
        orderRepository.getAllOrders().enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    orderList.clear();
                    orderList.addAll(response.body());
                    transactionAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {

            }
        });
    }

    private void loadDailyRevenue() {
        orderRepository.getDailyRevenue().enqueue(new Callback<List<DailyRevenue>>() {
            @Override
            public void onResponse(Call<List<DailyRevenue>> call, Response<List<DailyRevenue>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    setupChart(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<DailyRevenue>> call, Throwable t) {

            }
        });
    }

    private void setupChart(List<DailyRevenue> dailyRevenues) {
        List<Entry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        Collections.sort(dailyRevenues, (o1, o2) -> o1.getDate().compareTo(o2.getDate()));


        for (int i = 0; i < dailyRevenues.size(); i++) {
            entries.add(new Entry(i, (float) dailyRevenues.get(i).getTotal()));
            labels.add(dailyRevenues.get(i).getDate());
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