package org.example.framework.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ErrorController {
  public static void notFound(HttpServletRequest req, HttpServletResponse resp) {
    sendError(req, resp, 404, "Not Found");
  }

  public static void methodNotAllowed(HttpServletRequest req, HttpServletResponse resp) {
    sendError(req, resp, 405, "Method Not Allowed");
  }

  public static void internalServerError(HttpServletRequest req, HttpServletResponse resp) {
    sendError(req, resp, 500, "Internal Server Error");
  }

  private static void sendError(HttpServletRequest req, HttpServletResponse resp, int code, String message) {
    try {
      resp.sendError(code, message);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
