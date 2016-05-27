package com.petukhovsky.jvaluer.local;

import java.util.Optional;

/**
 * Created by Arthur Petukhovsky on 5/21/2016.
 */
public class OSRelatedValue<T> {

    private Optional<T> value;

    public OSRelatedValue() {
        value = Optional.empty();
    }

    public OSRelatedValue<T> windows(T value) {
        if (OS.isWindows()) this.value = Optional.of(value);
        return this;
    }

    public OSRelatedValue<T> windows32(T value) {
        if (OS.isWindows() && !OS.is64Bit()) this.value = Optional.of(value);
        return this;
    }

    public OSRelatedValue<T> windows64(T value) {
        if (OS.isWindows() && OS.is64Bit()) this.value = Optional.of(value);
        return this;
    }

    public OSRelatedValue<T> unix(T value) {
        if (OS.isUnix()) this.value = Optional.of(value);
        return this;
    }

    public OSRelatedValue<T> osx(T value) {
        if (OS.isOSX()) this.value = Optional.of(value);
        return this;
    }

    public T get() {
        return value.get();
    }

    public T orElse(T other) {
        return value.orElse(other);
    }

    public Optional<T> value() {
        return value;
    }
}
