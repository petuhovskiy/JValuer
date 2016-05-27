package com.petukhovsky.jvaluer.local;

import java.util.function.Supplier;

/**
 * Created by Arthur Petukhovsky on 5/21/2016.
 */
public class OSRelatedSupplier<T> {

    private OSRelatedValue<Supplier<T>> value;

    public OSRelatedSupplier() {
        value = new OSRelatedValue<>();
    }

    public OSRelatedSupplier<T> windows(Supplier<T> supplier) {
        value.windows(supplier);
        return this;
    }

    public OSRelatedSupplier<T> windows32(Supplier<T> supplier) {
        value.windows32(supplier);
        return this;
    }

    public OSRelatedSupplier<T> windows64(Supplier<T> supplier) {
        value.windows64(supplier);
        return this;
    }

    public OSRelatedSupplier<T> unix(Supplier<T> supplier) {
        value.unix(supplier);
        return this;
    }

    public OSRelatedSupplier<T> osx(Supplier<T> supplier) {
        value.osx(supplier);
        return this;
    }

    public T get() {
        return value.get().get();
    }

    public T orElse(T other) {
        return value.value().isPresent() ? get() : other;
    }

    public T orElse(Supplier<T> other) {
        return value.orElse(other).get();
    }

    public T orNull() {
        return orElse(() -> null);
    }
}
