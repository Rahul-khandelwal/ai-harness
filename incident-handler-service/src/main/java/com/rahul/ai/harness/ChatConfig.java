package com.rahul.ai.harness;

import dev.langchain4j.community.model.CohereChatModel;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatConfig {

  @Bean
  public ChatModel geminiChatModel() {
//    return GoogleAiGeminiChatModel.builder()
//        .apiKey(System.getenv("GEMINI_API_KEY"))
//        .modelName(System.getenv("GEMINI_MODEL"))
//        .temperature(0.0)
//        .build();
    return CohereChatModel.builder()
        .apiKey(System.getenv("COHERE_API_KEY"))
        .modelName(System.getenv("COHERE_MODEL"))
        .temperature(0.0)
        .build();
  }

  @Bean
  public ChatMemory chatMemory() {
    return MessageWindowChatMemory.withMaxMessages(10);
  }
}
