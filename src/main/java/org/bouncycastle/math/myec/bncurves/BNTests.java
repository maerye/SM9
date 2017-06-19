/**
 * BNTests.java
 *
 * Simple tests for Barreto-Naehrig (BN) pairing-friendly elliptic curves.
 *
 * Copyright (C) Paulo S. L. M. Barreto.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.bouncycastle.math.myec.bncurves;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class BNTests {

    protected static final BigInteger
        _0 = BigInteger.valueOf(0L),
        _1 = BigInteger.valueOf(1L),
        _2 = BigInteger.valueOf(2L),
        _3 = BigInteger.valueOf(3L),
        _4 = BigInteger.valueOf(4L),
        _5 = BigInteger.valueOf(5L),
        _6 = BigInteger.valueOf(6L);

    /**
     * Generic prototypes used in the BNPoint and BNPoint2 tests.
     */
    BNPoint prototype;
    BNPoint2 prototype2;

    /**
     * Create an instance of BNTests by providing prototypes
     * for BNPoint and GF variables.
     *
     * This is a direct application of the "Prototype" design pattern
     * as described by E. Gamma, R. Helm, R. Johnson and J. Vlissides in
     * "Design Patterns - Elements of Reusable Object-Oriented Software",
     * Addison-Wesley (1995), pp. 117-126.
     *
     * @param   prototype   the prototype for BNPoint instantiation
     */
    public BNTests(BNPoint prototype, BNPoint2 prototype2) {
        this.prototype = prototype;
        this.prototype2 = prototype2;
    }

    /**
     * Perform a complete test suite on the BNCurve implementation
     *
     * @param   iterations  the desired number of iterations of the test suite
     */
    public void doTest(int iterations, SecureRandom rand, boolean verbose) {
        BNPoint w, x, y, z, ecZero;
        BigInteger m, n;
        int numBits = 192; // caveat: maybe using larger values is better
        long totalElapsed = -System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            if (verbose) {
                System.out.print("test #" + i);
            }
            long elapsed = -System.currentTimeMillis();
            // create random values from the prototype:
            x = prototype.randomize(rand);
            y = prototype.randomize(rand);
            z = prototype.randomize(rand);
            ecZero = prototype.E.infinity;
            m = new BigInteger(numBits, rand);
            n = new BigInteger(numBits, rand);

            // check cloning/comparison/pertinence:
            if (iterations == 1) {
                System.out.print("\nchecking cloning/comparison/pertinence");
            }
            if (!x.equals(x)) {
                throw new RuntimeException("Comparison failure");
            }
            if (verbose) {
                System.out.print(".");
            }
            if (!x.isOnSameCurve(x)) {
                throw new RuntimeException("Inconsistent pertinence self-comparison");
            }
            if (verbose) {
                System.out.print(".");
            }
            if (!x.E.contains(x)) {
                throw new RuntimeException("Inconsistent curve pertinence");
            }
            if (verbose) {
                System.out.print(".");
            }

            // check addition properties:
            if (iterations == 1) {
                System.out.print(" done.\nchecking addition properties");
            }
            if (!x.add(y).equals(y.add(x))) {
                throw new RuntimeException("x + y != y + x");
            }
            if (verbose) {
                System.out.print(".");
            }
            if (!x.add(ecZero).equals(x)) {
                throw new RuntimeException("x + 0 != x");
            }
            if (verbose) {
                System.out.print(".");
            }
            if (!x.add(x.negate()).isZero()) {
                throw new RuntimeException("x + (-x) != 0");
            }
            if (verbose) {
                System.out.print(".");
            }
            if (!x.add(y).add(z).equals(x.add(y.add(z)))) {
                throw new RuntimeException("(x + y) + z != x + (y + z)");
            }
            if (verbose) {
                System.out.print(".");
            }
            if (!x.negate().negate().equals(x)) {
                throw new RuntimeException("-(-x) != x");
            }

            // check scalar multiplication properties:
            if (iterations == 1) {
                System.out.print(" done.\nchecking scalar multiplication properties");
            }
            if (!x.multiply(BigInteger.valueOf(0L)).equals(ecZero)) {
                throw new RuntimeException("0*x != 0");
            }
            if (verbose) {
                System.out.print(".");
            }
            if (!x.multiply(BigInteger.valueOf(1L)).equals(x)) {
                throw new RuntimeException("1*x != x");
            }
            if (verbose) {
                System.out.print(".");
            }
            if (!x.multiply(BigInteger.valueOf(2L)).equals(x.twice(1))) {
                throw new RuntimeException("2*x != twice x");
            }
            if (verbose) {
                System.out.print(".");
            }
            if (!x.multiply(BigInteger.valueOf(2L)).equals(x.add(x))) {
                throw new RuntimeException("2*x != x + x");
            }
            if (verbose) {
                System.out.print(".");
            }
            if (!x.multiply(BigInteger.valueOf(-1L)).equals(x.negate())) {
                throw new RuntimeException("(-1)*x != -x");
            }
            if (verbose) {
                System.out.print(".");
            }
            if (!x.multiply(m.negate()).equals(x.negate().multiply(m))) {
                throw new RuntimeException("(-m)*x != m*(-x)");
            }
            if (verbose) {
                System.out.print(".");
            }
            if (!x.multiply(m.negate()).equals(x.multiply(m).negate())) {
                throw new RuntimeException("(-m)*x != -(m*x)");
            }
            if (verbose) {
                System.out.print(".");
            }
            if (!x.multiply(m.add(n)).equals(x.multiply(m).add(x.multiply(n)))) {
                throw new RuntimeException("(m + n)*x != m*x + n*x");
            }
            if (verbose) {
                System.out.print(".");
            }
            w = x.multiply(n).multiply(m);
            if (!w.equals(x.multiply(m).multiply(n))) {
                throw new RuntimeException("m*(n*x) != n*(m*x)");
            }
            if (verbose) {
                System.out.print(".");
            }
            if (!w.equals(x.multiply(m.multiply(n)))) {
                throw new RuntimeException("m*(n*x) != (m*n)*x");
            }
            // TODO: test point compression/expansion/conversion
            elapsed += System.currentTimeMillis();
            if (verbose) {
                System.out.println(" done; elapsed =  " + (float)elapsed/1000 + " s.");
            }
        }
        totalElapsed += System.currentTimeMillis();
        //if (verbose) {
            System.out.println(" OK; all " + iterations + " tests done in " + (float)totalElapsed/1000 + " s.");
        //}
    }

    /**
     * Perform a complete test suite on the BNCurve2 implementation
     *
     * @param   iterations  the desired number of iterations of the test suite
     */
    public void doTest2(int iterations, SecureRandom rand, boolean verbose) {
        BNPoint2 w, x, y, z, ecZero;
        BigInteger m, n;
        int numBits = 192; // caveat: maybe using larger values is better
        long totalElapsed = -System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            if (verbose) {
                System.out.print("test #" + i);
            }
            long elapsed = -System.currentTimeMillis();
            // create random values from the prototype:
            x = prototype2.randomize(rand);
            y = prototype2.randomize(rand);
            z = prototype2.randomize(rand);
            ecZero = prototype2.E.infinity;
            m = new BigInteger(numBits, rand);
            n = new BigInteger(numBits, rand);

            // check cloning/comparison/pertinence:
            if (iterations == 1) {
                System.out.print("\nchecking cloning/comparison/pertinence");
            }
            if (!x.equals(x)) {
                throw new RuntimeException("Comparison failure");
            }
            if (verbose) {
                System.out.print(".");
            }
            if (!x.isOnSameCurve(x)) {
                throw new RuntimeException("Inconsistent pertinence self-comparison");
            }
            if (verbose) {
                System.out.print(".");
            }
            if (!x.E.contains(x)) {
                throw new RuntimeException("Inconsistent curve pertinence");
            }
            if (verbose) {
                System.out.print(".");
            }

            // check addition properties:
            if (iterations == 1) {
                System.out.print(" done.\nchecking addition properties");
            }
            if (!x.add(y).equals(y.add(x))) {
                throw new RuntimeException("x + y != y + x");
            }
            if (verbose) {
                System.out.print(".");
            }
            if (!x.add(ecZero).equals(x)) {
                throw new RuntimeException("x + 0 != x");
            }
            if (verbose) {
                System.out.print(".");
            }
            if (!x.add(x.negate()).isZero()) {
                throw new RuntimeException("x + (-x) != 0");
            }
            if (verbose) {
                System.out.print(".");
            }
            if (!x.add(y).add(z).equals(x.add(y.add(z)))) {
                throw new RuntimeException("(x + y) + z != x + (y + z)");
            }
            if (verbose) {
                System.out.print(".");
            }
            if (!x.negate().negate().equals(x)) {
                throw new RuntimeException("-(-x) != x");
            }
/*
            // check scalar multiplication properties:
            if (iterations == 1) {
                System.out.print(" done.\nchecking scalar multiplication properties");
            }
            if (!x.multiply(BigInteger.valueOf(0L)).equals(ecZero)) {
                throw new RuntimeException("0*x != 0");
            }
            if (verbose) {
                System.out.print(".");
            }
            if (!x.multiply(BigInteger.valueOf(1L)).equals(x)) {
                throw new RuntimeException("1*x != x");
            }
            if (verbose) {
                System.out.print(".");
            }
            if (!x.multiply(BigInteger.valueOf(2L)).equals(x.twice(1))) {
                throw new RuntimeException("2*x != twice x");
            }
            if (verbose) {
                System.out.print(".");
            }
            if (!x.multiply(BigInteger.valueOf(2L)).equals(x.add(x))) {
                throw new RuntimeException("2*x != x + x");
            }
            if (verbose) {
                System.out.print(".");
            }
            if (!x.multiply(BigInteger.valueOf(-1L)).equals(x.negate())) {
                throw new RuntimeException("(-1)*x != -x");
            }
            if (verbose) {
                System.out.print(".");
            }
            if (!x.multiply(m.negate()).equals(x.negate().multiply(m))) {
                throw new RuntimeException("(-m)*x != m*(-x)");
            }
            if (verbose) {
                System.out.print(".");
            }
            if (!x.multiply(m.negate()).equals(x.multiply(m).negate())) {
                throw new RuntimeException("(-m)*x != -(m*x)");
            }
            if (verbose) {
                System.out.print(".");
            }
            if (!x.multiply(m.add(n)).equals(x.multiply(m).add(x.multiply(n)))) {
                throw new RuntimeException("(m + n)*x != m*x + n*x");
            }
            if (verbose) {
                System.out.print(".");
            }
            w = x.multiply(n).multiply(m);
            if (!w.equals(x.multiply(m).multiply(n))) {
                throw new RuntimeException("m*(n*x) != n*(m*x)");
            }
            if (verbose) {
                System.out.print(".");
            }
            if (!w.equals(x.multiply(m.multiply(n)))) {
                throw new RuntimeException("m*(n*x) != (m*n)*x");
            }
            // TODO: test point compression/expansion/conversion
            elapsed += System.currentTimeMillis();
            if (verbose) {
                System.out.println(" done; elapsed =  " + (float)elapsed/1000 + " s.");
            }*/
        }
        totalElapsed += System.currentTimeMillis();
        //if (verbose) {
            System.out.println(" OK; all " + iterations + " tests done in " + (float)totalElapsed/1000 + " s.");
        //}
    }

    public static void benchmarks(int BM) {
        byte[] randSeed = new byte[20];
        (new Random()).nextBytes(randSeed);
        //*
        for (int i = 0; i < randSeed.length; i++) {
            randSeed[i] = (byte)i;
        }
        //*/
        SecureRandom rnd = new SecureRandom(randSeed);
        long elapsed;
        for (int i = 256; i <= 256; i += 8) {
            System.out.println("\n======== bits: " + i);
            BNParams bn = new BNParams(i);
            BNCurve E = new BNCurve(bn); System.out.println(E);
            BNCurve2 E2 = new BNCurve2(E); System.out.println(E2);
            BNTests T = new BNTests(E.G, E2.Gt);
            T.doTest(10, rnd, true);
            T.doTest2(10, rnd, true);
            System.out.println("-----------------");
            BNPairing pair = new BNPairing(E2);
            System.out.println(pair);
            BNField12 f;
            BNPoint P = E.G;
            BNPoint2 Q = E2.Gt;
            BigInteger k = new BigInteger(i, rnd);

        }
    }



    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
    	benchmarks(100);
    	//CLSEG(256, 10);
    }

}
