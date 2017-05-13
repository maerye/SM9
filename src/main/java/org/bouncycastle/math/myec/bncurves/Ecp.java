package org.bouncycastle.math.myec.bncurves;

import java.math.BigInteger;
/**
 * Created by mzy on 2017/5/13.
 */
public class Ecp {

    public BigInteger x,y,z;
    private BigInteger _0=BigInteger.ZERO;
    private BigInteger _1=BigInteger.ONE;
    private BigInteger p=new BigInteger("B640000002A3A6F1D603AB4FF58EC74521F2934B1A7AEEDBE56F9B27E351457D",16);


    private BigInteger rhs(BigInteger x){
        return x.pow(3).add(new BigInteger("5")).mod(p);
    }
    public Ecp(){
        x=_1;
        y=_1;
        z=_0;
    }


    public Ecp(BigInteger x,BigInteger y,BigInteger z){

    }
    public Ecp normalize (){
        BigInteger zi=z.modInverse(p);
        BigInteger zi2=zi.pow(2);

    }
    public Ecp add(Ecp){

    }
    public void reduce(){
        x=x.mod(p);
        y=y.mod(p);
        z=z.mod(p);
    }
}
