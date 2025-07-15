package com.nstut.fast_food_shop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.nstut.fast_food_shop.R;
import com.nstut.fast_food_shop.model.Order;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private Context context;
    private List<Order> orderList;

    public TransactionAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.orderId.setText("Order ID: " + order.getOrderId());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        holder.orderDate.setText("Date: " + sdf.format(order.getOrderDate()));
        holder.totalAmount.setText("Total: $" + String.format(Locale.getDefault(), "%,.2f", order.getTotalAmount()));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView orderId, orderDate, totalAmount;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.text_view_order_id);
            orderDate = itemView.findViewById(R.id.text_view_order_date);
            totalAmount = itemView.findViewById(R.id.text_view_total_amount);
        }
    }
}