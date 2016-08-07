package com.xda.one.api.misc;

public interface Consumer<T> {

    void run(T data);
}