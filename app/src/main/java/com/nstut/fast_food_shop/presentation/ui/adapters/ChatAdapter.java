package com.nstut.fast_food_shop.presentation.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nstut.fast_food_shop.R;
import com.nstut.fast_food_shop.data.models.ChatMessage;
import com.nstut.fast_food_shop.data.models.ProductRoom;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;
    private static final int VIEW_TYPE_RECOMMENDATION = 3;

    private List<ChatMessage> messages;
    private OnProductClickListener onProductClickListener;

    public interface OnProductClickListener {
        void onProductClick(ProductRoom product);
    }

    public ChatAdapter(List<ChatMessage> messages, OnProductClickListener onProductClickListener) {
        this.messages = messages;
        this.onProductClickListener = onProductClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = messages.get(position);
        if (message.isSentByUser()) {
            return VIEW_TYPE_SENT;
        } else if (message.isRecommendation()) {
            return VIEW_TYPE_RECOMMENDATION;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_SENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_sent, parent, false);
            return new ChatViewHolder(view);
        } else if (viewType == VIEW_TYPE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_received, parent, false);
            return new ChatViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_recommendation, parent, false);
            return new RecommendationViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        if (holder.getItemViewType() == VIEW_TYPE_SENT || holder.getItemViewType() == VIEW_TYPE_RECEIVED) {
            ((ChatViewHolder) holder).messageText.setText(message.getMessage());
        } else {
            ((RecommendationViewHolder) holder).bind(message.getProduct(), onProductClickListener);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;

        ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_text);
        }
    }

    static class RecommendationViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName;
        TextView productDescription;
        TextView productPrice;

        RecommendationViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            productDescription = itemView.findViewById(R.id.product_description);
            productPrice = itemView.findViewById(R.id.product_price);
        }

        void bind(final ProductRoom product, final OnProductClickListener listener) {
            productName.setText(product.getName());
            productDescription.setText(product.getDescription());
            productPrice.setText(String.format("$%.2f", product.getPrice()));
            Glide.with(itemView.getContext()).load(product.getImageUrl()).into(productImage);
            itemView.setOnClickListener(v -> listener.onProductClick(product));
        }
    }
}