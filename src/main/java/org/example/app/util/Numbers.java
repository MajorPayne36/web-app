package org.example.app.util;

import java.util.Random;

public class Numbers {
    private Numbers(){ }

    public static String generateCardNumber(){
        StringBuilder result = new StringBuilder();
        final var random = new Random();
        for (int i = 0; i <4 ; i++){
            result.append(random.ints(1, 10));
        }
        return result.toString();
    }
}
