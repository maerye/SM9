package src.field.gt;


import src.api.Element;

import java.math.BigInteger;

/**
 * @author Angelo De Caro (jpbclib@gmail.com)
 */
public class ImmutableGTFiniteElement extends GTFiniteElement {

    public ImmutableGTFiniteElement(GTFiniteElement gtFiniteElement) {
        super(gtFiniteElement);

        this.value = gtFiniteElement.value.getImmutable();
        this.immutable = true;
    }

    @Override
    public GTFiniteElement duplicate() {
        return super.duplicate();
    }

    @Override
    public GTFiniteElement getImmutable() {
        return this;
    }

    @Override
    public GTFiniteElement set(Element value) {
        throw new IllegalStateException("Invalid call on an immutable element");
    }

    @Override
    public GTFiniteElement set(int value) {
        throw new IllegalStateException("Invalid call on an immutable element");
    }

    @Override
    public GTFiniteElement set(BigInteger value) {
        throw new IllegalStateException("Invalid call on an immutable element");
    }

    @Override
    public GTFiniteElement twice() {
        return (GTFiniteElement) duplicate().twice().getImmutable();
    }

    @Override
    public GTFiniteElement mul(int z) {
        return (GTFiniteElement) duplicate().mul(z).getImmutable();
    }

    @Override
    public GTFiniteElement setToZero() {
        throw new IllegalStateException("Invalid call on an immutable element");
    }

    @Override
    public GTFiniteElement setToOne() {
        throw new IllegalStateException("Invalid call on an immutable element");
    }

    @Override
    public GTFiniteElement setToRandom() {
        throw new IllegalStateException("Invalid call on an immutable element");
    }

    @Override
    public GTFiniteElement setFromHash(byte[] source, int offset, int length) {
        throw new IllegalStateException("Invalid call on an immutable element");
    }

    @Override
    public int setFromBytes(byte[] source) {
        throw new IllegalStateException("Invalid call on an immutable element");
    }

    @Override
    public int setFromBytes(byte[] source, int offset) {
        throw new IllegalStateException("Invalid call on an immutable element");
    }

    @Override
    public GTFiniteElement square() {
        return (GTFiniteElement) duplicate().square().getImmutable();
    }

    @Override
    public GTFiniteElement invert() {
        return duplicate().invert().getImmutable();
    }

    @Override
    public GTFiniteElement halve() {
        return (GTFiniteElement) duplicate().halve().getImmutable();
    }

    @Override
    public GTFiniteElement negate() {
        return duplicate().negate().getImmutable();
    }

    @Override
    public GTFiniteElement add(Element element) {
        return duplicate().add(element).getImmutable();
    }

    @Override
    public GTFiniteElement sub(Element element) {
        return duplicate().sub(element).getImmutable();
    }

    @Override
    public GTFiniteElement div(Element element) {
        return duplicate().div(element).getImmutable();
    }

    @Override
    public GTFiniteElement mul(Element element) {
        return duplicate().mul(element).getImmutable();
    }

    @Override
    public GTFiniteElement mul(BigInteger n) {
        return duplicate().mul(n).getImmutable();
    }

    @Override
    public GTFiniteElement mulZn(Element z) {
        return (GTFiniteElement) duplicate().mulZn(z).getImmutable();
    }

    @Override
    public GTFiniteElement sqrt() {
        return (GTFiniteElement) duplicate().sqrt().getImmutable();
    }

    @Override
    public GTFiniteElement pow(BigInteger n) {
        return duplicate().pow(n).getImmutable();
    }

    @Override
    public GTFiniteElement powZn(Element n) {
        return duplicate().powZn(n).getImmutable();
    }

}