package src;

import org.bouncycastle.math.myec.bncurves.*;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Created by mzy on 2017/4/24.
 */
public class KEM {

    public EncapsulatedKey encapsulate(byte [] id,long klen){
        KeyGenerationCenter kgc =KeyGenerationCenter.getInstance();
        BigInteger N=kgc.getN();
        BNPoint g1=kgc.getG1();
        BNPoint2 g2=kgc.getG2();
        BNPoint ppube=kgc.getPpube();
        BNCurve2 curve2=kgc.getCurve2();

        byte []merge=new byte[id.length+1];
        System.arraycopy(id,0,merge,0,id.length);
        merge[id.length]=kgc.hid2;

        BigInteger h1=Sm9Util.h1(merge,N);
        BNPoint qb=g1.multiply(h1).add(ppube);

        byte [] k;
        BNPoint c;
        do{
            BigInteger r;
            do {
                r = new BigInteger(N.bitLength(), new SecureRandom());

            } while (r.compareTo(N) >= 0||r.compareTo(BigInteger.ONE)<0);

            c=qb.multiply(r);
            byte[]cb=Sm9Util.bnpointToBytes(c);
            BNPairing pair=new BNPairing(curve2);
            BNField12 g=pair.eta(ppube,g2);
            BNField12 w=g.exp(r);

            byte [] wb=Sm9Util.bnField12ToBytes(w);
            byte [] merge1=Sm9Util.byteMerger(cb,wb);
            byte [] merge2=Sm9Util.byteMerger(merge1,id);
            k=Sm9Util.KDF(merge2,klen);
        }while (testZeros(k));


        return new EncapsulatedKey(k,c);

    }
    public byte[] decapsulate(BNPoint c,byte [] id,Sm9EncryptPrivateKey de,long klen) throws Exception{
        KeyGenerationCenter kgc=KeyGenerationCenter.getInstance();
        BNCurve curve1=kgc.getCurve1();
        BNCurve2 curve2=kgc.getCurve2();

        if(!curve1.contains(c))
        {
            throw new Exception("invalid content");
        }
        BNPairing pairing=new BNPairing(curve2);
        BNField12 w=pairing.eta(c,de.getDe());

        byte [] wb=Sm9Util.bnField12ToBytes(w);
        byte [] cb=Sm9Util.bnpointToBytes(c);
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
