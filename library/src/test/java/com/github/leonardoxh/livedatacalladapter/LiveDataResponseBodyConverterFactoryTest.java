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

import com.google.common.reflect.TypeToken;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.google.common.truth.Truth.assertThat;

public class LiveDataResponseBodyConverterFactoryTest {
    private static final Annotation[] NO_ANNOTATIONS = new Annotation[0];

    @Rule public final MockWebServer server = new MockWebServer();

    private final Converter.Factory factory =
            LiveDataResponseBodyConverterFactory.wrap(new StringConverterFactory());
    private Retrofit retrofit;

    @Before
    public void setUp() {
        retrofit = new Retrofit.Builder()
                .baseUrl(server.url("/"))
                .addConverterFactory(factory)
                .addCallAdapterFactory(LiveDataCallAdapterFactory.create())
                .build();
    }

    @Test
    public void responseTypeReturnsStringConverterFactory() {
        Type responseType = new TypeToken<Response<Resource<String>>>() {}.getType();
        Converter<ResponseBody, ?> converter = factory
                .responseBodyConverter(responseType, NO_ANNOTATIONS, retrofit);

        assertThat(converter).isInstanceOf(StringConverterFactory.StringResponseBodyConverter.class);
    }

    @Test
    public void bodyTypeReturnsStringConverterFactory() {
        Type responseType = new TypeToken<Resource<String>>() {}.getType();
        Converter<ResponseBody, ?> converter = factory
                .responseBodyConverter(responseType, NO_ANNOTATIONS, retrofit);

        assertThat(converter).isInstanceOf(StringConverterFactory.StringResponseBodyConverter.class);
    }

    @Test
    public void anyOtherTypeReturnsStringConverterFactory() {
        Type responseType = new TypeToken<String>() {}.getType();
        Converter<ResponseBody, ?> converter = factory
                .responseBodyConverter(responseType, NO_ANNOTATIONS, retrofit);

        assertThat(converter).isInstanceOf(StringConverterFactory.StringResponseBodyConverter.class);
    }
}
