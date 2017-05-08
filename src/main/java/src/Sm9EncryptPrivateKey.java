package src;

import iaik.security.ec.math.curve.ECPoint;
import mcl.bn254.Ec2;

/**
 * Created by mzy on 2017/4/24.
 */
public class Sm9EncryptPrivateKey {
    private ECPoint de;
    public Sm9EncryptPrivateKey(ECPoint de){
        this.de=de;
    }
    public ECPoint getDe(){
        return de;
    }
}
