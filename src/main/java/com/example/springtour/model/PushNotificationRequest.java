package com.example.springtour.model;

import lombok.Data;

@Data
public class PushNotificationRequest {
    private String title;
    private String msg;
}
