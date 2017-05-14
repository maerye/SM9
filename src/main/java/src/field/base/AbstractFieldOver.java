package src.field.base;


import src.api.Element;
import src.api.Field;
import src.api.FieldOver;

import java.security.SecureRandom;

/**
 * @author Angelo De Caro (jpbclib@gmail.com)
 */
public abstract class AbstractFieldOver<F extends Field, E extends Element> extends AbstractField<E> implements FieldOver<F, E> {
    protected F targetField;


    protected AbstractFieldOver(SecureRandom random, F targetField) {
        super(random);
        this.targetField = targetField;
    }


    public F getTargetField() {
        return targetField;
    }

}
