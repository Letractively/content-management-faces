package net.tralfamadore.admin.util;


import javax.enterprise.inject.Produces;
import java.security.SecureRandom;
import java.util.Random;

/**
 * User: billreh
 * Date: 10/11/11
 * Time: 4:06 AM
 */
public class RandomProvider {
    private static final Random rand = new Random();
    private static final Random secureRand = new SecureRandom();

    @Produces
    public Random getRandom() {
        return rand;
    }

    @Produces
    @Secure
    public Random getSecureRandom() {
        return secureRand;
    }
}
