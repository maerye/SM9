package src;

import src.field.curve.CurveElement;

/**
 * Created by mzy on 2017/4/24.
 */
public class Sm9EncryptPrivateKey {
    private CurveElement de;
    public Sm9EncryptPrivateKey(CurveElement de){
        this.de=de;
    }
    public CurveElement getDe(){
        return de;
    }
}
