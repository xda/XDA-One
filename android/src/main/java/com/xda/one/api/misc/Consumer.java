package com.xda.one.api.misc;

public interface Consumer<T> {

    public void run(T data);
}