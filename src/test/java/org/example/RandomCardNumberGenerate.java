package org.example;

import org.example.random.RandomInt;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;

public class RandomCardNumberGenerate {

    private Random random;

    public static void main(String[] args) {

        int num = 257;
        System.out.println("Input            : " + num);
        System.out.println("result            : " + new RandomInt(4).getInt());
    }
}