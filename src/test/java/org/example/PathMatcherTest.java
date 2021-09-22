package org.example;

import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PathMatcherTest {
  @Test
  void matches() {
    final var pathPattern = "^/rest/cards/(?<cardId>\\d+)$";
    final var pattern = Pattern.compile(pathPattern);
    final var path = "/rest/cards/9999"; // {cardId}
    final var matcher = pattern.matcher(path);
    assertTrue(matcher.matches());
    assertEquals("9999", matcher.group(1));
    assertEquals("9999", matcher.group("cardId"));
  }
}
