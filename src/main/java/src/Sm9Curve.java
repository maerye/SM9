package src;

import org.bouncycastle.math.myec.bncurves.BNCurve;
import org.bouncycastle.math.myec.bncurves.BNParams;
import org.bouncycastle.math.myec.bncurves.BNPoint;

import java.math.BigInteger;

/**
 * Created by mzy on 2017/4/18.
 */
public class Sm9Curve {

    private static int fieldbits=256;
    public BNParams bnParams;
    private BNCurve bnCurve ;
    private static Sm9Curve THIS;

    public Sm9Curve(){
        bnParams=new BNParams(fieldbits);
        bnCurve=new BNCurve(bnParams);
    }

    public static Sm9Curve getInstance(){
        if(THIS==null)
        {
            THIS=new Sm9Curve();
        }
            return THIS;
    }

    public BigInteger getN(){ return bnParams.getN();}
    public BNPoint getG(){return bnCurve.getG();}
    public BNCurve getBnCurve(){ return bnCurve;}


}
