//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.xiaowu.wpywebframework.common.model;


import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Optionals<T> {
    private Optional<T> optional;

    public Optionals(Optional<T> optional) {
        this.optional = optional;
    }

    public static <T> Optionals<T> of(Optional<T> optional) {
        return new Optionals<T>(optional);
    }

    public static <T> Optionals<T> of(T value) {
        return of(Optional.of(value));
    }

    public static <T> Optionals<T> ofNullable(T value) {
        return of(Optional.ofNullable(value));
    }

    public static <T> Optionals<T> empty() {
        return of(Optional.empty());
    }

    public T get() {
        return (T)this.optional.get();
    }

    public boolean isPresent() {
        return this.optional.isPresent();
    }

    public boolean isEmpty() {
        return !this.optional.isPresent();
    }

    public void ifPresent(Consumer<? super T> action) {
        this.optional.ifPresent(action);
    }

    public void ifPresentOrElse(Consumer<? super T> action, Runnable empty) {
        if (this.optional.isPresent()) {
            action.accept(this.optional.get());
        } else {
            empty.run();
        }

    }

    public void ifEmpty(Runnable empty) {
        if (!this.optional.isPresent()) {
            empty.run();
        }

    }

    public Optionals<T> filter(Predicate<? super T> predicate) {
        return of(this.optional.filter(predicate));
    }

    public <U> Optionals<U> map(Function<? super T, ? extends U> mapper) {
        return of(this.optional.map(mapper));
    }

    public <U> Optionals<U> flatMap(Function<? super T, Optional<U>> mapper) {
        return of(this.optional.flatMap(mapper));
    }

    public T orElse(T other) {
        return (T)this.optional.orElse(other);
    }

    public T orElseGet(Supplier<? extends T> supplier) {
        return (T)this.optional.orElseGet(supplier);
    }

    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        return (T)this.optional.orElseThrow(exceptionSupplier);
    }
}
