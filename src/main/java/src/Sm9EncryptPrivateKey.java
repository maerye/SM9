package src;

import mcl.bn254.Ec2;
import org.bouncycastle.math.myec.bncurves.BNPoint2;

/**
 * Created by mzy on 2017/4/24.
 */
public class Sm9EncryptPrivateKey {
    private BNPoint2 de;
    public Sm9EncryptPrivateKey(BNPoint2 de){
        this.de=de;
    }
    public BNPoint2 getDe(){
        return de;
    }
}
