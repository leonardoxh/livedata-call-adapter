/*
 * Copyright 2018 Leonardo Rossetto
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.leonardoxh.livedatacalladapter;

import android.support.annotation.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;
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
            if (parameterizedType.getRawType() == Response.class) {
                checkParametrizedType(parameterizedType);
                Type subType = parameterizedType.getActualTypeArguments()[0];
                if (subType instanceof ParameterizedType) {
                    parameterizedType = (ParameterizedType) parameterizedType.getActualTypeArguments()[0];
                }
            }

            if (parameterizedType.getRawType() == Resource.class) {
                checkParametrizedType(parameterizedType);
                Type realType = parameterizedType.getActualTypeArguments()[0];
                return originalConverterFactory.responseBodyConverter(realType, annotations, retrofit);
            }
        }

        return originalConverterFactory.responseBodyConverter(type, annotations, retrofit);
    }

    private static void checkParametrizedType(ParameterizedType parameterizedType) {
        if (parameterizedType.getActualTypeArguments().length == 0) {
            throw new IllegalStateException("Resource should have a type Resource<T> or " +
                    "Resource<? extends T>");
        }
    }
}
