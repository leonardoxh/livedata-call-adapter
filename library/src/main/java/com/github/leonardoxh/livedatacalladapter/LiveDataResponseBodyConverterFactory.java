package com.github.leonardoxh.livedatacalladapter;

import android.support.annotation.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Bypass the default retrofit body converter behaviour
 * as we are using the {@link Resource} as an response wrapper we should
 * tell to the original converter the correct type
 */
public class LiveDataResponseBodyConverterFactory extends Converter.Factory {
    private final Converter.Factory originalConverterFactory;

    private LiveDataResponseBodyConverterFactory(Converter.Factory originalConverterFactory) {
        this.originalConverterFactory = originalConverterFactory;
    }

    public static LiveDataResponseBodyConverterFactory wrap(Converter.Factory factory) {
        return new LiveDataResponseBodyConverterFactory(factory);
    }

    @Nullable
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                            Retrofit retrofit) {
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            if (parameterizedType.getRawType() == Resource.class) {
                if (parameterizedType.getActualTypeArguments().length == 0) {
                    throw new IllegalStateException("Resource should have a type Resource<T> or " +
                            "Resource<? extends T>");
                }
                Type realType = parameterizedType.getActualTypeArguments()[0];
                return originalConverterFactory.responseBodyConverter(realType, annotations, retrofit);
            }
        }

        return originalConverterFactory.responseBodyConverter(type, annotations, retrofit);
    }
}
