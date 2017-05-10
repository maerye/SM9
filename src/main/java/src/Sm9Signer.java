package src;

import mcl.bn254.Ec1;
import mcl.bn254.Ec2;
import mcl.bn254.Fp12;
import mcl.bn254.Mpz;
import org.bouncycastle.math.myec.bncurves.*;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Created by mzy on 2017/4/7.
 */
public class Sm9Signer {

    private Sm9SignPrivateKey privatekey;
    private String id;

    public Sm9Signer(){

    }
    public void initSign(Sm9SignPrivateKey privatekey)
    {
        this.privatekey=privatekey;

    }
    public void initVerify(String id){
        this.id=id;
    }

    public Signature generateSignature(byte [] message) throws Exception{

        if(privatekey==null)
        {
            throw new Exception("not initial for sign");
        }
        KeyGenerationCenter kgc=KeyGenerationCenter.getInstance();
        BNPoint p1=kgc.getG1();


        BNPoint2 ppubs=kgc.getPpubs();

        BNPairing pairing=new BNPairing(kgc.getCurve2());
        BNField12 g=pairing.eta(p1,ppubs);


        BigInteger N=kgc.getN();
        BigInteger l,h;
        do {
            BigInteger r;
            do {
                r = new BigInteger(N.bitLength(), new SecureRandom());

            } while (r.compareTo(N) >= 0);

            BNField12 w = g.exp(r);

            byte[] wb = Sm9Util.bnField12ToBytes(w);

            byte[] merge = Sm9Util.byteMerger(message, wb);
             h = Sm9Util.h2(merge, N);
             l = r.subtract(h).mod(N);
        }while(l.equals(BigInteger.ZERO));

        BNPoint s=privatekey.getDs().multiply(l);

        return new Signature(h,s);
    }

    public boolean verifySignature(byte [] message,Signature signature) throws Exception{

        if(id==null)
        {
            throw new Exception("not initial for verify");
        }
        KeyGenerationCenter kgc =KeyGenerationCenter.getInstance();
        BNCurve curve1=kgc.getCurve1();
        BNCurve2 curve2=kgc.getCurve2();
        BNPoint p1=kgc.getG1();
        BNPoint2 p2=kgc.getG2();

        BigInteger N=kgc.getN();
        BNPoint2 ppubs=kgc.getPpubs();

        if(signature.h.compareTo(BigInteger.ONE)<0 || signature.h.compareTo(N)>=0) {
            return false;
        }
        if(!curve1.contains(signature.s)){
            return false;
        }
        BNPairing pairing =new BNPairing(curve2);
        BNField12 g=pairing.eta(p1,ppubs);

        BNField12 t=g.exp(signature.h);

        byte [] hid =new byte[]{kgc.hid};
        byte [] merge=Sm9Util.byteMerger(id.getBytes(),hid);
        BigInteger h1=Sm9Util.h1(merge,N);
        BNPoint2 p=p2.multiply0(h1).add(ppubs);

        BNField12 u=pairing.eta(signature.s,p);
        BNField12 w2=u.multiply(t);
        byte[] wb2=Sm9Util.bnField12ToBytes(w2);

//        Ec2 p=new Ec2(p2);
//        p.mul(new Mpz(h1.toString(10)));
//        p.add(ppubs);
//        Fp12 u =new Fp12();
//        u.pairing(p,signature.s);
//        Fp12 w=new Fp12(u);
//        w.mul(t);
        //byte [] wb2=Sm9Util.Fp12ToBytes(w);
        byte[] merge2=Sm9Util.byteMerger(message,wb2);
        BigInteger h2=Sm9Util.h2(merge2,N);

        return h2.equals(signature.h);
    }



}
