package src;

import mcl.bn254.Ec1;
import mcl.bn254.Ec2;
import mcl.bn254.Fp12;
import mcl.bn254.Mpz;

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
        Ec1 p1=kgc.getG1();

        Ec2 ppubs=kgc.getPpubs();
        Fp12 g=new Fp12();
        g.pairing(ppubs,p1);

        BigInteger N=kgc.getN();
        BigInteger l,h;
        do {
            BigInteger r;
            do {
                r = new BigInteger(N.bitLength(), new SecureRandom());

            } while (r.compareTo(N) >= 0);

           Fp12 w=new Fp12(g);
           w.power(new Mpz(r.toString(10)));

            byte[] wb = Sm9Util.Fp12ToBytes(w);

            byte[] merge = Sm9Util.byteMerger(message, wb);
             h = Sm9Util.h2(merge, N);
             l = r.subtract(h).mod(N);
        }while(l.equals(BigInteger.ZERO));

        Ec1 s =new Ec1(privatekey.getDs());
        s.mul(new Mpz(l.toString(10)));
        return new Signature(h,s);
    }

    public boolean verifySignature(byte [] message,Signature signature) throws Exception{

        if(id==null)
        {
            throw new Exception("not initial for verify");
        }
        KeyGenerationCenter kgc =KeyGenerationCenter.getInstance();

        Ec1 p1=kgc.getG1();
        Ec2 p2=kgc.getG2();

        BigInteger N=kgc.getN();
        Ec2 ppubs=kgc.getPpubs();

        if(signature.h.compareTo(BigInteger.ONE)<0 || signature.h.compareTo(N)>=0) {
            return false;
        }
        if(!signature.s.isValid()){
            return false;
        }
        Fp12 g=new Fp12();
        g.pairing(ppubs,p1);
        Fp12 t=new Fp12(g);
        t.power(new Mpz(signature.h.toString(10)));

        byte [] hid =new byte[]{kgc.hid};
        byte [] merge=Sm9Util.byteMerger(id.getBytes(),hid);
        BigInteger h1=Sm9Util.h1(merge,N);
        Ec2 p=new Ec2(p2);
        p.mul(new Mpz(h1.toString(10)));
        p.add(ppubs);
        Fp12 u =new Fp12();
        u.pairing(p,signature.s);

        Fp12 w=new Fp12(u);
        w.mul(t);
        byte [] wb2=Sm9Util.Fp12ToBytes(w);
        byte[] merge2=Sm9Util.byteMerger(message,wb2);
        BigInteger h2=Sm9Util.h2(merge2,N);

        return h2.equals(signature.h);
    }



}
