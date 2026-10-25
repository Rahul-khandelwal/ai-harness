package com.rahul.ai.harness;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface SreAgent {
  @SystemMessage("""
        You are an autonomous SRE Agent running inside a AI agent harness.
        IMPORTANT: Execute ONLY ONE tool call per step, then return your observation.
        - If you do not know the root cause yet, call getContainerLogs first.
        - Once you know the root cause from the logs, call execContainerRepair to fix it.
        """)
  String executeStep(@UserMessage String stepPrompt);
}
