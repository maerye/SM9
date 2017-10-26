package src;

import iaik.security.ec.math.curve.ECPoint;
import iaik.security.ec.math.field.GenericFieldElement;
import mcl.bn254.*;
import org.bouncycastle.asn1.*;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import src.api.Element;
import src.field.curve.CurveElement;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Created by mzy on 2017/4/26.
 */
public class Sm9Engine {
    static {
        System.loadLibrary("bn254_if_wrap");
        BN254.SystemInit();
    }

    private String id;
    private int k1len,k2len;
    private boolean forEncryption;
    private int type;// 0,1
    private Cipher sm4cipher;
    private Sm9EncryptPrivateKey privatekey;
    public Sm9Engine (Cipher cipher){
        this.sm4cipher=cipher;
    }

    public void initEncrypt(boolean forencryption ,String id,int k1,int k2,int type)throws Exception{
        this.forEncryption=forencryption;
        this.id=id;
        this.k1len=k1;
        this.k2len=k2;
        this.type=type;

    }
    public void initDecrypt (boolean forEncryption,String id,Sm9EncryptPrivateKey key,int k1,int k2,int type){
        this.forEncryption=forEncryption;
        this.id=id;
        this.privatekey=key;
        this.k1len=k1;
        this.k2len=k2;
        this.type=type;

    }

    public byte [] processBlock(byte [] in,int off,int len) throws Exception{

        KeyGenerationCenter kgc=KeyGenerationCenter.getInstance();
        byte [] block;
        if(off!=0||len!=in.length) {
            block=new byte[len];
            System.arraycopy(in,off,block,0,len);
        }
        else {
            block=in;
        }
        if(forEncryption) {
            return processEncrypt(block,kgc);

        }
        else{
            return processDecrypt(block);
        }

    }

    private byte [] processEncrypt(byte []block,KeyGenerationCenter kgc) throws  Exception{
        byte hid = kgc.hid2;
        BigInteger N = kgc.getN();

        CurveElement g1,g2,ppube,qb,c1;
        byte [] k1,k2,c2,c1b,wb1;

        g1=kgc.getG1();
        g2=kgc.getG2();
        ppube=kgc.getPpube();

        byte [] idb=this.id.getBytes();
        byte[] merge=new byte[idb.length+1];
        System.arraycopy(idb,0,merge,0,idb.length);
        merge[idb.length]=hid;
        BigInteger h1=Sm9Util.h1(merge,N);

        qb=g1.duplicate().mul(h1).add(ppube);

        do {
            BigInteger r;
            do {
                r=new BigInteger(N.bitLength(),new SecureRandom());
            }while(r.compareTo(N)>=0||r.compareTo(BigInteger.ONE)<0);

            c1=qb.duplicate().mul(r);
            c1b=c1.toBytes();
            Element g=kgc.pair(ppube,g2);
            Element w=g.pow(r);
            wb1=Sm9Util.GTFiniteElementToByte(w);

            if(type==0){
                int klen =block.length*8+k2len*8;
                byte [] merge1= Sm9Util.byteMerger(c1b,wb1);
                byte [] merge2= Sm9Util.byteMerger(merge1,id.getBytes());
                byte [] k= Sm9Util.KDF(merge2,klen);
                k1=new byte[block.length];
                k2=new byte [k2len];

                System.arraycopy(k,0,k1,0,block.length);
                System.arraycopy(k,block.length,k2,0,k2len);
                c2=xor(block,k1);
            }else {
                int klen =k1len*8+k2len*8;
                byte [] merge1= Sm9Util.byteMerger(c1b,wb1);
                byte [] merge2= Sm9Util.byteMerger(merge1,id.getBytes());
                byte [] k= Sm9Util.KDF(merge2,klen);
                k1=new byte[k1len];
                k2=new byte[k2len];
                System.arraycopy(k,0,k1,0,k1len);
                System.arraycopy(k,k1len,k2,0,k2len);
                Key key=new SecretKeySpec(k1,"SM4");
                sm4cipher.init(Cipher.ENCRYPT_MODE,key);
                int s=sm4cipher.getOutputSize(block.length);
                int insize=sm4cipher.getBlockSize();
                c2=new byte[s];
                c2=sm4cipher.doFinal(block,0,block.length);
            }

        }while(testZeros(k1));

        byte[] c3=Sm9Util.MAC(k2,c2);

//        byte []c1x=c1.scalePoint().getCoordinate().getX().toByteArray();
//        byte []c1y=c1.scalePoint().getCoordinate().getY().toByteArray();
        byte []c1x=c1.getX().toBytes();
        byte [] c1y=c1.getY().toBytes();
        ASN1Sequence seq=null;
        ASN1EncodableVector v=new ASN1EncodableVector();
        v.add(new ASN1Integer(c1x));
        v.add(new ASN1Integer(c1y));
        v.add(new DEROctetString(c3));
        v.add(new DEROctetString(c2));
        //v.add(new DEROctetString(c1t));
        seq=new DERSequence(v);
        byte[] res = null;
        try {
            res = seq.getEncoded(ASN1Encoding.DER);
        } catch (IOException e) {
            throw new IllegalArgumentException("encoding error in getInstance: " + e.toString());
        }
        return res;

    }
    private byte [] processDecrypt(byte []block)throws Exception{

        KeyGenerationCenter kgc=KeyGenerationCenter.getInstance();

        ASN1Sequence asn1Obj = ASN1Sequence.getInstance(block);
        ASN1Integer c1x_encoded =ASN1Integer.getInstance(asn1Obj.getObjectAt(0)) ;
        ASN1Integer c1y_encoded =ASN1Integer.getInstance(asn1Obj.getObjectAt(1)) ;

        DEROctetString c2_encoded = (DEROctetString) asn1Obj.getObjectAt(3);
        DEROctetString c3_encoded = (DEROctetString) asn1Obj.getObjectAt(2);
       // DEROctetString c1t=(DEROctetString)asn1Obj.getObjectAt(4);

        BigInteger x=c1x_encoded.getPositiveValue();
        BigInteger y=c1y_encoded.getPositiveValue();
        byte[] c2 = c2_encoded.getOctets();
        byte[] c3 = c3_encoded.getOctets();

//
//        java.security.spec.ECPoint point =new java.security.spec.ECPoint(x,y);
//        ECPoint c1p;
        CurveElement c1p;
        try {
           // c1p=kgc.getCurve1().newPoint(point);
            c1p=kgc.getCurve1().newElement();
            c1p.getX().set(x);
            c1p.getY().set(y);
            c1p.setInfFlag(0);

        }catch (Exception e)
        {
            throw new Exception("c1 is invalid");
        }

//        Fp12 ws =new Fp12();
//        ws.pairing(privatekey.getDe(),c1p);
//        byte [] wb=Sm9Util.Fp12ToBytes(ws);
        Element w=kgc.pair(c1p,privatekey.getDe());
        byte [] wb=Sm9Util.GTFiniteElementToByte(w);
        byte []c1b2=c1p.toBytes();

        byte [] k2,m;
        if(type==0){
            int klen=c2.length*8+k2len*8;
            byte [] merge1=Sm9Util.byteMerger(c1b2,wb);
            byte [] merge2=Sm9Util.byteMerger(merge1,id.getBytes());
            byte [] k=Sm9Util.KDF(merge2,klen);
            byte [] k1=new byte[c2.length];

            k2=new byte[k2len];
            System.arraycopy(k,0,k1,0,c2.length);
            System.arraycopy(k,c2.length,k2,0,k2len);
            if(testZeros(k1))
            {
                throw new Exception("k1 is zero");
            }
            m=xor(c2,k1);

        }
        else{
            int klen =k1len*8+k2len*8;
            byte [] merge1=Sm9Util.byteMerger(c1b2,wb);
            byte [] merge2=Sm9Util.byteMerger(merge1,id.getBytes());
            byte [] k=Sm9Util.KDF(merge2,klen);
            byte [] k1=new byte [k1len];
            k2=new byte[k2len];
            System.arraycopy(k,0,k1,0,k1len);
            System.arraycopy(k,k1len,k2,0,k2len);
            Key key =new SecretKeySpec(k1,"SM4");
            sm4cipher.init(Cipher.DECRYPT_MODE,key);
            m=new byte [c2.length];
            m=sm4cipher.doFinal(c2,0,c2.length);
        }
        byte [] u=Sm9Util.MAC(k2,c2);
        if(!Arrays.equals(u,c3))
        {
            throw new Exception("mac not right");
        }
        return m;
    }
    private boolean testZeros(byte[] in) {
        for (byte b : in) {
            if (b != 0)
                return false;
        }
        return true;
    }

    private byte[] xor(byte[] op1, byte[] op2) {
        if (op1.length != op2.length) {
            throw new DataLengthException("op1's length is different with op2 in XOR operation");
        }
        byte[] out = new byte[op1.length];
        for (int i = 0; i < out.length; i++) {
            out[i] = (byte) ((op1[i] ^ op2[i]) & 0xff);
        }
        return out;
    }
}
