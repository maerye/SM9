package src;


import src.api.Element;
import src.field.curve.CurveElement;

import java.math.BigInteger;


/**
 * Created by mzy on 2017/4/19.
 */
public class Signature {
    public BigInteger h;
    CurveElement s;

    public Signature (BigInteger h, CurveElement s)
    {
        this.h=h;
        this.s=s;
    }
}
