package com.example.p2pchat.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public record MessageData(String id, String name, String message, LocalDateTime timeStamp) implements Serializable{
}
