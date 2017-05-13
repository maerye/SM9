package org.bouncycastle.math.myec.bncurves;

/**
 * Created by mzy on 2017/5/13.
 */
public class SM9Field12 {
    public SM9Field4 a0,a1,a2;

    public SM9Field12(SM9Field4 a0,SM9Field4 a1,SM9Field4 a2){
        this.a0=a0;
        this.a1=a1;
        this.a2=a2;

        reduce();
    }

    public SM9Field12 multiply(SM9Field12 x){
        SM9Field4 v0,v1,v2,
                a0a1,a0a2,
                a1a2,b0b1,
                b0b2,b1b2,
                c0,c1,c2;

        v0=a0.multiply(x.a0);
        v1=a1.multiply(x.a1);
        v2=a2.multiply(x.a2);
        a0a1=a0.add(a1);
        a0a2=a0.add(a2);
        a1a2=a1.add(a2);
        b0b1=x.a0.add(x.a1);
        b0b2=x.a0.add(x.a2);
        b1b2=x.a1.add(x.a2);
        c0=v0.add((a1a2.multiply(b1b2).sub(v1).sub(v2)).multiplyv());
        c1=a0a1.multiply(b0b1).sub(v0).sub(v1).add(v2.multiplyv());
        c2=a0a2.multiply(b0b2).sub(v0).add(v1).sub(v2);

        return new SM9Field12(c0,c1,c2);

    }

    public void reduce(){
        a0.reduce();
        a1.reduce();
        a2.reduce();
    }
}
