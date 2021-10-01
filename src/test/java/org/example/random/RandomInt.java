package org.example.random;

import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.keygen.KeyGenerators;

import java.nio.ByteBuffer;

public class RandomInt implements BytesKeyGenerator {
    private static final int DEFAULT_KEY_LENGTH = 4;
    private final BytesKeyGenerator keyGenerator;
    private final int keyLenght;
    private final int numberLenght;

    public RandomInt() {
        this(4);
    }

    public RandomInt(int keyLength) {
        this.keyGenerator = KeyGenerators.secureRandom(keyLength);
        this.keyLenght = keyLength;
        this.numberLenght = (int) Math.pow(10, keyLenght);
    }

    public int getInt() {
        int result = ByteBuffer.wrap(generateKey()).getInt() % numberLenght;
        if (result < 0) {
            result *= -1;
        }
        return result;
    }

    public int getKeyLength() {
        return keyLenght;
    }

    public byte[] generateKey() {
        byte[] key = this.keyGenerator.generateKey();
        return key;
    }
}