package org.example.app.handler;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.example.app.domain.User;
import org.example.app.dto.OrderDto;
import org.example.app.service.CardService;
import org.example.app.util.UserHelper;
import org.example.framework.attribute.RequestAttributes;

import java.io.IOException;
import java.util.logging.Level;
import java.util.regex.Matcher;

@Log
@RequiredArgsConstructor
public class CardHandler { // Servlet -> Controller -> Service (domain) -> domain
  private final CardService service;
  private final Gson gson;

  public void getAll(HttpServletRequest req, HttpServletResponse resp) {
    try {
      // cards.getAll?ownerId=1
      final var user = UserHelper.getUser(req);
      final var data = service.getAllByOwnerId(user.getId());
      resp.setHeader("Content-Type", "application/json");
      resp.getWriter().write(gson.toJson(data));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void getById(HttpServletRequest req, HttpServletResponse resp) {
    try {
      final var cardId = Long.parseLong(((Matcher) req.getAttribute(RequestAttributes.PATH_MATCHER_ATTR)).group("cardId"));
      final var user = UserHelper.getUser(req);
      final var data = service.getById(user, cardId);
      resp.setHeader("Content-Type", "application/json");
      resp.getWriter().write(gson.toJson(data));
    } catch (IOException e) {
      e.printStackTrace();
    }
    log.log(Level.INFO, "getById");
  }

  public void order(HttpServletRequest req, HttpServletResponse resp) {
    try {
      final var orderDto = gson.fromJson(req.getReader(), OrderDto.class);
      final var user = UserHelper.getUser(req);
      final var data = service.doOrder(user, orderDto.getFromCardNumber(), orderDto.getToCardNumber(), orderDto.getMoney());
      resp.setHeader("Content-Type", "application/json");
      resp.getWriter().write(gson.toJson(data));
    } catch (IOException e) {
      e.printStackTrace();
    }
    log.log(Level.INFO, "order");
  }

  public void blockByNumber(HttpServletRequest req, HttpServletResponse resp) {
    try {
      final var cardNumber = ((Matcher) req.getAttribute(RequestAttributes.PATH_MATCHER_ATTR)).group("cardNumber");
      final var user = UserHelper.getUser(req);
      final var data = service.blockCard(user, cardNumber);
      resp.setHeader("Content-Type", "application/json");
      resp.getWriter().write(gson.toJson(data));
    } catch (IOException e) {
      e.printStackTrace();
    }
    log.log(Level.INFO, "blockByNumber");
  }

  public void create(HttpServletRequest req, HttpServletResponse resp) {
    try {
      final var cardBalance = Long.parseLong(((Matcher) req.getAttribute(RequestAttributes.PATH_MATCHER_ATTR)).group("cardBalance"));
      final var user = UserHelper.getUser(req);
      final var data = service.createNewCard(user, cardBalance);
      resp.setHeader("Content-Type", "application/json");
      resp.getWriter().write(gson.toJson(data));
    } catch (IOException e) {
      e.printStackTrace();
    }
    log.log(Level.INFO, "blockByNumber");
  }
}
