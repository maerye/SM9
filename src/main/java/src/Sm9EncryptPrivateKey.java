package src;

import mcl.bn254.Ec2;

/**
 * Created by mzy on 2017/4/24.
 */
public class Sm9EncryptPrivateKey {
    private Ec2 de;
    public Sm9EncryptPrivateKey(Ec2 de){
        this.de=de;
    }
    public Ec2 getDe(){
        return de;
    }
}
