package src;

import iaik.security.ec.math.curve.ECPoint;
import iaik.security.ec.math.curve.EllipticCurve;
import iaik.security.ec.math.field.GenericFieldElement;
import mcl.bn254.*;
import src.api.Element;
import src.field.curve.CurveElement;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Created by mzy on 2017/4/24.
 */
public class KEM {

    public EncapsulatedKey encapsulate(byte [] id,long klen){
        KeyGenerationCenter kgc =KeyGenerationCenter.getInstance();
        BigInteger N=kgc.getN();
        CurveElement g1,g2,ppube;
//        Ec1 g1=kgc.getG1();
//        Ec2 g2=kgc.getG2();
//        Ec1 ppube=kgc.getPpube();
        g1=kgc.getG1();
        g2=kgc.getG2();
        ppube=kgc.getPpube();

        byte []merge=new byte[id.length+1];
        System.arraycopy(id,0,merge,0,id.length);
        merge[id.length]=kgc.hid2;

        BigInteger h1=Sm9Util.h1(merge,N);

//        Ec1 qb=new Ec1(g1);
//        qb.mul(new Mpz(h1.toString(10)));
//        qb.add(ppube);
        CurveElement qb=g1.duplicate().mul(h1).add(ppube);

        byte [] k;
        CurveElement c;
        do{
            BigInteger r;
            do {
                r = new BigInteger(N.bitLength(), new SecureRandom());

            } while (r.compareTo(N) >= 0||r.compareTo(BigInteger.ONE)<0);

            c=qb.mul(r);

          //  byte [] cb=Sm9Util.ECpoint1Tobytes(c);
            byte [] cb=c.toBytes();

//            Fp12 g=new Fp12();
//            g.pairing(g2,ppube);
//            Fp12 w=new Fp12(g);
//            w.power(new Mpz(r.toString(10)));
//            byte [] wb=Sm9Util.Fp12ToBytes(w);
            Element g=kgc.pair(ppube,g2);
            Element w=g.pow(r);
            byte [] wb=Sm9Util.GTFiniteElementToByte(w);

            byte [] merge1=Sm9Util.byteMerger(cb,wb);
            byte [] merge2=Sm9Util.byteMerger(merge1,id);
            k=Sm9Util.KDF(merge2,klen);
        }while (testZeros(k));


        return new EncapsulatedKey(k,c);

    }
    public byte[] decapsulate(CurveElement c,byte [] id,Sm9EncryptPrivateKey de,long klen) throws Exception{
        KeyGenerationCenter kgc=KeyGenerationCenter.getInstance();
        //EllipticCurve curve1=kgc.getCurve1();
       // if(!curve1.containsPoint(c.toJDKECPoint()))
        if(!c.isValid())
        {
            throw new Exception("invalid content");
        }
//        Ec1 ec1=new Ec1(c);
//        Fp12 w=new Fp12();
//        w.pairing(de.getDe(),ec1);
//        byte [] wb=Sm9Util.Fp12ToBytes(w);
//        byte [] cb=Sm9Util.ec1ToBytes(ec1);
        Element w=kgc.pair(c,de.getDe());
        byte [] wb=Sm9Util.GTFiniteElementToByte(w);
       // byte [] cb=Sm9Util.ECpoint1Tobytes(c);
        byte [] cb=c.toBytes();
        byte [] merge1=Sm9Util.byteMerger(cb,wb);
        byte [] merge2=Sm9Util.byteMerger(merge1,id);
        byte [] k=Sm9Util.KDF(merge2,klen);
        if(testZeros(k))
        {
            throw new Exception("k is zeroo");
        }
        return k;
    }

    private boolean testZeros(byte[] in) {
        for (byte b : in) {
            if (b != 0)
                return false;
        }
        return true;
    }
}
