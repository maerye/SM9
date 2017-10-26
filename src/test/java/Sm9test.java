import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIFactoryMethod;
import iaik.security.ec.common.PointEncoders;

import mcl.bn254.Ec1;
import mcl.bn254.Fp;
import mcl.bn254.Fp12;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import src.*;

import javax.crypto.Cipher;
import java.math.BigInteger;
import java.security.Key;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Arrays;
import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import iaik.security.provider.IAIK;
import iaik.utils.CryptoUtils;
import iaik.security.ec.common.SecurityStrength;
import iaik.security.ec.math.curve.AtePairingOverBarretoNaehrigCurveFactory;
import iaik.security.ec.math.curve.ECPoint;
import iaik.security.ec.math.curve.EllipticCurve;
import iaik.security.ec.math.curve.Pairing;
import iaik.security.ec.math.curve.PairingTypes;
import iaik.security.ec.math.field.GenericField;
import iaik.security.ec.math.field.GenericFieldElement;
import src.api.*;
import src.field.curve.CurveElement;
import src.field.curve.CurveField;
import src.field.gt.GTFiniteElement;
import src.field.gt.GTFiniteField;
import src.field.poly.PolyModField;
import src.field.quadratic.QuadraticField;
import src.field.z.ZrElement;
import src.field.z.ZrField;
import src.pairing.f.TypeFCurveGenerator;
import src.pairing.f.TypeFPairing;


import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertArrayEquals;

/**
 * Created by mzy on 2017/4/18.
 */
public class Sm9test {


    private KeyGenerationCenter kgc;
    private String id ="maerye@123.com";
    private String testString ="testIng";
    @Before
    public void init()
    {
        kgc=KeyGenerationCenter.getInstance();
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            BouncyCastleProvider bcp = new BouncyCastleProvider();
            Security.addProvider(bcp);
        }
    }



    @Test
    public void testSign ()throws Exception
    {

        Sm9SignPrivateKey privateKey = kgc.generateSignPrivatekey(id);
        Sm9Signer signer =new Sm9Signer();
        signer.initSign(privateKey);
        Signature signature=signer.generateSignature(testString.getBytes());

        signer.initVerify(id);
        assertTrue (signer.verifySignature(testString.getBytes(),signature));

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
        sm9Engine.initEncrypt(true,id,16,32,1);
        byte [] m="0123456789abcdeffedcba9876543210".getBytes();
        byte []ciphertext=sm9Engine.processBlock(m,0,m.length);

        sm9Engine.initDecrypt(false,id,privateKey,16,32,1);
        byte []mp=sm9Engine.processBlock(ciphertext,0,ciphertext.length);
        assertArrayEquals(m,mp);
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
    public void testKDf(){
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
    public void testEc1tobytes()
    {
        /*BigInteger cx=new BigInteger("1EDEE2C3F465914491DE44CEFB2CB434AB02C308D9DC5E2067B4FED5AAAC8A0F",16);
        BigInteger cy=new BigInteger("1C9B4C435ECA35AB83BB734174C0F78FDE81A53374AFF3B3602BBC5E37BE9A4C",16);
//        Ec1  c = new Ec1(new Fp(cx.toString(10)),new Fp(cy.toString(10)));*/
//        kgc=KeyGenerationCenter.getInstance();
//        Ec1 c=new Ec1(kgc.getG1());
//        byte [] cb=Sm9Util.ec1ToBytes(c);
//        System.out.println(new BigInteger(cb).toString(16));
//
//        System.out.println(cb.length);
    }

    @Test
    public void testIAIK(){
        try {
            // this is the dynamic registration mentioned before
            IAIK.addAsProvider();

            byte[] data = "Hello Secure World!".getBytes("ASCII");

            byte[] tripleDesKeyBytes = new byte[24];
            (new SecureRandom()).nextBytes(tripleDesKeyBytes);
            Key tripleDesKey = new SecretKeySpec(tripleDesKeyBytes, "DESede");

            Cipher tripleDesCipher = Cipher.getInstance("DESede/CBC/PKCS5Padding", "IAIK");
            byte[] ivBytes = new byte[tripleDesCipher.getBlockSize()];
            (new SecureRandom()).nextBytes(ivBytes);
            IvParameterSpec iv = new IvParameterSpec(ivBytes);

            tripleDesCipher.init(Cipher.ENCRYPT_MODE, tripleDesKey, iv);
            byte[] cipherText = tripleDesCipher.doFinal(data);

            tripleDesCipher.init(Cipher.DECRYPT_MODE, tripleDesKey, iv);
            byte[] plainText = tripleDesCipher.doFinal(cipherText);

            if (CryptoUtils.equalsBlock(data, plainText)) {
                System.out.println("Test successful.");
            } else {
                System.err.println("Test FAILED!");
                System.exit(1);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(2);
        }
    }
    @Test
    public void testIAIKPairing(){
        int size=256;
        final Pairing pairing3 = AtePairingOverBarretoNaehrigCurveFactory
                .getPairing(PairingTypes.TYPE_3, size);

        EllipticCurve curve1 = pairing3.getGroup1();
        EllipticCurve curve2 = pairing3.getGroup2();
        GenericField target = pairing3.getTargetGroup();

        BigInteger order1=curve1.getOrder();
        BigInteger order2=curve2.getOrder();
        System.out.println("order1:"+order1);
        System.out.println("order2:"+order2);
        System.out.println("order2/1:"+order2.divide(order1));
        BigInteger pp=new BigInteger("ba139ec2401edc28fb605c6b53e289b51311aca0d477df46feee89b1622c349b",16);
        BigInteger ca=curve1.getField().getCardinality();

        System.out.println("base p:"+curve2.getField());

      if(order1.doubleValue()> Math.pow(2,191))
      {

          System.out.println("dayu");
      }

        System.out.println("---");
        System.out.println("Type-3 pairing:");
        System.out.println("G1: " + curve1);
        System.out.println("G2: " + curve2);
        System.out.println("target group: " + target);

        // obtain generators of the two curves
        ECPoint p = curve1.getGenerator();
        ECPoint q = curve2.getGenerator();

        System.out.println("---");

        System.out.println("p = " + p.scalePoint().getCoordinate());
        System.out.println("p x= " + p.scalePoint().getCoordinate().getX().toByteArray().length);
        System.out.println("p y= " + p.scalePoint().getCoordinate().getY().toByteArray().length);
        BigInteger bigInteger=new BigInteger(1,p.scalePoint().getCoordinate().getY().toByteArray());
        System.out.println("py test "+bigInteger.toString(16));

        System.out.println("q = " + q.scalePoint().getCoordinate());

        System.out.println("q x= " + q.scalePoint().getCoordinate().getX());
        byte [] qx=q.scalePoint().getCoordinate().getX().toByteArray();
        byte [] a0=new byte[32];
        byte [] a1=new byte[32];
        System.arraycopy(qx,0,a0,0,32);
        System.arraycopy(qx,32,a1,0,32);
        BigInteger a0B=new BigInteger(1,a0);
        BigInteger a1B=new BigInteger(1,a1);
        System.out.println("a0 :"+a0B.toString(16));
        System.out.println("a1 :"+a1B.toString(16));
        System.out.println("q y= " + q.scalePoint().getCoordinate().getY());


        // compute pairing of p and q
        GenericFieldElement t = pairing3.pair(p, q);
        System.out.println("p mul order " + p.multiplyPoint(order2).isNeutralPoint());
        System.out.println("q mul order " + q.multiplyPoint(order2).isNeutralPoint());
        System.out.println("t power order " + t.exponentiate(order2).isOne());


        System.out.println("---");
        System.out.println("e(p,q) = " + t);
        System.out.println("t 1 byte length"+t.getField().getOne().toByteArray().length);

        // get scalars
        final SecureRandom random = SecurityStrength
                .getSecureRandom(SecurityStrength.getSecurityStrength(curve1.getField().getFieldSize()));
        BigInteger k1 = new BigInteger(size - 1, random);
        BigInteger k2 = new BigInteger(size - 1, random);

        // hash onto curve 1
        final ECPoint p2 = curve1.hashToPoint(iaik.utils.Util.toByteArray("Test hashing onto curve 1"));

        // multiply points from curve 2 with scalars
        ECPoint r = q.clone().multiplyPoint(k1);
        ECPoint s = q.clone().multiplyPoint(k2);

        // compute pairing of p2 and r, and p2 and s
        GenericFieldElement[] ts = pairing3.pair(p2, new ECPoint[] { r, s });

        System.out.println("e(p2,r) = " + ts[0]);
        System.out.println("e(p2,s) = " + ts[1]);

        // hash onto curve 2
        final ECPoint q2 = curve2.hashToPoint(iaik.utils.Util.toByteArray("Test hashing onto curve 2"));

        // multiply points from curve 1 with scalars
        r = p.clone().multiplyPoint(k1);
        s = p.clone().multiplyPoint(k2);

       byte [] rx= r.scalePoint().getCoordinate().getX().toByteArray();
       byte [] ry= r.scalePoint().getCoordinate().getY().toByteArray();
       System.out.println(r.scalePoint().getCoordinate().getX());
        System.out.println(r.scalePoint().getCoordinate().getY());
        BigInteger rxB=new BigInteger(1,rx);
        BigInteger ryB=new BigInteger(1,ry);
        System.out.println(rxB.toString(16));
        System.out.println(ryB.toString(16));
        java.security.spec.ECPoint rcopy=new java.security.spec.ECPoint(rxB,ryB);
        ECPoint rp=curve1.newPoint(rcopy);
        if(r.equals(rp))
        {
            System.out.println("copy success");

        }

       byte [] merge=Sm9Util.byteMerger(rx,ry);


        // compute pairing of q2 and r, and q2 and s
        ts = pairing3.pair(new ECPoint[] { r, s }, q2);

        System.out.println("e(r,q2) = " + ts[0]);
        System.out.println("e(s,q2) = " + ts[1]);

        System.out.println("---");

        // create Type-2 pairing
        final Pairing pairing2 = AtePairingOverBarretoNaehrigCurveFactory
                .getPairing(PairingTypes.TYPE_2, size);

        curve1 = pairing2.getGroup1();
        curve2 = pairing2.getGroup2();

        target = pairing2.getTargetGroup();
        BigInteger order11=curve1.getOrder();
        BigInteger order22=curve2.getOrder();
        BigInteger orderDiv=order22.divide(order11);
        System.out.println("type2 order1:"+order11);
        System.out.println("type2 order2:"+order22);
        System.out.println(" order2/order1:"+orderDiv);
        if(order1.doubleValue()> Math.pow(2,191))
        {

            System.out.println("dayu");
        }
        System.out.println("Type-2 pairing:");
        System.out.println("G1: " + curve1);
        System.out.println("G2: " + curve2);
        System.out.println("target group: " + target);

        // obtain generators of the two curves
        p = curve1.getGenerator();
        q = curve2.getGenerator();
        q.multiplyPoint(order22);
        System.out.println("---");
        System.out.println("px = " + p.scalePoint().getCoordinate().getX().toByteArray().length);
        System.out.println("py = " + p.scalePoint().getCoordinate().getY().toByteArray().length);
        ECPoint temp=p.clone().multiplyPoint(order11);
        ECPoint temp2=q.multiplyPoint(order11);

        System.out.println("p *order= " + temp.isNeutralPoint());
        System.out.println("q *order= " + temp2.isNeutralPoint());

        // compute pairing of p and q
        t = pairing2.pair(p, q);
        System.out.println("t power order = " + t.exponentiate(order11).isOne());

        System.out.println("type 2 ONE "+ t.getField().getOne().toByteArray().length);

        System.out.println("---");
        System.out.println("e(p,q) = " + t);

        // apply the isomporphism from curve 2 to curve 1 (result is the same as in
        // e(p,q))
        t = pairing2.pair(pairing2.applyIsomorphism(q), q);
        System.out.println("e(Psi(q),q) = " + t);

        // get scalars
        k1 = new BigInteger(size - 1, random);
        k2 = new BigInteger(size - 1, random);

        // hash onto curve 1
        final ECPoint p3 = curve1.hashToPoint(iaik.utils.Util.toByteArray("Test hashing onto curve 1"));

        // multiply points from curve 2 with scalars
        r = q.clone().multiplyPoint(k1);
        s = q.clone().multiplyPoint(k2);

        // compute pairing of p3 and r, and p3 and s
        ts = pairing2.pair(p3, new ECPoint[] { r, s });

        System.out.println("e(p3,r) = " + ts[0]);
        System.out.println("e(p3,s) = " + ts[1]);

    }

    @Test
    public void testJpbc(){
        BigInteger xp=new BigInteger("93DE051D62BF718FF5ED0704487D01D6E1E4086909DC3280E8C4E4817C66DDDD",16);
        BigInteger yp=new BigInteger("21FE8DDA4F21E607631065125C395BBC1C1C00CBFA6024350C464CD70A3EA616",16);

        BigInteger xp21=new BigInteger("3722755292130B08D2AAB97FD34EC120EE265948D19C17ABF9B7213BAF82D65B",16);
        BigInteger xp22=new BigInteger("85AEF3D078640C98597B6027B441A01FF1DD2C190F5E93C454806C11D8806141",16);
        BigInteger yp21=new BigInteger("A7CF28D519BE3DA65F3170153D278FF247EFBA98A71A08116215BBA5C999A7C7",16);
        BigInteger yp22=new BigInteger("17509B092E845C1266BA0D262CBEE6ED0736A96FA347C8BD856DC76B84EBEB96",16);

        BigInteger q=new BigInteger("B640000002A3A6F1D603AB4FF58EC74521F2934B1A7AEEDBE56F9B27E351457D",16);

        BigInteger ks= new BigInteger("0130E78459D78545CB54C587E02CF480CE0B66340F319F348A1D5B1F2DC5F4",16);
        BigInteger a1=new BigInteger("29DBA116152D1F786CE843ED24A3B573414D2177386A92DD8F14D65696EA5E32",16);
        BigInteger a2=new BigInteger("9F64080B3084F733E48AFF4B41B565011CE0711C5E392CFB0AB1B6791B94C408",16);
        BigInteger a3=new BigInteger("41E00A53DDA532DA1A7CE027B7A46F741006E85F5CDFF0730E75C05FB4E3216D",16);
        BigInteger a4=new BigInteger("69850938ABEA0112B57329F447E3A0CBAD3E2FDB1A77F335E89E1408D0EF1C25",16);

        BigInteger t2=new BigInteger("291FE3CAC8F58AD2DC462C8D4D578A94DAFD5624DDC28E328D2936688A86CF1A",16);
        BigInteger xa=new BigInteger("A5702F05CF1315305E2D6EB64B0DEB923DB1A0BCF0CAFF90523AC8754AA69820",16);
        BigInteger xb=new BigInteger("78559A844411F9825C109F5EE3F52D720DD01785392A727BB1556952B2B013D3",16);

        BigInteger ppubsx1=new BigInteger("29DBA116152D1F786CE843ED24A3B573414D2177386A92DD8F14D65696EA5E32",16);
        BigInteger g0=new BigInteger("AAB9F06A4EEBA4323A7833DB202E4E35639D93FA3305AF73F0F071D7D284FCFB",16);
        BigInteger g1=new BigInteger("84B87422330D7936EABA1109FA5A7A7181EE16F2438B0AEB2F38FD5F7554E57A",16);
        BigInteger g2=new BigInteger("4C744E69C4A2E1C8ED72F796D151A17CE2325B943260FC460B9F73CB57C9014B",16);
        BigInteger g3=new BigInteger("B3129A75D31D17194675A1BC56947920898FBF390A5BF5D931CE6CBB3340F66D",16);
        BigInteger g4=new BigInteger("93634F44FA13AF76169F3CC8FBEA880ADAFF8475D5FD28A75DEB83C44362B439",16);
        BigInteger g5=new BigInteger("1604A3FCFA9783E667CE9FCB1062C2A5C6685C316DDA62DE0548BAA6BA30038B",16);
        BigInteger g6=new BigInteger("5A1AE172102EFD95DF7338DBC577C66D8D6C15E0A0158C7507228EFB078F42A6",16);
        BigInteger g7=new BigInteger("67E0E0C2EED7A6993DCE28FE9AA2EF56834307860839677F96685F2B44D0911F",16);
        BigInteger g8=new BigInteger("A01F2C8BEE81769609462C69C96AA923FD863E209D3CE26DD889B55E2E3873DB",16);
        BigInteger g9=new BigInteger("38BFFE40A22D529A0C66124B2C308DAC9229912656F62B4FACFCED408E02380F",16);
        BigInteger g10=new BigInteger("28B3404A61908F5D6198815C99AF1990C8AF38655930058C28C21BB539CE0000",16);
        BigInteger g11=new BigInteger("4E378FB5561CD0668F906B731AC58FEE25738EDF09CADC7A29C0ABC0177AEA6D",16);
        BigInteger r=new BigInteger("033C8616B06704813203DFD00965022ED15975C662337AED648835DC4B1CBE",16);

        BigInteger g12r1=new BigInteger("1F96B08E97997363911314705BFB9A9DBB97F75553EC90FBB2DDAE53C8F68E42",16);
        BigInteger g12r2=new BigInteger("6A814AAF475F128AEF43A128E37F80154AE6CB92CAD7D1501BAE30F750B3A9BD",16);
        BigInteger g12r3=new BigInteger("898D60848026B7EFB8FCC1B2442ECF0795F8A81CEE99A6248F294C82C90D26BD",16);
        BigInteger g12r4=new BigInteger("44643CEAD40F0965F28E1CD2895C3D118E4F65C9A0E3E741B6DD52C0EE2D25F5",16);
        BigInteger g12r5=new BigInteger("0656FCB663D24731E80292188A2471B8B68AA993899268499D23C89755A1A897",16);
        BigInteger g12r6=new BigInteger("4F8624EB435B838CCA77B2D0347E65D5E46964412A096F4150D8C5EDE5440DDF",16);
        BigInteger g12r7=new BigInteger("3F012DB04BA59FE88DB889321CC2373D4C0C35E84F7AB1FF33679BCA575D6765",16);
        BigInteger g12r8=new BigInteger("A543D25609AE943920679194ED30328BB33FD15660BDE485C6B79A7B32B01398",16);
        BigInteger g12r9=new BigInteger("8EAF5D179A1836B359A9D1D9BFC19F2EFCDB829328620962BD3FDF15F2567F58",16);
        BigInteger g12r10=new BigInteger("30DADC5CD9E207AEE32209F6C3CA3EC0D800A1A42D33C73153DED47C70A39D2E",16);
        BigInteger g12r11=new BigInteger("815AEBA217AD502DA0F48704CC73CABB3C06209BD87142E14CBD99E8BCA1680F",16);
        BigInteger g12r12=new BigInteger("81377B8FDBC2839B4FA2D0E0F8AA6853BBBE9E9C4099608F8612C6078ACD7563",16);

        BigInteger pair0=new BigInteger("AAB9F06A4EEBA4323A7833DB202E4E35639D93FA3305AF73F0F071D7D284FCFB",16);
        SecureRandom random =new SecureRandom();

//        Field Fq = new ZrField(random, q);
//        Field Fq2 = new QuadraticField(random, Fq);

        BigInteger test=new BigInteger("15376082189538387440900211051596494061397537732182077032922339670147106514454",10);
        System.out.println("test: "+test.toString(16));
        String xbits=xp.toString(2);
        String[] sarray=xbits.split("");
        System.out.println(sarray.length);
        int rBits=256;
        PairingParametersGenerator pairingParametersGenerator=new TypeFCurveGenerator(rBits);
        PairingParameters parameters=pairingParametersGenerator.generate();

        System.out.println(parameters.toString());

        TypeFPairing pairing=new TypeFPairing(parameters);

        CurveField G1=(CurveField) pairing.getG1();
        CurveField G2=(CurveField)pairing.getG2();
        GTFiniteField GT=(GTFiniteField)pairing.getGT();


        CurveElement in1=G1.newElement();
        in1.getX().set(xp);
        in1.getY().set(yp);
        in1.setInfFlag(0);
        System.out.println("g1 : "+in1);

        CurveElement in2=G2.newElement();
        Point in2x=(Point) in2.getX().getField().newElement();
        Point in2y=(Point) in2.getX().getField().newElement();

        in2x.getX().set(xp21);
        in2x.getY().set(xp22);
        in2y.getX().set(yp21);
        in2y.getY().set(yp22);

        in2.getX().set(in2x);
        in2.getY().set(in2y);
        in2.setInfFlag(0);


        Element ppubs=in2.duplicate().mul(ks);

        GTFiniteElement e=(GTFiniteElement) pairing.pairing(in1,ppubs);

        System.out.println("e:"+e);
        System.out.println("e bytes:"+e.toBytes());
        System.out.println("e^r:"+e.pow(r));
        System.out.println("e bytes:"+new BigInteger(e.toBytes()).toString(16));

        BigInteger a=new BigInteger("12345");
        Element g1a=in1.duplicate().mul(a);
        Element ea=pairing.pairing(g1a,ppubs);

        System.out.println( "e^a:"+e.pow(a));
        System.out.println("pair ea"+ea);



    }

    @Test
    public void testField(){

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
            System.out.println("NG : " + msg + ", lhs = " + lhs + ", rhs = " + rhs);
        }
    }

    public static void out(String x){
        System.out.println(x);
    }
}
