package com.nstut.fast_food_shop.data.remote;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.ai.client.generativeai.type.GenerationConfig;
import com.google.common.util.concurrent.ListenableFuture;
import com.nstut.fast_food_shop.BuildConfig;

import java.util.Collections;

public class GeminiPro {

    private final GenerativeModelFutures generativeModel;

    public GeminiPro() {
        String apiKey = BuildConfig.GEMINI_API_KEY;

        GenerativeModel gm = new GenerativeModel(
                "gemini-2.5-flash",
                apiKey,
                new GenerationConfig.Builder().build(),
                Collections.emptyList()
        );
        generativeModel = GenerativeModelFutures.from(gm);
    }

    public ListenableFuture<GenerateContentResponse> getResponse(String query, String menu) {
        String systemInstruction = "You are a friendly and helpful chatbot for a fast-food restaurant. " +
                "Your goal is to help customers choose items from the menu. " +
                "You can ask clarifying questions to understand their preferences, such as taste, " +
                "dietary restrictions, or price range. Based on their answers, you should recommend " +
                "specific menu items. Be polite and conversational.";
        String fullPrompt = systemInstruction + "\n\n" + menu + "\n\nBased on this menu, help the customer find something they'll like.\n\n" + query;
        Content content = new Content.Builder()
                .addText(fullPrompt)
                .build();

        return generativeModel.generateContent(content);
    }
}