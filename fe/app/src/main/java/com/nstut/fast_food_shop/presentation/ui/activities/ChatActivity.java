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
import com.nstut.fast_food_shop.data.local.dao.ProductDao;
import com.nstut.fast_food_shop.data.local.db.AppDatabase;
import com.nstut.fast_food_shop.data.models.ChatMessage;
import com.nstut.fast_food_shop.data.models.ProductRoom;
import com.nstut.fast_food_shop.data.remote.GenerativeModel;
import com.nstut.fast_food_shop.presentation.ui.adapters.ChatAdapter;
import com.nstut.fast_food_shop.repository.CartRepository;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ChatActivity extends BaseActivity implements ChatAdapter.OnProductClickListener, ChatAdapter.OnAddToCartClickListener {

    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;
    private List<Content> chatHistory;
    private EditText messageInput;
    private ImageButton sendButton;
    private GenerativeModel geminiPro;
    private Executor mainExecutor;
    private ProductDao productDao;
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
        productDao = AppDatabase.getInstance(this).productDao();
        cartRepository = new CartRepository(this);

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

                ListenableFuture<List<ProductRoom>> productsFuture = JdkFutureAdapters.listenInPoolThread(AppDatabase.databaseWriteExecutor.submit(() -> productDao.getAllProductsList()));
                Futures.addCallback(productsFuture, new FutureCallback<List<ProductRoom>>() {
                    @Override
                    public void onSuccess(List<ProductRoom> products) {
                        String menu = formatMenu(products);
                        ListenableFuture<GenerateContentResponse> responseFuture = geminiPro.getResponse(messageText, menu, getHistory());
                        Futures.addCallback(responseFuture, new FutureCallback<GenerateContentResponse>() {
                            @Override
                            public void onSuccess(GenerateContentResponse result) {
                                String botResponse = result.getText();
                                runOnUiThread(() -> {
                                    String jsonString = extractJson(botResponse);
                                    if (jsonString != null) {
                                        try {
                                            String cleanBotResponse = botResponse.replace(jsonString, "").trim();
                                            JSONObject jsonObject = new JSONObject(jsonString);
                                            JSONArray productArray = jsonObject.getJSONArray("products");
                                            List<ProductRoom> recommendedProducts = new ArrayList<>();
                                            for (int i = 0; i < productArray.length(); i++) {
                                                JSONObject productObject = productArray.getJSONObject(i);
                                                String productName = productObject.getString("product_name");
                                                ProductRoom recommendedProduct = findProductByName(products, productName);
                                                if (recommendedProduct != null) {
                                                    recommendedProducts.add(recommendedProduct);
                                                }
                                            }
                                            if (!recommendedProducts.isEmpty()) {
                                                addMessageToHistory(new ChatMessage(cleanBotResponse, recommendedProducts));
                                            } else {
                                                addMessageToHistory(new ChatMessage(cleanBotResponse, false));
                                            }
                                        } catch (JSONException e) {
                                            addMessageToHistory(new ChatMessage(botResponse, false));
                                        }
                                    } else {
                                        addMessageToHistory(new ChatMessage(botResponse, false));
                                    }
                                });
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                t.printStackTrace();
                                runOnUiThread(() -> {
                                    chatMessages.add(new ChatMessage("Sorry, something went wrong.", false));
                                    chatAdapter.notifyItemInserted(chatMessages.size() - 1);
                                    chatRecyclerView.scrollToPosition(chatMessages.size() - 1);
                                });
                            }
                        }, mainExecutor);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        // Handle failure to fetch products
                    }
                }, mainExecutor);
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

    private String formatMenu(List<ProductRoom> products) {
        StringBuilder menuBuilder = new StringBuilder("Here is our menu:\n");
        for (ProductRoom product : products) {
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

    private ProductRoom findProductByName(List<ProductRoom> products, String name) {
        for (ProductRoom product : products) {
            if (product.getName().equalsIgnoreCase(name)) {
                return product;
            }
        }
        return null;
    }

    @Override
    public void onProductClick(ProductRoom product) {
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT_ID, product.getId());
        startActivity(intent);
    }

    @Override
    public void onAddToCartClick(ProductRoom product) {
        cartRepository.addItemToCart(product, 1);
        Toast.makeText(this, "Added " + product.getName() + " to cart", Toast.LENGTH_SHORT).show();
        updateCartBadge();
    }
}