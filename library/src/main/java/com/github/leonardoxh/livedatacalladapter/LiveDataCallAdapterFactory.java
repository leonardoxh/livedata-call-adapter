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

import android.arch.lifecycle.LiveData;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.annotation.Nullable;

import retrofit2.CallAdapter;
import retrofit2.Response;
import retrofit2.Retrofit;

public final class LiveDataCallAdapterFactory extends CallAdapter.Factory {
    private LiveDataCallAdapterFactory() {
    }

    public static LiveDataCallAdapterFactory create() {
        return new LiveDataCallAdapterFactory();
    }

    @Nullable
    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        if (getRawType(returnType) != LiveData.class) {
            return null;
        }
        if (!(returnType instanceof ParameterizedType)) {
            throw new IllegalStateException("Response must be parametrized as " +
                    "LiveData<Resource> or LiveData<? extends Resource>");
        }

        Type responseType = getParameterUpperBound(0, (ParameterizedType) returnType);
        if (getRawType(responseType) == Response.class) {
            if (!(responseType instanceof ParameterizedType)) {
                throw new IllegalStateException("Response must be parametrized as " +
                        "LiveData<Response<Resource>> or LiveData<Response<? extends Resource>>");
            }

            return new LiveDataResponseCallAdapter<>(responseType);
        }
        return new LiveDataBodyCallAdapter<>(getParameterUpperBound(0, (ParameterizedType) returnType));
    }
}
