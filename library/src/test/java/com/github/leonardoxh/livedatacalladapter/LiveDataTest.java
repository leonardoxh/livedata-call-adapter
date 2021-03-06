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

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.HttpException;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;

import static com.google.common.truth.Truth.assertThat;
import static okhttp3.mockwebserver.SocketPolicy.DISCONNECT_AFTER_REQUEST;

public class LiveDataTest {
    @Rule public final InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    @Rule public final MockWebServer server = new MockWebServer();
    private Service service;

    @Before
    public void setUp() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(server.url("/"))
                .addConverterFactory(LiveDataResponseBodyConverterFactory.create())
                .addCallAdapterFactory(LiveDataCallAdapterFactory.create())
                .addConverterFactory(new StringConverterFactory())
                .build();
        service = retrofit.create(Service.class);
    }

    @Test
    public void bodySuccess200() throws Exception {
        server.enqueue(new MockResponse().setBody("Hello"));

        Resource<String> stringResource = LiveDataTestUtil.getLiveDataValue(service.body());
        assertThat(stringResource.isSuccess()).isTrue();
        assertThat(stringResource.getResource()).isEqualTo("Hello");
    }

    @Test
    public void bodySuccess404() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(404));

        Resource<String> stringResource = LiveDataTestUtil.getLiveDataValue(service.body());
        assertThat(stringResource.isSuccess()).isFalse();
        assertThat(stringResource.getError()).isInstanceOf(HttpException.class);
    }

    @Test
    public void bodyFailure() throws Exception {
        server.enqueue(new MockResponse().setSocketPolicy(DISCONNECT_AFTER_REQUEST));

        Resource<String> stringResource = LiveDataTestUtil.getLiveDataValue(service.body());
        assertThat(stringResource.isSuccess()).isFalse();
        assertThat(stringResource.getError()).isInstanceOf(IOException.class);
    }

    @Test
    public void responseSuccess200() throws Exception {
        server.enqueue(new MockResponse().setBody("Hello"));

        Response<Resource<String>> responseResource = LiveDataTestUtil.getLiveDataValue(service.response());
        assertThat(responseResource.isSuccessful()).isTrue();
        Resource<String> resource = responseResource.body();
        assertThat(resource).isNotNull();
        assertThat(resource.isSuccess()).isTrue();
        assertThat(resource.getResource()).isEqualTo("Hello");
    }

    @Test
    public void responseSuccess404() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(404));

        Response<Resource<String>> responseResource = LiveDataTestUtil.getLiveDataValue(service.response());
        assertThat(responseResource.isSuccessful()).isTrue();
        Resource<String> resource = responseResource.body();
        assertThat(resource).isNotNull();
        assertThat(resource.isSuccess()).isFalse();
        assertThat(resource.getError()).isNull();
    }

    @Test
    public void responseFailure() throws Exception {
        server.enqueue(new MockResponse().setSocketPolicy(DISCONNECT_AFTER_REQUEST));

        Response<Resource<String>> responseResource = LiveDataTestUtil.getLiveDataValue(service.response());
        assertThat(responseResource.isSuccessful()).isTrue();
        Resource<String> resource = responseResource.body();
        assertThat(resource).isNotNull();
        assertThat(resource.isSuccess()).isFalse();
        assertThat(resource.getError()).isInstanceOf(IOException.class);
    }

    interface Service {
        @GET("/") LiveData<Resource<String>> body();
        @GET("/") LiveData<Response<Resource<String>>> response();
    }
}
