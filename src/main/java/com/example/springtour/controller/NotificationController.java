package com.example.springtour.controller;

import com.example.springtour.model.PushNotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@Slf4j
@RequiredArgsConstructor
public class NotificationController {



    // Example endpoint to send a push notification
     @PostMapping("/send")
     public ResponseEntity<String> sendPushNotification(@RequestBody PushNotificationRequest request) {
         // Logic to send push notification

         log.info("Request {}", request);
         return ResponseEntity.ok("Push notification sent successfully");
     }

}
