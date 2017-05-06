package src;

import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.jpbc.PairingParametersGenerator;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.plaf.jpbc.pairing.f.TypeFCurveGenerator;
import mcl.bn254.*;


import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Created by mzy on 2017/4/17.
 */
public class KeyGenerationCenter {

//    static {
//        System.loadLibrary("bn254_if_wrap");
//        BN254.SystemInit();
//    }
    private static int rBits =256;

    private BigInteger ks; //master sign private key
    private BigInteger ke;//master encrypt private key
    //public Ec2 ppubs; //master sign public key
    public Ec1 ppube; //master encrypt public key

    public byte hid=0x01;
    public byte hid2=0x02;

//    public Ec1 g1;
//    public Ec2 g2;
    private Field g1;
    private Field g2;

    private SecureRandom random;
    private SecureRandom random2;

    private BigInteger N; // the order

    private static KeyGenerationCenter THIS;

    public void init()
    {

    }
    public KeyGenerationCenter(){


//        Fp aa = new Fp("12723517038133731887338407189719511622662176727675373276651903807414909099441");
//        Fp ab = new Fp("4168783608814932154536427934509895782246573715297911553964171371032945126671");
//        Fp ba = new Fp("13891744915211034074451795021214165905772212241412891944830863846330766296736");
//        Fp bb = new Fp("7937318970632701341203597196594272556916396164729705624521405069090520231616");
//        g1 = new Ec1(new Fp(-1), new Fp(1));
//        g2 = new Ec2(new Fp2(aa, ab), new Fp2(ba, bb));

        PairingParametersGenerator pairingParametersGenerator=new TypeFCurveGenerator(rBits);
        PairingParameters parameters=pairingParametersGenerator.generate();
        Pairing pairing = PairingFactory.getPairing(parameters);
        g1=pairing.getG1();
        g2=pairing.getG2();

//        Mpz r=BN254.GetParamR();
//        this.N=new BigInteger(r.toString(),10);

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

//        Ec2 ec2ppubs=new Ec2(g2);
//        Mpz ksmpz=new Mpz(ks.toString(10));
//        ec2ppubs.mul(ksmpz);
//        this.ppubs=new Ec2(ec2ppubs);
        
        Ec1 ec1ppube=new Ec1(g1);
        ec1ppube.mul(new Mpz(ke.toString(10)));
        this.ppube=ec1ppube;

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

        Ec1 ds =new Ec1(g1);
        Mpz t2mpz=new Mpz(t2.toString(10));
        ds.mul(t2mpz);

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
        Ec2 de=new Ec2(g2);
        de.mul(new Mpz(t2.toString(10)));
        return new Sm9EncryptPrivateKey(de);
    }
    public Ec2 getPpubs(){return ppubs ;}
    public Ec1 getPpube(){return ppube;}
    public Ec1 getG1 (){return g1;}
    public Ec2 getG2 () {return g2;}
    public BigInteger getN(){return N;}
}
