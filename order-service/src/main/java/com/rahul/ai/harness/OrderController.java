package com.rahul.ai.harness;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

@RestController
@RequestMapping("/order")
public class OrderController {

  private final RestClient restClient;

  public OrderController() {
    this.restClient = RestClient.builder().build();
  }

  @PostMapping
  public ResponseEntity<String> placeOrder(@RequestBody OrderRequest order) {
    System.out.println("Order received : " + order);
    try {
      restClient.post().uri("http://notification-service:8082/notification")
          .body(Map.of("message", "Order processed " + order))
          .retrieve().toBodilessEntity();
      return ResponseEntity.ok("Order processed successfully!");
    } catch (HttpServerErrorException.ServiceUnavailable ex) {
      // Automatically trigger the Agent Harness webhook
      restClient.post()
          .uri("http://incident-handler-service:8083/incident")
          .body("503 Service Unavailable when calling container 'notification-service': " + ex.getMessage())
          .retrieve().toBodilessEntity();
      return ResponseEntity.status(200).body("Order processed but Notification failed!");
    }
  }

  public static record OrderRequest(String product, float price, int quantity) {}
}