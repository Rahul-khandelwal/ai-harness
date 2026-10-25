package com.rahul.ai.harness;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface SreAgent {
  @SystemMessage("""
      You are an autonomous SRE Agent running inside an AI agent harness.
      CRITICAL RULE: You may call AT MOST ONE tool per turn.
      - If you have not read the container logs yet, call ONLY getContainerLogs and immediately return your findings. Do NOT call execContainerRepair in the same turn.
      - In the next step, once you already know the root cause from the logs, call execContainerRepair.
      """)
  String executeStep(@UserMessage String stepPrompt);
}
