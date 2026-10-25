package com.rahul.ai.harness;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
@RequestMapping("/incident")
public class IncidentHandler {
  private static final int MAX_STEPS = 5;
  private final SreAgent sreAgent;
  private final RestClient restClient;

  public IncidentHandler(SreAgent sreAgent) {
    this.sreAgent = sreAgent;
    this.restClient = RestClient.create();
  }

  // 1. Event Trigger Webhook (Called by order-service when it gets a 503)
  @PostMapping
  public String onIncidentEvent(@RequestBody String alertPayload) {
    System.out.println("INCIDENT EVENT RECEIVED: " + alertPayload);
    return runHarness(alertPayload);
  }

  // 2. The Autonomous Agent Harness Loop
  private String runHarness(String alertPayload) {
    for (int step = 0; step < MAX_STEPS; step++) {
      System.out.printf("=== HARNESS STEP {%d} OF {%d} ===%n", step, MAX_STEPS);

      String prompt = String.format(
          "Step %d/%d. Incident Alert: %s. Service is currently UNHEALTHY (503). Take the next single action.",
          step, MAX_STEPS, alertPayload
      );

      String agentResponse = sreAgent.executeStep(prompt);
      System.out.printf("Agent Action Summary: %s%n", agentResponse);

      // Deterministic Verification: Do NOT trust the LLM, verify via HTTP GET
      if (isServiceHealthy("http://notification-service:8082/health")) {
        System.out.printf("VERIFICATION PASSED: Service returned 200 OK on Step {%d}!", step);
        String report = sreAgent.executeStep(
            "The service is now healthy (200 OK). Write a brief Root Cause Analysis Incident Report."
        );
        System.out.printf("\n=== FINAL INCIDENT REPORT ===\n%s%n", report);
        return report;
      }

      System.out.printf("VERIFICATION FAILED: Service still unhealthy after Step {%d}. Continuing loop...%n", step);
    }

    System.out.printf("MAX_STEPS ({%d}) exceeded! Cutting off LLM and escalating to human on-call.%n", MAX_STEPS);
    return "ESCALATED: Agent Harness could not resolve incident within " + MAX_STEPS + " steps.";
  }

  private boolean isServiceHealthy(String healthUrl) {
    try {
      var response = restClient.get().uri(healthUrl).retrieve().toBodilessEntity();
      return response.getStatusCode().is2xxSuccessful();
    } catch (Exception e) {
      return false;
    }
  }
}
