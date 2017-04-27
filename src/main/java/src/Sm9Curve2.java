package src;

import org.bouncycastle.math.myec.bncurves.BNCurve2;
import org.bouncycastle.math.myec.bncurves.BNPoint2;

/**
 * Created by mzy on 2017/4/18.
 */
public class Sm9Curve2 {
    private BNCurve2 bnCurve2;

    public Sm9Curve2 (Sm9Curve sm9Curve)
    {
        bnCurve2=new BNCurve2(sm9Curve.getBnCurve());
    }
    public BNCurve2 getBNCurve2(){return bnCurve2;}
    public BNPoint2 getGt() { return bnCurve2.getGt();}
}
