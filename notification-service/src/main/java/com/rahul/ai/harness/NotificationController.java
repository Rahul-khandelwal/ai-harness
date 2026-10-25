package com.rahul.ai.harness;

import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notification")
public class NotificationController {

  @GetMapping("/health")
  public ResponseEntity<String> getHealth() {
    if (Files.exists(Path.of("/tmp/service.lock"))) {
      System.out.println("LockAcquisitionException: Stale lock file found at /tmp/service.lock. Rejecting request.");
      return ResponseEntity.status(503).body("LockAcquisitionException: Service is locked");
    }
    return ResponseEntity.ok("Notification service is up and running!");
  }

  @PostMapping
  public ResponseEntity<String> notify(@RequestBody NotificationRequest notificationRequest) {
    System.out.println("Notification received : " + notificationRequest);
    if (Files.exists(Path.of("/tmp/service.lock"))) {
      System.out.println("LockAcquisitionException: Stale lock file found at /tmp/service.lock. Rejecting request.");
      return ResponseEntity.status(503).body("LockAcquisitionException: Service is locked");
    }
    return ResponseEntity.ok("Notification sent!");
  }

  public static record NotificationRequest(String message) {}
}