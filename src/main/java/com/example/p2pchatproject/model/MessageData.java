package com.example.p2pchatproject.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public record MessageData(String username, String uuid, String message,LocalDateTime timeStamp) implements Serializable{
}
