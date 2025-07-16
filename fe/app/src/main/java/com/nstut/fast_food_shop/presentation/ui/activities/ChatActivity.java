package com.nstut.fast_food_shop.presentation.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.JdkFutureAdapters;
import com.google.common.util.concurrent.ListenableFuture;
import com.nstut.fast_food_shop.R;
import com.nstut.fast_food_shop.data.models.ChatMessage;
import com.nstut.fast_food_shop.model.Product;
import com.nstut.fast_food_shop.data.remote.GenerativeModel;
import com.nstut.fast_food_shop.presentation.ui.adapters.ChatAdapter;
import com.nstut.fast_food_shop.presentation.ui.adapters.ProductAdapter;
import com.nstut.fast_food_shop.repository.CartRepository;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ChatActivity extends BaseActivity implements ProductAdapter.OnProductClickListener, ProductAdapter.OnAddToCartClickListener {

    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;
    private List<Content> chatHistory;
    private EditText messageInput;
    private ImageButton sendButton;
    private GenerativeModel geminiPro;
    private Executor mainExecutor;
    private CartRepository cartRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatRecyclerView = findViewById(R.id.chat_recycler_view);
        messageInput = findViewById(R.id.message_input);
        sendButton = findViewById(R.id.send_button);

        geminiPro = new GenerativeModel();
        mainExecutor = Executors.newSingleThreadExecutor();
        cartRepository = new CartRepository();

        chatMessages = new ArrayList<>();
        chatHistory = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages, this, this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        chatRecyclerView.setLayoutManager(layoutManager);
        chatRecyclerView.setAdapter(chatAdapter);

        sendButton.setOnClickListener(v -> {
            String messageText = messageInput.getText().toString().trim();
            if (!messageText.isEmpty()) {
                addMessageToHistory(messageText, true);
                messageInput.setText("");

                // TODO: Call ProductRepository to get all products
                // For now, just sending a generic response
                addMessageToHistory(new ChatMessage("Sorry, I can't provide recommendations at the moment.", false));
            }
        });
    }

    private void addMessageToHistory(String text, boolean isUser) {
        addMessageToHistory(new ChatMessage(text, isUser));
    }

    private void addMessageToHistory(ChatMessage chatMessage) {
        chatMessages.add(chatMessage);
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        chatRecyclerView.scrollToPosition(chatMessages.size() - 1);
        if (!chatMessage.isRecommendation()) {
            Content.Builder contentBuilder = new Content.Builder().addText(chatMessage.getMessage());
            if (chatMessage.isSentByUser()) {
                contentBuilder.setRole("user");
            } else {
                contentBuilder.setRole("model");
            }
            chatHistory.add(contentBuilder.build());
            if (chatHistory.size() > 10) {
                chatHistory.remove(0);
            }
        }
    }

    private List<Content> getHistory() {
        return new ArrayList<>(chatHistory);
    }

    private String formatMenu(List<Product> products) {
        StringBuilder menuBuilder = new StringBuilder("Here is our menu:\n");
        for (Product product : products) {
            menuBuilder.append("- ")
                    .append(product.getName())
                    .append(": ")
                    .append(product.getDescription())
                    .append(" Price: $")
                    .append(product.getPrice())
                    .append("\n");
        }
        return menuBuilder.toString();
    }

    private String extractJson(String text) {
        int startIndex = text.indexOf('{');
        int endIndex = text.lastIndexOf('}');
        if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
            return text.substring(startIndex, endIndex + 1);
        }
        return null;
    }

    private Product findProductByName(List<Product> products, String name) {
        for (Product product : products) {
            if (product.getName().equalsIgnoreCase(name)) {
                return product;
            }
        }
        return null;
    }

    @Override
    public void onProductClick(Product product) {
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT_ID, product.getId());
        startActivity(intent);
    }

    @Override
    public void onAddToCartClick(Product product) {
        cartRepository.addItemToCart(getCurrentUser().getId(), String.valueOf(product.getId()), 1);
        Toast.makeText(this, "Added " + product.getName() + " to cart", Toast.LENGTH_SHORT).show();
        updateCartBadge();
    }
}