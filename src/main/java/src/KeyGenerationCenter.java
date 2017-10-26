package src;


import src.api.Element;
import src.api.PairingParameters;
import src.api.PairingParametersGenerator;
import src.api.Point;
import src.field.curve.CurveElement;
import src.field.curve.CurveField;
import src.field.gt.GTFiniteField;
import src.pairing.f.TypeFCurveGenerator;
import src.pairing.f.TypeFPairing;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Created by mzy on 2017/4/17.
 */
public class KeyGenerationCenter {


    private static int size =256;

    private BigInteger ks; //master sign private key
    private BigInteger ke;//master encrypt private key

    public byte hid=0x01;
    public byte hid2=0x02;

//    private ECPoint g1,g2,ppubs,ppube;
//    private EllipticCurve curve1,curve2;
//    private final Pairing pairing2;
    private CurveElement g1,g2,ppubs,ppube;
    private CurveField curve1,curve2;
    private GTFiniteField gt;
    private TypeFPairing pairing;

    private SecureRandom random;
    private SecureRandom random2;

    private BigInteger N; // the order

    private static KeyGenerationCenter THIS;


    public KeyGenerationCenter(){


//        this.pairing2 = AtePairingOverBarretoNaehrigCurveFactory
//                .getPairing(PairingTypes.TYPE_2, size);
//
//        this.curve1 = pairing2.getGroup1();
//        this.curve2 = pairing2.getGroup2();
//
//
//        this.g1=curve1.getGenerator();
//        this.g2=curve2.getGenerator();
//        this.N=curve1.getOrder();
        BigInteger g1x=new BigInteger("93DE051D62BF718FF5ED0704487D01D6E1E4086909DC3280E8C4E4817C66DDDD",16);
        BigInteger g1y=new BigInteger("21FE8DDA4F21E607631065125C395BBC1C1C00CBFA6024350C464CD70A3EA616",16);

        BigInteger xp21=new BigInteger("3722755292130B08D2AAB97FD34EC120EE265948D19C17ABF9B7213BAF82D65B",16);
        BigInteger xp22=new BigInteger("85AEF3D078640C98597B6027B441A01FF1DD2C190F5E93C454806C11D8806141",16);
        BigInteger yp21=new BigInteger("A7CF28D519BE3DA65F3170153D278FF247EFBA98A71A08116215BBA5C999A7C7",16);
        BigInteger yp22=new BigInteger("17509B092E845C1266BA0D262CBEE6ED0736A96FA347C8BD856DC76B84EBEB96",16);

        int rBits=256;
        PairingParametersGenerator pairingParametersGenerator=new TypeFCurveGenerator(rBits);
        PairingParameters parameters=pairingParametersGenerator.generate();

        this.pairing=new TypeFPairing(parameters);

        this.curve1=(CurveField) pairing.getG1();
        this.curve2=(CurveField)pairing.getG2();
        this.gt=(GTFiniteField)pairing.getGT();
        g1=curve1.newElement();
        g1.getX().set(g1x);
        g1.getY().set(g1y);
        g1.setInfFlag(0);

        g2=curve2.newElement();
        Point g2x=(Point) g2.getX().getField().newElement();
        Point g2y=(Point) g2.getX().getField().newElement();

        g2x.getX().set(xp21);
        g2x.getY().set(xp22);
        g2y.getX().set(yp21);
        g2y.getY().set(yp22);
        g2.getX().set(g2x);
        g2.getY().set(g2y);
        g2.setInfFlag(0);
        this.N=pairing.getR();


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

        this.ppubs=g2.duplicate().mul(ks);
        this.ppube=g1.duplicate().mul(ke);
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

        //ds = t2 *g1

//        Ec1 ds =new Ec1(g1);
//        Mpz t2mpz=new Mpz(t2.toString(10));
//        ds.mul(t2mpz);
        CurveElement ds=g1.duplicate().mul(t2);

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

        //de = t2*g2
//        Ec2 de=new Ec2(g2);
//        de.mul(new Mpz(t2.toString(10)));

        CurveElement de=g2.duplicate().mul(t2);
        return new Sm9EncryptPrivateKey(de);
    }
    public Element pair(CurveElement p1,CurveElement p2){ return pairing.pairing(p1,p2);}
    public CurveElement getPpubs(){return this.ppubs ;}
    public CurveElement getPpube(){return this.ppube;}
    public CurveElement getG1 (){return this.g1;}
    public CurveElement getG2 () {return this.g2;}
    public BigInteger getN(){return this.N;}
    public CurveField getCurve1(){return this.curve1;}
    public CurveField getCurve2(){return this.curve2;}
}
