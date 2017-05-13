package org.bouncycastle.math.myec.bncurves;


import java.math.BigInteger;

/**
 * Created by mzy on 2017/5/12.
 */
public class SM9Field2 {

    public BigInteger a0,a1;
    private BigInteger bt=new BigInteger("-2");
    private BigInteger p=new BigInteger("B640000002A3A6F1D603AB4FF58EC74521F2934B1A7AEEDBE56F9B27E351457D",16);

    public SM9Field2(BigInteger a0,BigInteger a1){
        this.a0=a0;
        this.a1=a1;
        reduce();
    }
    public SM9Field2 multiply(SM9Field2 x){
        BigInteger v0=a0.multiply(x.a0);
        BigInteger v1=a1.multiply(x.a1);
        BigInteger mix1=a0.add(a1);
        BigInteger mix2=x.a0.add(x.a1);

        return new SM9Field2(v0.add(v1.multiply(bt)),mix1.multiply(mix2).subtract(v0).subtract(v1));
    }

    public SM9Field2 multiplyr(){
        return new SM9Field2(a1.multiply(bt),a0);
    }
    public SM9Field2 add(SM9Field2 x){
        return new SM9Field2(a0.add(x.a0),a1.add(x.a1));
    }
    public SM9Field2 sub(SM9Field2 x){
        return this.add(x.negate());
    }
    public SM9Field2 negate(){
        BigInteger m=a0.add(a1);
        BigInteger t=m.negate().mod(p);

        return new SM9Field2(t.add(a1),t.add(a0));
    }
    public SM9Field2 square(){
        BigInteger v0=a0.pow(2);
        BigInteger v1=a1.pow(2);
        BigInteger mix=(a0.add(a1)).pow(2);

        return new SM9Field2(v0.add(v1.multiply(bt)),mix.subtract(v0).subtract(v1));
    }
    public void reduce(){
        a0=a0.mod(p);
        a1=a1.mod(p);
    }

    public boolean isZero(){
        return a0.equals(BigInteger.ZERO)&&a1.equals(BigInteger.ZERO);
    }
    @Override
    public String toString(){
        return "("+a0.toString()+","+a1.toString()+")";
    }

    public boolean equals(SM9Field2 x){
        return this.a0.equals(x.a0)&&this.a1.equals(x.a1);
    }
}
