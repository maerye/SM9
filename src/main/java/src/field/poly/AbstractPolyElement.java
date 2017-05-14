package src.field.poly;


import src.api.Element;
import src.api.Polynomial;
import src.field.base.AbstractElement;
import src.field.base.AbstractFieldOver;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Angelo De Caro (jpbclib@gmail.com)
 */
public abstract class AbstractPolyElement<E extends Element, F extends AbstractFieldOver>
        extends AbstractElement<F> implements Polynomial<E> {

    protected List<E> coefficients;


    protected AbstractPolyElement(F field) {
        super(field);

        this.coefficients = new ArrayList<E>();
    }


    public int getSize() {
        return coefficients.size();
    }

    public E getAt(int index) {
        return coefficients.get(index);
    }

    public List<E> getCoefficients() {
        return coefficients;
    }

    public E getCoefficient(int index) {
        return coefficients.get(index);
    }

    public int getDegree() {
        return coefficients.size();
    }

}