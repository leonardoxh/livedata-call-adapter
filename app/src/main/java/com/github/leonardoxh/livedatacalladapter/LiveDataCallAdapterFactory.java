package com.github.leonardoxh.livedatacalladapter;

import android.arch.lifecycle.LiveData;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import retrofit2.CallAdapter;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LiveDataCallAdapterFactory extends CallAdapter.Factory {
    private LiveDataCallAdapterFactory() {
    }

    public static LiveDataCallAdapterFactory create() {
        return new LiveDataCallAdapterFactory();
    }

    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        if (getRawType(returnType) != LiveData.class) {
            return null;
        }
        if (!(returnType instanceof ParameterizedType)) {
            throw new IllegalStateException("Response must be parametrized as " +
                    "Response<LiveData> or Response<? extends LiveData>");
        }

        Type responseType = getParameterUpperBound(0, (ParameterizedType) returnType);
        if (getRawType(responseType) == Response.class) {
            if (!(responseType instanceof ParameterizedType)) {
                throw new IllegalStateException("Response must be parametrized as " +
                        "Response<LiveData> or Response<? extends LiveData>");
            }

            return new LiveDataResponseCallAdapter<>(responseType);
        }
        return new LiveDataBodyCallAdapter<>(getParameterUpperBound(0, (ParameterizedType) returnType));
    }
}
