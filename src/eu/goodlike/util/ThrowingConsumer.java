package eu.goodlike.util;

public interface ThrowingConsumer<T> {

    void accept(T t) throws Throwable;

}
