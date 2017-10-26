package src;

import src.api.Element;
import src.field.curve.CurveElement;


/**
 * Created by mzy on 2017/4/17.
 */
public class Sm9SignPrivateKey {


    private CurveElement ds;

    public Sm9SignPrivateKey(CurveElement point)
    {
        this.ds=point;
    }
    public CurveElement getDs() {
        return ds;
    }
}
