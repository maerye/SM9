package src.api;

import java.util.List;

/**
 * This element represents a polynomial through its coefficients.
 *
 * @author Angelo De Caro (jpbclib@gmail.com)
 * @since 1.0.0
 */
public interface Polynomial<E extends Element> extends Element, Vector {

    /**
     * Returns the degree of this polynomial.
     *
     * @return the degree of this polynomial.
     * @since 1.0.0
     */
    int getDegree();

    /**
     * Returns the list of coefficients representing
     * this polynomial.
     *
     * @return the list of coefficients representing
     * this polynomial.
     * @since 1.0.0
     */
    List<E> getCoefficients();

    /**
     * Returns the coefficient at a specified position.
     *
     * @param index the position of the requested coefficient.
     * @return the coefficient at a specified position.
     * @since 1.0.0
     */
    E getCoefficient(int index);

}
