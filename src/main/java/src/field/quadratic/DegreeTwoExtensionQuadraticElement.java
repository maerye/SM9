package src.field.quadratic;


import src.api.Element;

/**
 * @author Angelo De Caro (jpbclib@gmail.com)
 */
public class DegreeTwoExtensionQuadraticElement<E extends Element> extends QuadraticElement<E> {


    public DegreeTwoExtensionQuadraticElement(DegreeTwoExtensionQuadraticField field) {
        super(field);

        this.x = (E) field.getTargetField().newElement();
        this.y = (E) field.getTargetField().newElement();
    }

    public DegreeTwoExtensionQuadraticElement(DegreeTwoExtensionQuadraticElement element) {
        super((QuadraticField) element.field);

        this.x = (E) element.x.duplicate();
        this.y = (E) element.y.duplicate();
    }


    public DegreeTwoExtensionQuadraticElement duplicate() {
        return new DegreeTwoExtensionQuadraticElement(this);
    }

    public DegreeTwoExtensionQuadraticElement square() {
        Element e0 = x.duplicate();
        Element e1 = x.duplicate();

        e0.add(y).mul(e1.sub(y));
        e1.set(x).mul(y).twice()/*add(e1)*/;

        x.set(e0);
        y.set(e1);

        return this;
    }

    public DegreeTwoExtensionQuadraticElement invert() {
        Element e0 = x.duplicate();
        Element e1 = y.duplicate();

        e0.square().add(e1.square()).invert();

        x.mul(e0);
        y.mul(e0.negate());

        return this;
    }

    public DegreeTwoExtensionQuadraticElement mul(Element e) {
        DegreeTwoExtensionQuadraticElement element = (DegreeTwoExtensionQuadraticElement) e;

        Element e0 = x.duplicate();
        Element e1 = element.x.duplicate();
        Element e2 = x.getField().newElement();

        // e2 = (x+y) * (x1+y1)
        e2.set(e0.add(y)).mul(e1.add(element.y));

        // e0 = x*x1
        e0.set(x).mul(element.x);
        // e1 = y*y1
        e1.set(y).mul(element.y);
        // e2 = (x+y)*(x1+y1) - x*x1
        e2.sub(e0);

        // x = x*x1 - y*y1
        x.set(e0).sub(e1);
        // y = (x+y)*(x1+y1)- x*x1 - y*y1
        y.set(e2).sub(e1);

        return this;
    }

    public boolean isSqr() {
        /*
        //x + yi is a square <=> x^2 + y^2 is (in the base field)

        // Proof: (=>) if x+yi = (a+bi)^2,
        // then a^2 - b^2 = x, 2ab = y,
        // thus (a^2 + b^2)^2 = (a^2 - b^2)^2 + (2ab)^2 =  x^2 + y^2
        // (<=) Suppose A^2 = x^2 + y^2
        // then if there exist a, b satisfying:
        //   a^2 = (+-A + x)/2, b^2 = (+-A - x)/2
        // then (a + bi)^2 = x + yi.
        // We show that exactly one of (A + x)/2, (-A + x)/2
        // is a quadratic residue (thus a, b do exist).
        // Suppose not. Then the product
        // (x^2 - A^2) / 4 is some quadratic residue, a contradiction
        // since this would imply x^2 - A^2 = -y^2 is also a quadratic residue,
        // but we know -1 is not a quadratic residue.
        */
        return x.duplicate().square().add(y.duplicate().square()).isSqr();
    }

    public DegreeTwoExtensionQuadraticElement sqrt() {
        //if (a+bi)^2 = x+yi then
        //2a^2 = x +- sqrt(x^2 + y^2)
        //(take the sign such that a exists) and 2ab = y
        //[thus 2b^2 = - (x -+ sqrt(x^2 + y^2))]
        Element e0 = x.duplicate().square();
        Element e1 = y.duplicate().square();
        e0.add(e1).sqrt();

        //e0 = sqrt(x^2 + y^2)
        e1.set(x).add(e0);
        Element e2 = x.getField().newElement().set(2).invert();
        e1.mul(e2);

        //e1 = (x + sqrt(x^2 + y^2))/2

        if (e1.isSqr()) {
            e1.sub(e0);
            //e1 should be a square
        }
        e0.set(e1).sqrt();
        e1.set(e0).add(e0);
        e1.invert();
        y.mul(e1);
        x.set(e0);

        return this;
    }

    public boolean isEqual(Element e) {
        if (e == this)
            return true;

        DegreeTwoExtensionQuadraticElement element = (DegreeTwoExtensionQuadraticElement) e;

        return x.isEqual(element.x) && y.isEqual(element.y);
    }

    public String toString() {
        return String.format("{x=%s,y=%s}", x, y);
    }

    @Override
    public Element getImmutable() {
        return new ImmutableDegreeTwoExtensionQuadraticElement(this);
    }


}
