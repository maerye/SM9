package src;

import mcl.bn254.Ec1;
import mcl.bn254.Ec2;
import mcl.bn254.Fp12;
import mcl.bn254.Mpz;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Created by mzy on 2017/4/24.
 */
public class KEM {

    public EncapsulatedKey encapsulate(byte [] id,long klen){
        KeyGenerationCenter kgc =KeyGenerationCenter.getInstance();
        BigInteger N=kgc.getN();
        Ec1 g1=kgc.getG1();
        Ec2 g2=kgc.getG2();
        Ec1 ppube=kgc.getPpube();

        byte []merge=new byte[id.length+1];
        System.arraycopy(id,0,merge,0,id.length);
        merge[id.length]=kgc.hid2;

        BigInteger h1=Sm9Util.h1(merge,N);
        Ec1 qb=new Ec1(g1);
        qb.mul(new Mpz(h1.toString(10)));
        qb.add(ppube);
        byte [] k;
        Ec1 c;
        do{
            BigInteger r;
            do {
                r = new BigInteger(N.bitLength(), new SecureRandom());

            } while (r.compareTo(N) >= 0||r.compareTo(BigInteger.ONE)<0);

            c=new Ec1(qb);
            c.mul(new Mpz(r.toString(10)));
            byte[]cb=Sm9Util.ec1ToBytes(c);
            Fp12 g=new Fp12();
            g.pairing(g2,ppube);
            Fp12 w=new Fp12(g);
            w.power(new Mpz(r.toString(10)));
            byte [] wb=Sm9Util.Fp12ToBytes(w);
            byte [] merge1=Sm9Util.byteMerger(cb,wb);
            byte [] merge2=Sm9Util.byteMerger(merge1,id);
            k=Sm9Util.KDF(merge2,klen);
        }while (testZeros(k));

        return new EncapsulatedKey(k,c);

    }
    public byte[] decapsulate(Ec1 c,byte [] id,Sm9EncryptPrivateKey de,long klen) throws Exception{
        if(!c.isValid())
        {
            throw new Exception("invalid content");
        }
        Fp12 w=new Fp12();
        w.pairing(de.getDe(),c);
        byte [] wb=Sm9Util.Fp12ToBytes(w);
        byte [] cb=Sm9Util.ec1ToBytes(c);
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
