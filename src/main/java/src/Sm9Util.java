package src;

import iaik.security.ec.math.curve.ECPoint;
import iaik.security.ec.math.field.GenericFieldElement;
import mcl.bn254.Ec1;
import mcl.bn254.Fp;
import mcl.bn254.Fp12;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SM3Digest;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.regex.Pattern;

/**
 * Created by mzy on 2017/4/17.
 */
public class Sm9Util {

    private static Digest digest=new SM3Digest();
    private static final double LOG2 = Math.log(2.0);
    public static int BIGINTEGER_LENGTH =32;

    public static BigInteger h1(byte [] z, BigInteger n){
        double log2n=Math.log(n.doubleValue())/LOG2;
        double hlen=8*Math.ceil((5*log2n)/32);
        int digestSize=digest.getDigestSize();
        int v=digestSize*8;
        int counts=(int)Math.ceil(hlen/v);
        byte [] Ha=new byte[counts*digestSize];
        for(int ct=1;ct<counts;ct++)
        {
            digest.reset();
            digest.update((byte)0x01);
            digest.update(z,0,z.length);
            digest.update((byte)(ct>>24 & 0xff));
            digest.update((byte)(ct>>16 & 0xff));
            digest.update((byte)(ct>>8 & 0xff));
            digest.update((byte)(ct & 0xff));
            digest.doFinal(Ha,(ct-1)*digestSize);
        }
        byte [] temp=new byte[digestSize];
        digest.reset();
        digest.update((byte)0x01);
        digest.update(z,0,z.length);
        digest.update((byte)(counts>>24 & 0xff));
        digest.update((byte)(counts>>16 & 0xff));
        digest.update((byte)(counts>>8 & 0xff));
        digest.update((byte)(counts & 0xff));
        digest.doFinal(temp,0);

        BigInteger Hanum1;
        if(hlen%v>0) {
            int nbits=(int)(hlen-(v*Math.floor(hlen/v)));
            int right=digestSize*8-nbits;
            System.arraycopy(temp,0,Ha,(counts-1)*digestSize,digestSize);
            Hanum1=new BigInteger(1,Ha);
            Hanum1=Hanum1.shiftRight(right);

        }
        else{
            System.arraycopy(temp,0,Ha,(counts-1)*digestSize,digestSize);
            Hanum1=new BigInteger(1,Ha);
        }
        return Hanum1.mod(n.subtract(BigInteger.ONE)).add(BigInteger.ONE);
    }

    public static BigInteger h2(byte [] z,BigInteger n){
        double log2n=Math.log(n.doubleValue())/LOG2;
        double hlen=8*Math.ceil((5*log2n)/32);
        int digestSize=digest.getDigestSize();
        int v=digestSize*8;
        int counts=(int)Math.ceil(hlen/v);
        byte [] Ha=new byte[counts*digestSize];
        for(int ct=1;ct<counts;ct++)
        {
            digest.reset();
            digest.update((byte)0x02);
            digest.update(z,0,z.length);
            digest.update((byte)(ct>>24 & 0xff));
            digest.update((byte)(ct>>16 & 0xff));
            digest.update((byte)(ct>>8 & 0xff));
            digest.update((byte)(ct & 0xff));
            digest.doFinal(Ha,(ct-1)*digestSize);
        }
        byte [] temp=new byte[digestSize];
        digest.reset();
        digest.update((byte)0x02);
        digest.update(z,0,z.length);
        digest.update((byte)(counts>>24 & 0xff));
        digest.update((byte)(counts>>16 & 0xff));
        digest.update((byte)(counts>>8 & 0xff));
        digest.update((byte)(counts & 0xff));
        digest.doFinal(temp,0);

        BigInteger Hanum2;
        if(hlen%v>0) {
            int nbits=(int)(hlen-(v*Math.floor(hlen/v)));
            int right=digestSize*8-nbits;
            System.arraycopy(temp,0,Ha,(counts-1)*digestSize,digestSize);
            Hanum2=new BigInteger(1,Ha);
            Hanum2=Hanum2.shiftRight(right);

        }
        else{
            System.arraycopy(temp,0,Ha,(counts-1)*digestSize,digestSize);
            Hanum2=new BigInteger(1,Ha);
        }
        return Hanum2.mod(n.subtract(BigInteger.ONE)).add(BigInteger.ONE);

    }

 /*   public static byte[] bnField12ToBytes(BNField12  bnField12) {
        BNField2 [] field2s=bnField12.v;
        ByteBuffer buffer=ByteBuffer.allocate(BIGINTEGER_LENGTH*12);
        for(int i=field2s.length-1;i>=0;i--){
            BigInteger a1=field2s[i].im;
            BigInteger a0=field2s[i].re;
            byte[] a1b=bigIntegerTobytes(a1);
            byte[] a0b=bigIntegerTobytes(a0);
            buffer.put(a1b);
            buffer.put(a0b);
        }
        return buffer.array();

    }*/

    public static byte[] GtElementToBytes(GenericFieldElement gt){
        byte [] result=new byte[BIGINTEGER_LENGTH*12];
        byte [] source=gt.toByteArray();
        for(int i=11;i>=0;i--)
        {
            System.arraycopy(source,i*32,result,(11-i)*32,32);
        }
        return result;
    }
    public static  byte [] ECpoint1Tobytes(ECPoint p)
    {
        byte [] x=p.scalePoint().getCoordinate().getX().toByteArray();
        byte [] y=p.scalePoint().getCoordinate().getY().toByteArray();

        return byteMerger(x,y);
    }
    public static byte[] Fp12ToBytes(Fp12 fp12) {
        String [] x=fp12.toString().split(",");
        String charTdel="[]\n ";
        String pat="["+ Pattern.quote(charTdel)+"]";
        String temp;
        BigInteger []tempBig=new BigInteger[x.length];
        for(int i=0;i<x.length;i++)
        {
            temp=x[i].replaceAll(pat,"");
            tempBig[i]=new BigInteger(temp,10);
        }
        ByteBuffer buffer=ByteBuffer.allocate(BIGINTEGER_LENGTH*12);
        for(int i=tempBig.length-1;i>=0;i--)
        {
              buffer.put(bigIntegerTobytes(tempBig[i]));
        }

        return buffer.array();
    }
    public static byte [] ec1ToBytes (Ec1 p){
        Fp x=p.getX();
        Fp y=p.getY();
        BigInteger xb=new BigInteger(x.toString(),10);
        BigInteger yb=new BigInteger(y.toString(),10);
        byte [] a=bigIntegerTobytes(xb);
        byte [] b=bigIntegerTobytes(yb);
        byte [] merge=byteMerger(a,b);
        return merge;
    }
    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2){
        byte[] byte_3 = new byte[byte_1.length+byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }

    public static byte [] bigIntegerTobytes (BigInteger in){
            byte tmpd[] = (byte[]) null;
            if (in == null) {
                return null;
            }

            if (in.toByteArray().length == 33) {
                tmpd = new byte[32];
                System.arraycopy(in.toByteArray(), 1, tmpd, 0, 32);
            } else if (in.toByteArray().length == 32) {
                tmpd = in.toByteArray();
            } else {
                tmpd = new byte[32];
                for (int i = 0; i < 32 - in.toByteArray().length; i++) {
                    tmpd[i] = 0;
                }

                System.arraycopy(in.toByteArray(), 0, tmpd, 32 - in.toByteArray().length, in.toByteArray().length);
            }
            return tmpd;

    }
    public static byte [] KDF(byte [] z,long klen){
        int digestSize =digest.getDigestSize();
        int v=digestSize*8;
        assert (klen<4294967295L*v);
        int reminder=(int)(klen%v) ;
        int counts=(int)(klen/v)+(reminder==0? 0:1);
        byte [] ha =new byte[counts*digestSize];

        for(int ct=1;ct<=counts;ct++){
            digest.reset();
            digest.update(z,0,z.length);
            digest.update((byte)(ct>>24 & 0xff));
            digest.update((byte)(ct>>16 & 0xff));
            digest.update((byte)(ct>>8 & 0xff));
            digest.update((byte)(ct& 0xff));
            digest.doFinal(ha,(ct-1)*digestSize);
        }


        if(klen%v>0){
            int lbits=(int) (klen-(v*Math.floor(klen/v)));
            int shiftbit=digestSize*8-lbits;
            BigInteger hanum=new BigInteger(1,ha);
            hanum=hanum.shiftRight(shiftbit);
            return hanum.toByteArray();
        }
        else{
            return ha;
        }

    }

    public static byte [] MAC(byte [] k2,byte[] z){
        byte [] k=new byte[digest.getDigestSize()];
        digest.reset();
        digest.update(z,0,z.length);
        digest.update(k2,0,k2.length);
        digest.doFinal(k,0);
        return k;
    }


}
