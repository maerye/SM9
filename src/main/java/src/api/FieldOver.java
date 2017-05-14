package src.api;

/**
 * This interface represents an algebraic structure defined
 * over another.
 *
 * @author Angelo De Caro (jpbclib@gmail.com)
 * @since 1.0.0
 * @see Field
 */
public interface FieldOver<F extends Field, E extends Element> extends Field<E> {

    /**
     * Returns the target field.
     *
     * @return the target field.
     * @since 1.0.0
     */
    F getTargetField();

}
