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

import com.google.common.reflect.TypeToken;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.mockwebserver.MockWebServer;
import retrofit2.CallAdapter;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;

public class LiveDataCallAdapterFactoryTest {
    private static final Annotation[] NO_ANNOTATIONS = new Annotation[0];

    @Rule public final MockWebServer server = new MockWebServer();
    private final CallAdapter.Factory factory = LiveDataCallAdapterFactory.create();
    private Retrofit retrofit;

    @Before
    public void setUp() {
        retrofit = new Retrofit.Builder()
                .baseUrl(server.url("/"))
                .addConverterFactory(LiveDataResponseBodyConverterFactory.wrap(new StringConverterFactory()))
                .addCallAdapterFactory(factory)
                .build();
    }

    @Test
    public void responseType() {
        Type bodyClass = new TypeToken<LiveData<Resource<String>>>() {}.getType();
        assertThat(factory.get(bodyClass, NO_ANNOTATIONS, retrofit).responseType())
                .isEqualTo(new TypeToken<Resource<String>>() {}.getType());

        Type bodyWildcard = new TypeToken<LiveData<Resource<? extends String>>>() {}.getType();
        assertThat(factory.get(bodyWildcard, NO_ANNOTATIONS, retrofit).responseType())
                .isEqualTo(new TypeToken<Resource<? extends String>>() {}.getType());

        Type responseType = new TypeToken<LiveData<Response<Resource<String>>>>() {}.getType();
        assertThat(factory.get(responseType, NO_ANNOTATIONS, retrofit).responseType())
                .isEqualTo(new TypeToken<Response<Resource<String>>>() {}.getType());

        Type responseTypeWildcard = new TypeToken<LiveData<Response<Resource<? extends String>>>>() {}.getType();
        assertThat(factory.get(responseTypeWildcard, NO_ANNOTATIONS, retrofit).responseType())
                .isEqualTo(new TypeToken<Response<Resource<? extends String>>>() {}.getType());
    }

    @Test
    public void nonListenableFutureReturnsNull() {
        CallAdapter<?, ?> adapter = factory.get(String.class, NO_ANNOTATIONS, retrofit);
        assertThat(adapter).isNull();
    }

    @Test
    public void rawTypesThrows() {
        Type liveDataType = new TypeToken<LiveData>() {}.getType();
        try {
            CallAdapter callAdapter = factory.get(liveDataType, NO_ANNOTATIONS, retrofit);
            fail("Unespected callAdapter = " + callAdapter.getClass().getName());
        } catch (IllegalStateException e) {
            assertThat(e).hasMessageThat().isEqualTo("Response must be parametrized as " +
                    "LiveData<Resource> or LiveData<? extends Resource>");
        }
    }

    @Test
    public void rawResponseTypesThrows() {
        Type liveDataType = new TypeToken<LiveData<Response>>() {}.getType();
        try {
            CallAdapter callAdapter = factory.get(liveDataType, NO_ANNOTATIONS, retrofit);
            fail("Unespected callAdapter = " + callAdapter.getClass().getName());
        } catch (IllegalStateException e) {
            assertThat(e).hasMessageThat().isEqualTo("Response must be parametrized as " +
                    "LiveData<Response<Resource>> or LiveData<Response<? extends Resource>>");
        }
    }
}