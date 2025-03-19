package com.luka.gamesellerrating.service.impl;

import com.luka.gamesellerrating.service.AIService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AIServiceImpl implements AIService {

    private final ChatClient chatClient;

    public AIServiceImpl(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public String analyzeSentiment(String comment) {
        return chatClient.prompt().user(comment).call().content();
    }
}
