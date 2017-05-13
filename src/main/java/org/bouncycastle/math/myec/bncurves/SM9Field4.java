package org.bouncycastle.math.myec.bncurves;

import apach.bn.BIG;

import java.math.BigInteger;

/**
 * Created by mzy on 2017/5/12.
 */
public class SM9Field4 {
    public SM9Field2 a0,a1;
    BigInteger _0=BigInteger.ZERO;
    BigInteger _1=BigInteger.ONE;

    public SM9Field4 (SM9Field2 a0,SM9Field2 a1)
    {
        this.a0=a0;
        this.a1=a1;
        reduce();
    }

    public SM9Field4 multiply(SM9Field4 x){
        SM9Field2 v0=a0.multiply(x.a0);
        SM9Field2 v1=a1.multiply(x.a1);

        SM9Field2 c0=v0.add(v1.multiplyr());
        SM9Field2 mix1=a0.add(a1);
        SM9Field2 mix2=x.a0.add(x.a1);

        return new SM9Field4(c0,mix1.multiply(mix2).sub(v0).sub(v1));
    }
    public SM9Field4 multiplyv(){
         SM9Field2 v0=new SM9Field2(_0,_0);
         SM9Field2 v1=new SM9Field2(_1,_0);
         SM9Field4 v=new SM9Field4(v0,v1);
        return this.multiply(v);
    }
    public SM9Field4 square(){
        SM9Field2 v0=a0.square();
        SM9Field2 v1=a1.square();
        SM9Field2 c0=v0.add(v1.multiplyr());
        SM9Field2 mix=a0.add(a1);
        return new SM9Field4(c0,mix.square().sub(v0).sub(v1));

    }
    public SM9Field4 sub(SM9Field4 x){

        return this.add(x.negate());
    }
    public SM9Field4 negate(){
        SM9Field2 m=a0.add(a1);
        SM9Field2 t=m.negate();

        return new SM9Field4(t.add(a1),t.add(a0));
    }
    public SM9Field4 add(SM9Field4 x){
        return new SM9Field4(a0.add(x.a0),a1.add(x.a1));
    }


    public void reduce()
    {
        a0.reduce();
        a1.reduce();
    }

    public boolean equals(SM9Field4 x){
        return a0.equals(x.a0)&&a1.equals(x.a1);
    }

}
