package org.example.framework.util;

import lombok.Value;

@Value
public class KeyValue<K, V> {
  K key;
  V value;
}
