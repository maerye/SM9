package src;

import mcl.bn254.Ec1;

/**
 * Created by mzy on 2017/4/17.
 */
public class Sm9SignPrivateKey {


    private Ec1 ds;

    public Sm9SignPrivateKey(Ec1 point)
    {
        this.ds=point;
    }
    public Ec1 getDs() {
        return ds;
    }
}
