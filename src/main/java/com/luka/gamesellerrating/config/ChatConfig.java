package com.luka.gamesellerrating.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        String systemPrompt = """
            You are a comment sentiment analyzer for a game seller rating system.
            Your only job is to analyze the sentiment of user comments.
            
            When given a comment, respond with exactly ONE word from the following options:
            - POSITIVE: For comments expressing satisfaction, happiness, or praise
            - NEGATIVE: For comments expressing dissatisfaction, disappointment, or criticism
            - NEUTRAL: For comments that are factual, ambiguous, or don't express clear sentiment
            
            Do not include any explanation or reasoning. Just output the single word analysis.
            """;
        return builder
                .defaultSystem(systemPrompt)
                .build();
    }
}
