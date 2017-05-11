package src;

import org.bouncycastle.math.myec.bncurves.*;
import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Created by mzy on 2017/4/17.
 */
public class KeyGenerationCenter {


    private static int fieldbits=256;
    private BNParams bnParams;
    private BNCurve curve1;
    private BNCurve2 curve2;
    private BNPoint ppube,g1;
    private BNPoint2 ppubs,g2;

    private BigInteger ks; //master sign private key
    private BigInteger ke;//master encrypt private key


    public byte hid=0x01;
    public byte hid2=0x02;


    private SecureRandom random;
    private SecureRandom random2;

    private BigInteger N; // the order
    private static KeyGenerationCenter THIS;

    public void init()
    {

    }
    public KeyGenerationCenter(){

        this.bnParams=new BNParams(fieldbits);
        this.curve1=new BNCurve(bnParams);
        this.curve2=new BNCurve2(curve1);
        this.g1=curve1.getG();
        this.g2=curve2.getGt();
        this.N=curve1.getN();

        this.random=new SecureRandom();
        this.random2=new SecureRandom();

        //generate ks the sign master private key [1,N-1]
        BigInteger temp,temp2;
        while(true) {
            temp=new BigInteger(N.bitLength(),random);
            if(temp.compareTo(BigInteger.ZERO)==1 && temp.compareTo(N)==-1)
                break;
        }

        while(true) {
            temp2=new BigInteger(N.bitLength(),random2);
            if(temp2.compareTo(BigInteger.ZERO)==1 && temp.compareTo(N)==-1)
                break;
        }

        this.ks = temp;
        this.ke =temp2;


        this.ppubs=g2.multiply0(ks);

        this.ppube=g1.multiply0(ke);
    }

    public static KeyGenerationCenter getInstance()
    {
        if(THIS==null){
            THIS= new KeyGenerationCenter();
        }

        return  THIS;
    }

    public Sm9SignPrivateKey generateSignPrivatekey(String id) throws Exception {

        byte [] idbytes = id.getBytes();
        int length=idbytes.length;
        byte [] temp = new byte [length+1];
        System.arraycopy(idbytes,0,temp,0,length);
        temp[length]=hid;

        BigInteger t1=Sm9Util.h1(temp,N).add(ks);
        t1=t1.mod(N);
        if(t1.equals(BigInteger.ZERO))
        {
            throw new Exception("need to update the master sign private key ");
        }
        BigInteger t2 = ks.multiply(t1.modInverse(N)).mod(N);
        BNPoint ds=g1.multiply(t2);

        return new Sm9SignPrivateKey(ds);
    }
    public Sm9EncryptPrivateKey generateEncrypyPrivateKey(String id) throws Exception {
        byte [] idb=id.getBytes();
        int length=idb.length;
        byte [] merge=new byte[length+1];
        System.arraycopy(idb,0,merge,0,length);
        merge[length]=hid2;

        BigInteger t1=Sm9Util.h1(merge,N).add(ke);
        t1=t1.mod(N);

        if(t1.equals(BigInteger.ZERO))
        {
            throw new Exception("need to update the master encrypt private key");
        }
        BigInteger t2=ke.multiply(t1.modInverse(N)).mod(N);//???
        BNPoint2 de=g2.multiply0(t2);
        return new Sm9EncryptPrivateKey(de);
    }
    public BNPoint2 getPpubs(){return ppubs ;}
    public BNPoint getPpube(){return ppube;}
    public BNPoint getG1 (){return g1;}
    public BNPoint2 getG2 () {return g2;}
    public BigInteger getN(){return N;}
    public BNCurve2 getCurve2(){return curve2;}
    public BNCurve getCurve1(){return curve1;}
}
