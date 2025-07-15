package com.nstut.fast_food_shop.data.remote;

import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.ai.client.generativeai.type.GenerationConfig;
import com.google.ai.client.generativeai.type.SafetySetting;
import com.google.common.util.concurrent.ListenableFuture;
import com.nstut.fast_food_shop.BuildConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GenerativeModel {

    private final GenerativeModelFutures generativeModel;
    private final Content systemContent;

    public GenerativeModel() {
        String apiKey = BuildConfig.GEMINI_API_KEY;
        String systemInstruction = "You are a friendly and helpful chatbot for a fast-food restaurant. " +
                "Your goal is to help customers choose items from the menu. " +
                "You can ask clarifying questions to understand their preferences, such as taste, " +
                "dietary restrictions, or price range. Based on their answers, you should recommend " +
                "specific menu items. When you recommend a product, you should respond with a JSON object " +
                "containing the product's name, for example: {\"products\": [{\"product_name\": \"Cheeseburger\"}, {\"product_name\": \"Fries\"}]}. " +
                "Be polite and conversational.";

        systemContent = new Content.Builder()
                .addText(systemInstruction)
                .build();

        com.google.ai.client.generativeai.GenerativeModel gm = new com.google.ai.client.generativeai.GenerativeModel(
                "gemini-2.5-flash",
                apiKey,
                new GenerationConfig.Builder().build(),
                Collections.emptyList()
        );
        generativeModel = GenerativeModelFutures.from(gm);
    }

    public ListenableFuture<GenerateContentResponse> getResponse(String query, String menu, List<Content> history) {
        String fullPrompt = menu + "\n\nBased on this menu, help the customer find something they'll like.\n\n" + query;
        Content userContent = new Content.Builder()
                .addText(fullPrompt)
                .build();
        List<Content> contents = new ArrayList<>();
        contents.add(systemContent);
        contents.addAll(history);
        contents.add(userContent);

        return generativeModel.generateContent(contents.toArray(new Content[0]));
    }
}