package org.example.app.util.random;

import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.keygen.KeyGenerators;

import java.nio.ByteBuffer;

public class Random4NumberGen implements BytesKeyGenerator {
    private static final int DEFAULT_KEY_LENGTH = 4;
    private final BytesKeyGenerator keyGenerator;
    private final int keyLenght;
    private final int numberLenght;

    public Random4NumberGen() {
        this(4);
    }

    public Random4NumberGen(int keyLength) {
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