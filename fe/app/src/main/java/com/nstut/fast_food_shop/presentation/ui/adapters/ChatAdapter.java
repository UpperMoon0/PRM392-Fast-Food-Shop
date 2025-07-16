package com.nstut.fast_food_shop.presentation.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.nstut.fast_food_shop.R;
import com.nstut.fast_food_shop.data.models.ChatMessage;
import com.nstut.fast_food_shop.model.Product;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;
    private static final int VIEW_TYPE_RECOMMENDATION = 3;

    private List<ChatMessage> messages;
    private OnProductClickListener onProductClickListener;
    private OnAddToCartClickListener onAddToCartClickListener;

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public interface OnAddToCartClickListener {
        void onAddToCartClick(Product product);
    }

    public ChatAdapter(List<ChatMessage> messages, OnProductClickListener onProductClickListener, OnAddToCartClickListener onAddToCartClickListener) {
        this.messages = messages;
        this.onProductClickListener = onProductClickListener;
        this.onAddToCartClickListener = onAddToCartClickListener;
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
        } else if (holder.getItemViewType() == VIEW_TYPE_RECOMMENDATION) {
            ((RecommendationViewHolder) holder).bind(message, onProductClickListener, onAddToCartClickListener);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void addMessage(ChatMessage message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;

        ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_text);
        }
    }

    static class RecommendationViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        RecyclerView productsRecyclerView;
        ProductAdapter productAdapter;

        RecommendationViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_text);
            productsRecyclerView = itemView.findViewById(R.id.products_recycler_view);
        }

        void bind(final ChatMessage message, final OnProductClickListener productClickListener, final OnAddToCartClickListener addToCartClickListener) {
            messageText.setText(message.getMessage());
            if (message.getProducts() != null && !message.getProducts().isEmpty()) {
                productsRecyclerView.setVisibility(View.VISIBLE);
                productAdapter = new ProductAdapter(message.getProducts(),
                        product -> productClickListener.onProductClick(product),
                        product -> addToCartClickListener.onAddToCartClick(product));
                productsRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
                productsRecyclerView.setAdapter(productAdapter);
            } else {
                productsRecyclerView.setVisibility(View.GONE);
            }
        }
    }
}