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
     * @param   random      the source of randomness for the various tests
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
     * @param   random      the source of randomness for the various tests
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
            /*
            System.out.println("(t-1)*Q   = " + Q.multiply(bn.t.subtract(_1)).normalize());
            System.out.println("p*Q       = " + Q.multiply(bn.p).normalize());
            System.out.println("frobex(Q) = " + Q.frobex(1));
            System.exit(0);
            //*/
            BigInteger k = new BigInteger(i, rnd);
            /*
            g = pair.eta(E.G, E2.Gt);
            System.out.println("g = " + g);
            System.out.println("g^n = " + g.exp(bn.n));
            a = pair.eta(E.G.twice(1), E2.Gt);
            b = pair.eta(E.G, E2.Gt.twice(1));
            c = g.square();
            System.out.println("eq? " + (a.equals(b) && b.equals(c)));
            for (int j = 0; j < 10; j++) {
                BigInteger m = new BigInteger(i, rnd);
                a = pair.eta(E.G.multiply(m), E2.Gt);
                b = pair.eta(E.G, E2.Gt.multiply(m));
                c = g.exp(m);
                System.out.println("eq? " + (a.equals(b) && b.equals(c)));
                if (!(a.equals(b) && b.equals(c)) || a.isOne()) {
                    throw new RuntimeException("LOGIC ERROR!");
                }
            }
            //*/
            /*
            g = pair.ate(E2.Gt, E.G);
            System.out.println("g = " + g);
            System.out.println("g^n = " + g.exp(bn.n));
            a = pair.ate(E2.Gt.twice(1), E.G);
            b = pair.ate(E2.Gt, E.G.twice(1));
            c = g.square();
            System.out.println("eq? " + (a.equals(b) && b.equals(c)));
            if (!(a.equals(b) && b.equals(c)) || a.isOne()) {
                throw new RuntimeException("LOGIC ERROR!");
            }
            for (int j = 0; j < 10; j++) {
                BigInteger m = new BigInteger(i, rnd);
                a = pair.ate(E2.Gt.multiply(m), E.G);
                b = pair.ate(E2.Gt, E.G.multiply(m));
                c = g.exp(m);
                System.out.println("eq? " + (a.equals(b) && b.equals(c)));
                if (!(a.equals(b) && b.equals(c)) || a.isOne()) {
                    throw new RuntimeException("LOGIC ERROR!");
                }
            }
            //*/
            /*
            g = pair.opt(E2.Gt, E.G);
            System.out.println("g = " + g);
            System.out.println("g^n = " + g.exp(bn.n));
            a = pair.opt(E2.Gt.twice(1), E.G);
            b = pair.opt(E2.Gt, E.G.twice(1));
            c = g.square();
            System.out.println("eq? " + (a.equals(b) && b.equals(c)));
            if (!(a.equals(b) && b.equals(c)) || a.isOne()) {
                throw new RuntimeException("LOGIC ERROR!");
            }
            //*
            for (int j = 0; j < 10; j++) {
                BigInteger m = new BigInteger(i, rnd);
                a = pair.opt(E2.Gt.multiply(m), E.G);
                b = pair.opt(E2.Gt, E.G.multiply(m));
                c = g.exp(m);
                System.out.println("eq? " + (a.equals(b) && b.equals(c)));
                if (!(a.equals(b) && b.equals(c)) || a.isOne()) {
                    throw new RuntimeException("LOGIC ERROR!");
                }
            }
            //*/
            /*
            System.out.println("Benchmarking BNPoint:");
            elapsed = -System.currentTimeMillis();
            for (int t = 0; t < BM; t++) {
                P = P.multiply(k);
            }
            elapsed += System.currentTimeMillis();
            System.out.println("Elapsed time: " + (float)elapsed/BM + " ms.");
            if (P.isZero()) {
                throw new RuntimeException("LOGIC ERROR!");
            }
            //*/
            /*
            System.out.println("Benchmarking BNPoint simultexp2:");
            BigInteger k1 = new BigInteger(i, rnd);
            BNPoint P1 = P.randomize(rnd);
            elapsed = -System.currentTimeMillis();
            for (int t = 0; t < BM; t++) {
                P = P.simultaneous(k, k1, P1);
            }
            elapsed += System.currentTimeMillis();
            System.out.println("Elapsed time: " + (float)elapsed/BM + " ms.");
            if (P.isZero()) {
                throw new RuntimeException("LOGIC ERROR!");
            }
            System.out.println("Benchmarking BNPoint simultexp4:");
            BigInteger k2 = new BigInteger(i, rnd);
            BNPoint P2 = P.randomize(rnd);
            BigInteger k3 = new BigInteger(i, rnd);
            BNPoint P3 = P.randomize(rnd);
            elapsed = -System.currentTimeMillis();
            for (int t = 0; t < BM; t++) {
                P = P.simultaneous(k, P, k1, P1, k2, P2, k3, P3);
            }
            elapsed += System.currentTimeMillis();
            System.out.println("Elapsed time: " + (float)elapsed/BM + " ms.");
            if (P.isZero()) {
                throw new RuntimeException("LOGIC ERROR!");
            }
            //*/
            /*
            System.out.println("Benchmarking BNPoint2:");
            elapsed = -System.currentTimeMillis();
            for (int t = 0; t < BM; t++) {
                //BNPoint2 Q2 = Q.multiply(k);
                BNPoint2 Q2 = Q.exp(k.negate());
                //if (!Q2.equals(Q.multiply(k.negate()))) { throw new RuntimeException("Oops!"); }
                Q = Q2;
            }
            elapsed += System.currentTimeMillis();
            System.out.println("Elapsed time: " + (float)elapsed/BM + " ms.");
            if (Q.isZero()) {
                throw new RuntimeException("LOGIC ERROR!");
            }
            //*/
            /*
            f = pair.opt(Q, P);
            System.out.println("f      = " + f);
            System.out.println("f^k    = " + f.exp(k));
            System.out.println("f#k    = " + f.exp(k));
            System.exit(0);
            //*/
            /*
            System.out.println("Benchmarking BNField12 exponentiation:");
            f = pair.opt(Q, P);
            elapsed = -System.currentTimeMillis();
            for (int t = 0; t < BM; t++) {
                f = f.exp(k);
            }
            elapsed += System.currentTimeMillis();
            System.out.println("Elapsed time: " + (float)elapsed/BM + " ms.");
            //*/
            /*
            System.out.println("Benchmarking BNField12 GLV:");
            f = pair.opt(Q, P);
            elapsed = -System.currentTimeMillis();
            for (int t = 0; t < BM; t++) {
                f = f.exp(k);
            }
            elapsed += System.currentTimeMillis();
            System.out.println("Elapsed time: " + (float)elapsed/BM + " ms.");
            //*/
            /*
            System.out.println("Benchmarking Eta Pairing:");
            f = pair.Fp12_0;
            elapsed = -System.currentTimeMillis();
            for (int t = 0; t < BM; t++) {
                f = pair.eta(P, Q);
            }
            elapsed += System.currentTimeMillis();
            System.out.println("Elapsed time: " + (float)elapsed/BM + " ms.");
            System.out.println("f = " + f);
            //*/
            /*
            System.out.println("Benchmarking Ate Pairing:");
            f = pair.Fp12_0;
            elapsed = -System.currentTimeMillis();
            for (int t = 0; t < BM; t++) {
                f = pair.ate(Q, P);
            }
            elapsed += System.currentTimeMillis();
            System.out.println("Elapsed time: " + (float)elapsed/BM + " ms.");
            System.out.println("f = " + f);
            //*/
            /*
            System.out.println("Benchmarking Optimal Pairing:");
            f = pair.Fp12_0;
            elapsed = -System.currentTimeMillis();
            for (int t = 0; t < BM; t++) {
                f = pair.opt(Q, P);
            }
            elapsed += System.currentTimeMillis();
            System.out.println("Elapsed time: " + (float)elapsed/BM + " ms.");
            System.out.println("f = " + f);
            //*/
            /*
	        System.out.println("Benchmarking private RSA-" + 12*bn.p.bitLength());
	        BigInteger p = BigInteger.probablePrime(6*bn.p.bitLength(), rnd);
	        BigInteger q = BigInteger.probablePrime(6*bn.p.bitLength(), rnd);
	        BigInteger u = q.modInverse(p);
	        BigInteger n = p.multiply(q);
	        BigInteger phi = p.subtract(_1).multiply(q.subtract(_1));
	        BigInteger e = BigInteger.valueOf(65537L);
	        BigInteger d = e.modInverse(phi);
	        BigInteger m = new BigInteger(12*bn.p.bitLength(), rnd).mod(n);
	        elapsed = -System.currentTimeMillis();
	        for (int t = 0; t < BM; t++) {
	            BigInteger mp = m.modPow(d, p);
	            BigInteger mq = m.modPow(d, q);
	            m = mp.subtract(mq).multiply(u).mod(p).multiply(q).add(mq);
	        }
	        elapsed += System.currentTimeMillis();
	        System.out.println("Elapsed time: " + (float)elapsed/BM + " ms.");
            //*/

            //*
            System.out.println("Benchmarking Barbosa-Farshim key validation:");
            f = pair.opt(Q, P);
            elapsed = -System.currentTimeMillis();
            for (int t = 0; t < BM; t++) {
                f = pair.opt(Q, P);
            }
            elapsed += System.currentTimeMillis();
            System.out.println("Elapsed time: " + (float)elapsed/BM + " ms.");

            System.out.println("Benchmarking BLMQ preprocessing:");
            elapsed = -System.currentTimeMillis();
            for (int t = 0; t < BM; t++) {
                P = P.multiply(k);
            }
            elapsed += System.currentTimeMillis();
            System.out.println("Elapsed time: " + (float)elapsed/BM + " ms.");

            System.out.println("Benchmarking LXH preprocessing:");
            elapsed = -System.currentTimeMillis();
            for (int t = 0; t < BM; t++) {
                f = pair.opt(Q, P);
                f = pair.opt(Q, P);
            }
            elapsed += System.currentTimeMillis();
            System.out.println("Elapsed time: " + (float)elapsed/BM + " ms.");

            System.out.println("Benchmarking CLPKE preprocessing:");
            elapsed = -System.currentTimeMillis();
            for (int t = 0; t < BM; t++) {
                f = f.exp(k);
            }
            elapsed += System.currentTimeMillis();
            System.out.println("Elapsed time: " + (float)elapsed/BM + " ms.");

            System.out.println("Benchmarking BDCPS key validation:");
            elapsed = -System.currentTimeMillis();
            for (int t = 0; t < BM; t++) {
                f = pair.opt(Q, P);
                f = f.exp(k); f = f.exp(k);
                P = P.multiply(k);
            }
            elapsed += System.currentTimeMillis();
            System.out.println("Elapsed time: " + (float)elapsed/BM + " ms.");

            System.out.println("Benchmarking Barbosa-Farshim signcryption:");
            elapsed = -System.currentTimeMillis();
            for (int t = 0; t < BM; t++) {
                f = f.exp(k);
                P = P.multiply(k); P = P.multiply(k); Q = Q.simultaneous(k, k, Q);
            }
            elapsed += System.currentTimeMillis();
            System.out.println("Elapsed time: " + (float)elapsed/BM + " ms.");

            System.out.println("Benchmarking BLMQ signcryption:");
            elapsed = -System.currentTimeMillis();
            for (int t = 0; t < BM; t++) {
                f = f.exp(k);
                P = P.multiply(k); Q = Q.multiply(k);
            }
            elapsed += System.currentTimeMillis();
            System.out.println("Elapsed time: " + (float)elapsed/BM + " ms.");

            System.out.println("Benchmarking LXH signcryption:");
            elapsed = -System.currentTimeMillis();
            for (int t = 0; t < BM; t++) {
                f = f.exp(k);
                f = f.exp(k);
                P = P.multiply(k);
                P = P.multiply(k);
                P = P.multiply(k);
            }
            elapsed += System.currentTimeMillis();
            System.out.println("Elapsed time: " + (float)elapsed/BM + " ms.");

            System.out.println("Benchmarking CLPKE signcryption:");
            elapsed = -System.currentTimeMillis();
            for (int t = 0; t < BM; t++) {
                f = f.exp(k); f = f.exp(k); f = f.exp(k);
            }
            elapsed += System.currentTimeMillis();
            System.out.println("Elapsed time: " + (float)elapsed/BM + " ms.");

            System.out.println("Benchmarking BDCPS signcryption:");
            elapsed = -System.currentTimeMillis();
            for (int t = 0; t < BM; t++) {
                f = f.exp(k);
            }
            elapsed += System.currentTimeMillis();
            System.out.println("Elapsed time: " + (float)elapsed/BM + " ms.");

			/*
            System.out.println("Benchmarking BDCPS* signcryption:");
            elapsed = -System.currentTimeMillis();
            for (int t = 0; t < BM; t++) {
            	BigInteger qq = k.modInverse(sms.n); // pure Zheng
                f = f.exp(k);
            }
            elapsed += System.currentTimeMillis();
            System.out.println("Elapsed time: " + (float)elapsed/BM + " ms.");
            //*/

            System.out.println("Benchmarking Barbosa-Farshim unsigncryption:");
            elapsed = -System.currentTimeMillis();
            for (int t = 0; t < BM; t++) {
                f = pair.opt(Q, P); f = pair.opt(Q, P); f = pair.opt(Q, P); f = pair.opt(Q, P);
                P = P.multiply(k);
            }
            elapsed += System.currentTimeMillis();
            System.out.println("Elapsed time: " + (float)elapsed/BM + " ms.");

            System.out.println("Benchmarking BLMQ unsigncryption:");
            elapsed = -System.currentTimeMillis();
            for (int t = 0; t < BM; t++) {
                f = pair.opt(Q, P); f = pair.opt(Q, P);
                f = f.exp(k);
            }
            elapsed += System.currentTimeMillis();
            System.out.println("Elapsed time: " + (float)elapsed/BM + " ms.");

            System.out.println("Benchmarking LXH unsigncryption:");
            elapsed = -System.currentTimeMillis();
            for (int t = 0; t < BM; t++) {
                f = pair.opt(Q, P); f = pair.opt(Q, P);
                f = pair.opt(Q, P); f = pair.opt(Q, P);
                f = f.exp(k);
                f = f.exp(k);
            }
            elapsed += System.currentTimeMillis();
            System.out.println("Elapsed time: " + (float)elapsed/BM + " ms.");

            System.out.println("Benchmarking CLPKE unsigncryption:");
            elapsed = -System.currentTimeMillis();
            for (int t = 0; t < BM; t++) {
                f = f.exp(k); f = f.exp(k); f = f.exp(k);
            }
            elapsed += System.currentTimeMillis();
            System.out.println("Elapsed time: " + (float)elapsed/BM + " ms.");

            System.out.println("Benchmarking BDCPS unsigncryption:");
            elapsed = -System.currentTimeMillis();
            BigInteger kk = k.multiply(k).mod(bn.n);
            for (int t = 0; t < BM; t++) {
                f = f.fastSimultaneous(k, kk, f);
            }
            elapsed += System.currentTimeMillis();
            System.out.println("Elapsed time: " + (float)elapsed/BM + " ms.");
            //*/
        }
    }

	public static void CLSEG(int i, int BM) {
        byte[] randSeed = new byte[20];
        (new Random()).nextBytes(randSeed);
        SecureRandom rnd = new SecureRandom(randSeed);
        long elapsed;
        System.out.println("\n======== bits: " + i);
        BNParams sms = new BNParams(i);
        BNCurve E = new BNCurve(sms); //System.out.println(E);
        BNCurve2 E2 = new BNCurve2(E); //System.out.println(E2);
        BNPoint P = E.G;
        BNPoint2 Q = E2.Gt;
        BigInteger s = new BigInteger(i, rnd).mod(sms.n); // master key
        BNPoint Ppub = P.multiply(s);
        BigInteger h1ID_A = new BigInteger(i, rnd).mod(sms.n); // h_1(ID_A)
        //BigInteger h1ID_B = new BigInteger(i, rnd).mod(sms.n); // h_1(ID_B)
        BNPoint2 Q_A = Q.multiply(h1ID_A.add(s).modInverse(sms.n));
        //BNPoint2 Q_B = Q.multiply(h1ID_B.add(s).modInverse(sms.n));
        BigInteger x_A = new BigInteger(i, rnd).mod(sms.n);
        BigInteger x_B = new BigInteger(i, rnd).mod(sms.n);
        BNPairing pair = new BNPairing(E2);
        //Object[] pairingTable;
        BNField12 g = pair.opt(Q, P);
        BNPoint V_A = P.multiply(x_A);
        BNPoint V_B = P.multiply(x_B);
        BigInteger h_A = new BigInteger(i, rnd).mod(sms.n); // h_0(r_A, ID_A, y_A)
        //BigInteger h_B = new BigInteger(i, rnd).mod(sms.n); // h_0(r_B, ID_B, y_B)
        BNPoint S_A = Ppub;
        //BNPoint S_B = Ppub;
        BNPoint2 T_A = Q_A;//.multiply(u_A.subtract(h_A).multiply(x_A.modInverse(sms.n)).mod(sms.n));
        //BNPoint2 T_B = Q_B;//.multiply(u_B.subtract(h_B).multiply(x_B.modInverse(sms.n)).mod(sms.n));
    	BigInteger h = new BigInteger(i, rnd).mod(sms.n);
    	BigInteger z = new BigInteger(i, rnd).mod(sms.n);

        System.out.println("Benchmarking optimal Pairing:");
        elapsed = -System.currentTimeMillis();
        for (int t = 0; t < BM; t++) {
            pair.opt(Q, P);
        }
        elapsed += System.currentTimeMillis();
        System.out.println("Elapsed time: " + (float)elapsed/BM + " ms.");

		////////////////////////////////////////////////////////////////////
        System.out.println("Benchmarking CLSEG Private-Key-Extract:");
        elapsed = -System.currentTimeMillis();
        for (int t = 0; t < BM; t++) {
            Q_A = Q.multiply(h1ID_A.add(s).modInverse(sms.n));
        }
        elapsed += System.currentTimeMillis();
        System.out.println("Elapsed time: " + (float)elapsed/BM + " ms.");

		////////////////////////////////////////////////////////////////////
        System.out.println("Benchmarking CLSEG Check-Private-Key:");
        elapsed = -System.currentTimeMillis();
        for (int t = 0; t < BM; t++) {
            pair.opt(Q_A, P.multiply(h1ID_A).add(Ppub));
            //boolean ok = f.equals(g);
        }
        elapsed += System.currentTimeMillis();
        System.out.println("Elapsed time: " + (float)elapsed/BM + " ms.");

		////////////////////////////////////////////////////////////////////
        System.out.println("Benchmarking CLSEG Set-Public-Value:");
        elapsed = -System.currentTimeMillis();
        for (int t = 0; t < BM; t++) {
            V_A = P.multiply(x_A);
        }
        elapsed += System.currentTimeMillis();
        System.out.println("Elapsed time: " + (float)elapsed/BM + " ms.");

		////////////////////////////////////////////////////////////////////
        System.out.println("Benchmarking CLSEG Set-Public-Key:");
        elapsed = -System.currentTimeMillis();
        for (int t = 0; t < BM; t++) {
        	BigInteger u_A = new BigInteger(i, rnd).mod(sms.n);
            //BNField12 r_A = g.exp(u_A);
            S_A = Ppub.multiply(x_A);
            T_A = Q_A.multiply(u_A.subtract(h_A).multiply(x_A.modInverse(sms.n)).mod(sms.n)); 
        }
        elapsed += System.currentTimeMillis();
        System.out.println("Elapsed time: " + (float)elapsed/BM + " ms.");

		////////////////////////////////////////////////////////////////////
        System.out.println("Benchmarking CLSEG Public-Key-Validate:");
        elapsed = -System.currentTimeMillis();
        for (int t = 0; t < BM; t++) {
            pair.opt(T_A, V_A.multiply(h1ID_A).add(S_A)).multiply(g.exp(h_A));
        }
        elapsed += System.currentTimeMillis();
        System.out.println("Elapsed time: " + (float)elapsed/BM + " ms.");

		////////////////////////////////////////////////////////////////////
        System.out.println("Benchmarking CLSEG Signcrypt:");
        elapsed = -System.currentTimeMillis();
        for (int t = 0; t < BM; t++) {
        	BigInteger u = new BigInteger(i, rnd).mod(sms.n);
            V_B.multiply(u);
            z = u.subtract(x_A.multiply(h)).mod(sms.n);
        }
        elapsed += System.currentTimeMillis();
        System.out.println("Elapsed time: " + (float)elapsed/BM + " ms.");

		////////////////////////////////////////////////////////////////////
        System.out.println("Benchmarking CLSEG Unsigncrypt:");
        elapsed = -System.currentTimeMillis();
        for (int t = 0; t < BM; t++) {
            V_A.simultaneous(h.multiply(x_B).mod(sms.n), z, V_B);
        }
        elapsed += System.currentTimeMillis();
        System.out.println("Elapsed time: " + (float)elapsed/BM + " ms.");
	}

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
    	benchmarks(100);
    	//CLSEG(256, 10);
    }

}
