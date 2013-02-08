/*
 * $Id$
 *
 * Copyright 2013 Valentyn Kolesnikov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.calc;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

/**
 * BigDecimal utilities.
 *
 * @author Valentyn Kolesnikov
 * @version $Revision$ $Date$
 */
public final class BigDecimalUtil {
    private static final int SCALE = 18;
    public static long ITER = 1000;
    public static MathContext context = new MathContext( 100 );

    private BigDecimalUtil() {
    }

    /**
     * Compute the square root of x to a given scale, x >= 0.
     * Use Newton's algorithm.
     * @param x the value of x
     * @return the result value
     */
    public static BigDecimal sqrt(BigDecimal x) {
        // Check that x >= 0.
        if (x.signum() < 0) {
            throw new ArithmeticException("x < 0");
        }

        // n = x*(10^(2*SCALE))
        BigInteger n = x.movePointRight(SCALE << 1).toBigInteger();

        // The first approximation is the upper half of n.
        int bits = (n.bitLength() + 1) >> 1;
        BigInteger ix = n.shiftRight(bits);
        BigInteger ixPrev;

        // Loop until the approximations converge
        // (two successive approximations are equal after rounding).
        do {
            ixPrev = ix;

            // x = (x + n/x)/2
            ix = ix.add(n.divide(ix)).shiftRight(1);

            Thread.yield();
        } while (ix.compareTo(ixPrev) != 0);

        return new BigDecimal(ix, SCALE);
    }

    public static BigDecimal ln(BigDecimal x) {
        if (x.equals(BigDecimal.ONE)) {
            return BigDecimal.ZERO;
        }

        x = x.subtract(BigDecimal.ONE);
        BigDecimal ret = new BigDecimal(ITER + 1);
        for (long i = ITER; i >= 0; i--) {
        BigDecimal N = new BigDecimal(i / 2 + 1).pow(2);
            N = N.multiply(x, context);
            ret = N.divide(ret, context);

            N = new BigDecimal(i + 1);
            ret = ret.add(N, context);

        }

        ret = x.divide(ret, context);
        return ret;
    }    
}
