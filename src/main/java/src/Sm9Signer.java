package src;

import com.sun.xml.internal.bind.v2.model.core.EnumLeafInfo;
import iaik.security.ec.math.curve.ECPoint;
import iaik.security.ec.math.curve.EllipticCurve;
import iaik.security.ec.math.field.GenericFieldElement;
import mcl.bn254.Ec1;
import mcl.bn254.Ec2;
import mcl.bn254.Fp12;
import mcl.bn254.Mpz;
import src.api.Element;
import src.api.Polynomial;
import src.field.curve.CurveElement;
import src.field.gt.GTFiniteElement;

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
//        Ec1 p1=kgc.getG1();
//
//        Ec2 ppubs=kgc.getPpubs();
//        Fp12 g=new Fp12();
//        g.pairing(ppubs,p1);
        CurveElement p1=kgc.getG1();
        CurveElement ppubs=kgc.getPpubs();
        Element g=kgc.pair(p1,ppubs);

        BigInteger N=kgc.getN();
        BigInteger l,h;
        do {
            BigInteger r;
            do {
                r = new BigInteger(N.bitLength(), new SecureRandom());

            } while (r.compareTo(N) >= 0);

            Element w=g.duplicate().pow(r);

            //byte[] wb = Sm9Util.Fp12ToBytes(w);
            byte [] wb=Sm9Util.GTFiniteElementToByte(w);
            byte[] merge = Sm9Util.byteMerger(message, wb);
             h = Sm9Util.h2(merge, N);
             l = r.subtract(h).mod(N);
        }while(l.equals(BigInteger.ZERO));

        CurveElement ds =privatekey.getDs();
        CurveElement s=ds.duplicate().mul(l);

        return new Signature(h,s);
    }

    public boolean verifySignature(byte [] message,Signature signature) throws Exception{

        if(id==null)
        {
            throw new Exception("not initial for verify");
        }
        KeyGenerationCenter kgc =KeyGenerationCenter.getInstance();
        BigInteger N=kgc.getN();

        CurveElement p1,p2,ppubs;

        p1=kgc.getG1();
        p2=kgc.getG2();
        ppubs=kgc.getPpubs();

        if(signature.h.compareTo(BigInteger.ONE)<0 || signature.h.compareTo(N)>=0) {
            return false;
        }
        if(!signature.s.isValid()){
            return false;
        }
//        Fp12 g=new Fp12();
//        g.pairing(ppubs,p1);
//        Fp12 t=new Fp12(g);
//        t.power(new Mpz(signature.h.toString(10)));
        Element g=kgc.pair(p1,ppubs);
        Element t=g.pow(signature.h);

        byte [] hid =new byte[]{kgc.hid};
        byte [] merge=Sm9Util.byteMerger(id.getBytes(),hid);
        BigInteger h1=Sm9Util.h1(merge,N);


        CurveElement p=p2.duplicate().mul(h1).add(ppubs);
        Element u=kgc.pair(signature.s,p);
        Element w=u.mul(t);


        byte[] wb2=Sm9Util.GTFiniteElementToByte(w);

        byte[] merge2=Sm9Util.byteMerger(message,wb2);
        BigInteger h2=Sm9Util.h2(merge2,N);

        return h2.equals(signature.h);
    }



}
