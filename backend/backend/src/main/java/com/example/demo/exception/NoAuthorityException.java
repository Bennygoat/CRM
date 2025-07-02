package com.example.demo.exception;

public class NoAuthorityException extends RuntimeException {
  public NoAuthorityException(String message) {
    super(message);
  }
}