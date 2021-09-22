package org.example.framework.servlet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.framework.attribute.ContextAttributes;
import org.example.framework.attribute.RequestAttributes;
import org.example.framework.util.KeyValue;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// 1. Classic Web Applications
// 2. Web Application
public class FrontServlet extends HttpServlet {
  private Map<Pattern, Map<String, Handler>> routes;

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    routes = ((Map<Pattern, Map<String, Handler>>) getServletContext().getAttribute(ContextAttributes.ROUTES_ATTR));
  }

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    try {
      final var keyValue = findHandler(req);
      // keyValue.getKey().group("cardId");
      req.setAttribute(RequestAttributes.PATH_MATCHER_ATTR, keyValue.getKey());
      final var handler = keyValue.getValue();
      handler.handle(req, resp);
    } catch (Exception e) {
      e.printStackTrace();
      ErrorController.internalServerError(req, resp);
      // TODO: handle
    }
  }

  private String getPath(HttpServletRequest req) {
    return req.getRequestURI().substring(req.getContextPath().length());
  }

  private KeyValue<Matcher, Handler> findHandler(HttpServletRequest req) {
    final var path = getPath(req);

    for (final var routeEntry : routes.entrySet()) {
      final var matcher = routeEntry.getKey().matcher(path);
      if (!matcher.matches()) {
        continue;
      }

      for (final var entry : routeEntry.getValue().entrySet()) {
        if (!entry.getKey().equals(req.getMethod())) {
          continue;
        }

        return new KeyValue<>(matcher, entry.getValue());
      }

      return new KeyValue<>(null, ErrorController::methodNotAllowed);
    }

    return new KeyValue<>(null, ErrorController::notFound);
  }
}
