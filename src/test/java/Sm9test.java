
import org.bouncycastle.jcajce.provider.asymmetric.ec.KeyFactorySpi;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.math.myec.bncurves.*;
import org.junit.Before;
import org.junit.Test;
import src.*;

import javax.crypto.Cipher;
import java.math.BigInteger;
import java.security.Key;
import java.security.SecureRandom;
import java.security.Security;
import java.util.ArrayList;
import java.util.Arrays;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertArrayEquals;

/**
 * Created by mzy on 2017/4/18.
 */
public class Sm9test {

    private Sm9Curve sm9Curve;
    private Sm9Curve2 sm9Curve2;
    private KeyGenerationCenter kgc;
    private String id ="maerye@123.com";
    private String testString ="testIng";
    @Before
    public void init()
    {
        kgc=KeyGenerationCenter.getInstance();
        sm9Curve=Sm9Curve.getInstance();
        sm9Curve2=new Sm9Curve2(sm9Curve);
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            BouncyCastleProvider bcp = new BouncyCastleProvider();
            Security.addProvider(bcp);
        }
    }

    @Test
    public void testSign ()throws Exception
    {
       BigInteger u = new BigInteger("110000010000000000000000000000000000000000001000000000001000011", 2);
        System.out.println(u.bitLength()+" "+u.toString(16));
        Sm9SignPrivateKey privateKey = kgc.generateSignPrivatekey(id);
        Sm9Signer signer =new Sm9Signer();
        signer.initSign(privateKey);
        Signature signature=signer.generateSignature(testString.getBytes());

        signer.initVerify(id);
        assertTrue (signer.verifySignature(testString.getBytes(),signature));

        String x="dsad";
        byte []xs=x.getBytes();
        assertEquals(x,new String(xs));
    }
    @Test
    public void testSignBigmessage() throws Exception{
        int [] msl=new int[]{10000,500,1000,5000,10000,50000,100000,250000,500000,100};
        long[] temp;
        for(int i =0;i<msl.length;i++) {
            temp = testSign(10, msl[i]);
            System.out.println("length :"+msl[i] +" " +temp[0] + " " + temp[1]);
        }

    }
    public long[] testSign(int round, int messagelen) throws Exception{
        byte [] message=new byte [messagelen];
        Sm9SignPrivateKey privateKey = kgc.generateSignPrivatekey(id);
        for (int i=0;i<messagelen;i++){
            message[i]=0x4f;
        }
        Sm9Signer signer =new Sm9Signer();
        Signature signature=null;
        long start=System.currentTimeMillis();
        for(int i=0;i<round;i++) {
            signer.initSign(privateKey);
            signature = signer.generateSignature(message);
        }
        long end1=System.currentTimeMillis();
        for(int i=0;i<round;i++) {
            signer.initVerify(id);
            signer.verifySignature(message, signature);
        }
        long end=System.currentTimeMillis();

        return new long[]{(end1-start)/round,(end-end1)/round};

    }
    @Test
    public void testEncapsulate ()throws Exception
    {
        KEM kem=new KEM();
        kgc=KeyGenerationCenter.getInstance();
        Sm9EncryptPrivateKey privateKey=kgc.generateEncrypyPrivateKey(id);
        EncapsulatedKey encapsulatedKey=kem.encapsulate(id.getBytes(),256);
        byte[] k=kem.decapsulate(encapsulatedKey.getC(),id.getBytes(),privateKey,256);

        System.out.println("encapulated :"+ Arrays.toString(encapsulatedKey.getK()));
        System.out.println("decapulate: "+Arrays.toString(k));

        assertArrayEquals(encapsulatedKey.getK(),k);
    }
    @Test
    public void testEncrypt()throws Exception{
        kgc= KeyGenerationCenter.getInstance();
        Sm9EncryptPrivateKey privateKey=kgc.generateEncrypyPrivateKey(id);
        Cipher cipher=Cipher.getInstance("SM4/ECB/NoPadding","BC");
        Sm9Engine sm9Engine=new Sm9Engine(cipher);
        String s="0123456789abcdeffedcba9876543210";
        String [] ss=new String[15];
        byte [] m=s.getBytes();
        byte []ciphertext=null;
        byte[] mp=null;
        long start=System.currentTimeMillis();
        sm9Engine.initEncrypt(true,id,16,32,1);
        for(int i=0;i<15;i++)
        {
           ciphertext=sm9Engine.processBlock(m,0,m.length);
        }

        long end=System.currentTimeMillis();



        sm9Engine.initDecrypt(false,id,privateKey,16,32,1);
        for(int i=0;i<15;i++) {
            mp = sm9Engine.processBlock(ciphertext, 0, ciphertext.length);
        }
        long end1=System.currentTimeMillis();
        System.out.println("mb:"+m.length);
        System.out.println("encrypt time:"+(end-start));
        System.out.println("decrypt time:"+(end1-end));

        assertArrayEquals(m,mp);

        sm9Engine.initEncrypt(true,id,16,32,1);
        byte [] m2="0123456789abcdeffedcba9876543210".getBytes();
        byte []ciphertext2=sm9Engine.processBlock(m,0,m.length);

        sm9Engine.initDecrypt(false,id,privateKey,16,32,1);
        byte []mp2=sm9Engine.processBlock(ciphertext2,0,ciphertext2.length);

        assertArrayEquals(m2,mp2);
    }
    @Test
    public void testEncrptall()throws Exception{
        int [] length=new int[]{3,10,15,20,25,30,50,55,60,65};
        int rounds=5;
        long [] temp;
        long tempenc,tempdec;
        for(int i=0;i<length.length;i++)
        {
            tempenc=0;
            tempdec=0;
            for(int j=0;j<rounds;j++){
                System.out.println("round :"+j);
                temp=testEnc(length[i]);
                tempenc+=temp[0];
                tempdec+=temp[1];
            }
            System.out.println("length : "+length[i]*32+"enc:"+tempenc/rounds+"dec : "+tempdec/rounds);
        }
    }
    public long [] testEnc(int round)throws Exception{
        kgc= KeyGenerationCenter.getInstance();
        Sm9EncryptPrivateKey privateKey=kgc.generateEncrypyPrivateKey(id);
        Cipher cipher=Cipher.getInstance("SM4/ECB/NoPadding","BC");
        Sm9Engine sm9Engine=new Sm9Engine(cipher);
        String s="0123456789abcdeffedcba9876543210";
        String s1="0123456789abcdef";
        byte [] m=s.getBytes();
        byte []ciphertext=null;
        byte[] mp=null;
        long start=System.currentTimeMillis();
        sm9Engine.initEncrypt(true,id,16,32,0);
        for(int i=0;i<round;i++)
        {
            ciphertext=sm9Engine.processBlock(m,0,m.length);
        }

        long end=System.currentTimeMillis();

        sm9Engine.initDecrypt(false,id,privateKey,16,32,0);
        for(int i=0;i<round;i++) {
            mp = sm9Engine.processBlock(ciphertext, 0, ciphertext.length);
        }
        long end1=System.currentTimeMillis();

        return new long[]{(end-start),(end1-end)};

    }
    @Test
    public void testh() {
        BigInteger N =new BigInteger("B640000002A3A6F1D603AB4FF58EC74449F2934B18EA8BEEE56EE19CD69ECF25",16);
        byte [] ida="Alice".getBytes();
        assertEquals(new BigInteger("416C696365",16),new BigInteger(ida));
        byte [] hid=new byte[]{0x01};
        byte []merge=Sm9Util.byteMerger(ida,hid);
        BigInteger h1=Sm9Util.h1(merge,N);
        assertEquals(new BigInteger("2ACC468C3926B0BDB2767E99FF26E084DE9CED8DBC7D5FBF418027B667862FAB",16),h1);

        BigInteger ks=new BigInteger("0130E78459D78545CB54C587E02CF480CE0B66340F319F348A1D5B1F2DC5F4",16);
        BigInteger t1=h1.add(new BigInteger("0130E78459D78545CB54C587E02CF480CE0B66340F319F348A1D5B1F2DC5F4",16)).mod(N);

        assertEquals(new BigInteger("2ACD7773BD808842F841D35F87070D795F6AF8F3F08C915E760A451186B3F59F",16),t1);
        BigInteger t2=ks.multiply(t1.modInverse(N)).mod(N);

        assertEquals(new BigInteger("291FE3CAC8F58AD2DC462C8D4D578A94DAFD5624DDC28E328D2936688A86CF1A",16),t2);

        byte [] hm=new BigInteger("4368696E65736520494253207374616E6461726481377B8FDBC2839B4FA2D0E0F8AA6853BBBE9E9C" +
                "4099608F8612C6078ACD7563815AEBA217AD502DA0F48704CC73CABB3C06209BD87142E14CBD99E8" +
                "BCA1680F30DADC5CD9E207AEE32209F6C3CA3EC0D800A1A42D33C73153DED47C70A39D2E8EAF5D17" +
                "9A1836B359A9D1D9BFC19F2EFCDB829328620962BD3FDF15F2567F58A543D25609AE943920679194" +
                "ED30328BB33FD15660BDE485C6B79A7B32B013983F012DB04BA59FE88DB889321CC2373D4C0C35E8" +
                "4F7AB1FF33679BCA575D67654F8624EB435B838CCA77B2D0347E65D5E46964412A096F4150D8C5ED" +
                "E5440DDF0656FCB663D24731E80292188A2471B8B68AA993899268499D23C89755A1A89744643CEA" +
                "D40F0965F28E1CD2895C3D118E4F65C9A0E3E741B6DD52C0EE2D25F5898D60848026B7EFB8FCC1B2" +
                "442ECF0795F8A81CEE99A6248F294C82C90D26BD6A814AAF475F128AEF43A128E37F80154AE6CB92" +
                "CAD7D1501BAE30F750B3A9BD1F96B08E97997363911314705BFB9A9DBB97F75553EC90FBB2DDAE53" +
                "C8F68E42",16).toByteArray();
       BigInteger h2=Sm9Util.h2(hm,N);
        assertEquals(new BigInteger("823C4B21E4BD2DFE1ED92C606653E996668563152FC33F55D7BFBB9BD9705ADB",16),h2);

    }

    @Test
    public void testKDF(){
        BigInteger merge=new BigInteger("1EDEE2C3F465914491DE44CEFB2CB434AB02C308D9DC5E2067B4FED5AAAC8A0F1C9B4C43" +
                "5ECA35AB83BB734174C0F78FDE81A53374AFF3B3602BBC5E37BE9A4C8EAB0CD6D0C95A6B" +
                "BB7051AC848FDFB9689E5E5C486B1294557189B338B53B1D78082BB40152DC35AC774442" +
                "CC6408FFD68494D9953D77BF55E30E84697F66745AAF52239E46B0373B3168BAB75C32E0" +
                "48B5FAEBABFA1F7F9BA6B4C0C90E65B075F6A2D9ED54C87CDDD2EAA787032320205E7AC7" +
                "D7FEAA8695AB2BF7F5710861247C2034CCF4A1432DA1876D023AD6D74FF1678FDA3AF37A" +
                "3D9F613CDE8057988B07151BAC93AF48D78D86C26EA97F24E2DACC84104CCE8791FE90BA" +
                "61B2049CAAC6AB38EA07F9966173FD9BBF34AAB58EE84CD3777A9FD00BBCA1DC09CF8696" +
                "A1040465BD723AE513C4BE3EF2CFDC088A935F0B207DEED7AAD5CE2FC37D42034D874A4C" +
                "E9B3B58765B1252A0880952B4FF3C97EA1A4CFDC67A0A0072541A03D3924EABC443B0503" +
                "510B93BBCD98EB70E0192B821D14D69CCB2513A1A7421EB7A018A035E8FB61F271DE1C5B" +
                "3E781C63508C113B3EAC537805EAE164D732FAD056BEA27C8624D5064C9C278A193D63F6" +
                "908EE558DF5F5E0721317FC6E829C242426F62",16);
        byte [] k=Sm9Util.KDF(merge.toByteArray(),0x0100);
        assertArrayEquals(new BigInteger("4FF5CF86D2AD40C8F4BAC98D76ABDBDE0C0E2F0A829D3F911EF5B2BCE0695480",16).toByteArray(),k);
    }

    @Test
    public void testlibrary(){
        BNPoint g1=sm9Curve.getG();
        BNPoint2 g2=sm9Curve2.getGt();
        assertBool("g1 is on ec",sm9Curve.getBnCurve().contains(g1));
        assertBool("g2 is on ec",sm9Curve2.getBNCurve2().contains(g2));



        BigInteger N=sm9Curve.getN();
        BNPoint t=g1.multiply(N);
        assertBool("order of g1 == N ",t.isZero());

        BNPoint2 t2=g2.multiply(N);
        assertBool("order of g2== N",t2.isZero());

        BigInteger a =new BigInteger("1232131");
        BigInteger b=new BigInteger("321314443");
        BigInteger c=a.add(b);

        BNPoint p1=g1.multiply(c);
        BNPoint p11=g1.multiply(a).add(g1.multiply(b));

        System.out.println("p11"+p11);
        System.out.println("normalize p11"+p11.normalize());
        byte [] x=Sm9Util.bigIntegerTobytes(p11.normalize().getX());
        byte [] y=Sm9Util.bigIntegerTobytes(p11.normalize().getY());

        BNPoint p11copy=new BNPoint(sm9Curve.getBnCurve(),new BigInteger(1,x),new BigInteger(1,y));
        assertEqual("copy success",p11,p11copy);

        assertEqual("check g1 * c = g1 * a + g1 * b",p1,p11);

        BNPairing pairing =new BNPairing(sm9Curve2.getBNCurve2());

        BNPoint2 g2a=g2.multiply0(a);//???

        BNPoint2 g2ap=sm9Curve2.getBNCurve2().kG(a);

        assertEqual("g2a = g2ap :",g2a,g2ap);
//
        BNPoint2 g21=g2.multiply0(new BigInteger("1"));
        assertEqual("g21 = g2 :",g21,g2);
//        BNPoint2 g211=sm9Curve2.getBNCurve2().kG(new BigInteger("1"));
//        assertEqual("g211 = g2 :",g211,g2);
//
//        BNPoint2 g22=sm9Curve2.getBNCurve2().kG(new BigInteger("2"));
//        BNPoint2 g2tw=g2.twice(1);
//        assertEqual("g22 = g2tw :",g22,g2tw);



//        BigInteger a0 ,a1,a2,a3,a4,a5,a6,a7,a8,a9,a10,a11,p,sm9q;
//        a0 =new BigInteger("AAB9F06A4EEBA4323A7833DB202E4E35639D93FA3305AF73F0F071D7D284FCFB",16);
//        a1= new BigInteger("84B87422330D7936EABA1109FA5A7A7181EE16F2438B0AEB2F38FD5F7554E57A",16);
//        a2=new BigInteger("4C744E69C4A2E1C8ED72F796D151A17CE2325B943260FC460B9F73CB57C9014B",16);
//        a3=new BigInteger("B3129A75D31D17194675A1BC56947920898FBF390A5BF5D931CE6CBB3340F66D",16);
//        a4=new BigInteger("93634F44FA13AF76169F3CC8FBEA880ADAFF8475D5FD28A75DEB83C44362B439",16);
//        a5=new BigInteger("1604A3FCFA9783E667CE9FCB1062C2A5C6685C316DDA62DE0548BAA6BA30038B",16);
//        a6=new BigInteger("5A1AE172102EFD95DF7338DBC577C66D8D6C15E0A0158C7507228EFB078F42A6",16);
//        a7=new BigInteger("67E0E0C2EED7A6993DCE28FE9AA2EF56834307860839677F96685F2B44D0911F",16);
//        a8=new BigInteger("A01F2C8BEE81769609462C69C96AA923FD863E209D3CE26DD889B55E2E3873DB",16);
//        a9=new BigInteger("38BFFE40A22D529A0C66124B2C308DAC9229912656F62B4FACFCED408E02380F",16);
//        a10=new BigInteger("28B3404A61908F5D6198815C99AF1990C8AF38655930058C28C21BB539CE0000",16);
//        a11=new BigInteger("4E378FB5561CD0668F906B731AC58FEE25738EDF09CADC7A29C0ABC0177AEA6D",16);
//        p=new BigInteger("16798108731015832284940804142231733909889187121439069848933715426072753864723",10);
//        sm9q=new BigInteger("B640000002A3A6F1D603AB4FF58EC74521F2934B1A7AEEDBE56F9B27E351457D",16);
//
//        BNField2 [] g=new BNField2[6];
//
//        g[0]=new BNField2(new BigInteger("AAB9F06A4EEBA4323A7833DB202E4E35639D93FA3305AF73F0F071D7D284FCFB",16),new BigInteger("84B87422330D7936EABA1109FA5A7A7181EE16F2438B0AEB2F38FD5F7554E57A",16));
//        g[1]=new BNField2(new BigInteger("4C744E69C4A2E1C8ED72F796D151A17CE2325B943260FC460B9F73CB57C9014B",16),new BigInteger("B3129A75D31D17194675A1BC56947920898FBF390A5BF5D931CE6CBB3340F66D",16));
//        g[2]=new BNField2(new BigInteger("93634F44FA13AF76169F3CC8FBEA880ADAFF8475D5FD28A75DEB83C44362B439",16),new BigInteger("1604A3FCFA9783E667CE9FCB1062C2A5C6685C316DDA62DE0548BAA6BA30038B",16));
//        g[3]=new BNField2(new BigInteger("5A1AE172102EFD95DF7338DBC577C66D8D6C15E0A0158C7507228EFB078F42A6",16),new BigInteger("67E0E0C2EED7A6993DCE28FE9AA2EF56834307860839677F96685F2B44D0911F",16));
//        g[4]=new BNField2(new BigInteger("A01F2C8BEE81769609462C69C96AA923FD863E209D3CE26DD889B55E2E3873DB",16),new BigInteger("38BFFE40A22D529A0C66124B2C308DAC9229912656F62B4FACFCED408E02380F",16));
//        g[5]=new BNField2(new BigInteger("28B3404A61908F5D6198815C99AF1990C8AF38655930058C28C21BB539CE0000",16),new BigInteger("4E378FB5561CD0668F906B731AC58FEE25738EDF09CADC7A29C0ABC0177AEA6D",16));
//
//        String gpoint="[[["+ a0.toString(10)+","+a1.toString(10)+"],\n["+a2.toString(10)+","+a3.toString(10)+"],\n["+a4.toString(10)+","+a5.toString(10)+"]],\n"
//                +"[["+a6.toString(10)+","+a7.toString(10)+"],\n["+a8.toString(10)+","+a9.toString(10)+"],\n["+a10.toString(10)+","+a11.toString(10)+"]]]";
//
//        System.out.println("a0:"+a0.toString(10)+" a0 mod p :"+a0.mod(p).toString(10)+" a0 mod q:"+a0.mod(sm9q).toString(16));
//        System.out.println("a0 0x"+a0.toString(16));
//
//        Fp12 ng= new Fp12();
//        ng.set(gpoint);
//        System.out.println(ng.toString());
//       byte[] ngbytes= Sm9Util.Fp12ToBytes(ng);
    }
    @Test
    public void testPair(){
       BNPoint g1=sm9Curve.getG();
       BNPoint2 g2=sm9Curve2.getGt();

       BNPairing pairing=new BNPairing(sm9Curve2.getBNCurve2());

       BNField12 e=pairing.eta(g1,g2);

       BigInteger a=new BigInteger("1232");

       BNPoint2 g2a=g2.multiply0(a);
       BNPoint g1a=g1.multiply(a);

       assertTrue(sm9Curve2.getBNCurve2().contains(g2a));

       BNField12 e2=pairing.eta(g1,g2a);
       BNField12 e22=pairing.eta(g1a,g2);
       assertEquals(e22,e.exp(a));

       BNPoint g=g1.add(g1a);

      BNField12 e3= pairing.eta(g,g2);
      assertEquals(e3,e.multiply(e22));
    }

    public static void assertBool(String msg, Boolean b) {
        if (b) {
            System.out.println("OK : " + msg);
        } else {
            System.out.println("NG : " + msg);
        }
    }
    public static void assertEqual(String msg, Object lhs, Object rhs) {
        if (lhs.equals(rhs)) {
            System.out.println("OK : " + msg);
        } else {
            System.out.println("NG : " + msg + ", lhs = " + lhs + "\n rhs = " + rhs);
        }
    }
}
