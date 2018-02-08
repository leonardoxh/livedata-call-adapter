package com.github.leonardoxh.livedatacalladapter;

import android.support.annotation.Nullable;

public final class Resource<T> {
    private T resource;
    private Throwable error;

    private Resource() {
    }

    public boolean isSuccess() {
        return resource != null && error == null;
    }

    @Nullable
    public T getResource() {
        return resource;
    }

    @Nullable
    public Throwable getError() {
        return error;
    }

    public static <T> Resource<T> success(T body) {
        final Resource<T> resource = new Resource<>();
        resource.resource = body;
        return resource;
    }

    public static <T> Resource error(Throwable error) {
        final Resource<T> resource = new Resource<>();
        resource.error = error;
        return resource;
    }
}
