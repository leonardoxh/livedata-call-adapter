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
import android.arch.lifecycle.MutableLiveData;

import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Callback;
import retrofit2.Response;

public class LiveDataResponseCallAdapter<R> implements CallAdapter<R, LiveData<Resource<Response<R>>>> {
    private final Type responseType;

    LiveDataResponseCallAdapter(Type type) {
        this.responseType = type;
    }

    @Override
    public Type responseType() {
        return responseType;
    }

    @SuppressWarnings("unchecked")
    @Override
    public LiveData<Resource<Response<R>>> adapt(Call<R> call) {
        final MutableLiveData<Resource<Response<R>>> liveDataResponse = new MutableLiveData<>();
        call.enqueue(new LiveDataResponseCallCallback(liveDataResponse));
        return liveDataResponse;
    }

    private static class LiveDataResponseCallCallback<T> implements Callback<T> {
        private final MutableLiveData<Response<Resource<T>>> liveData;

        LiveDataResponseCallCallback(MutableLiveData<Response<Resource<T>>> liveData) {
            this.liveData = liveData;
        }

        @Override
        public void onResponse(Call<T> call, Response<T> response) {
            if (call.isCanceled()) return;
            liveData.postValue(Response.success(Resource.success(response.body())));
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onFailure(Call<T> call, Throwable t) {
            if (call.isCanceled()) return;
            liveData.postValue(Response.<Resource<T>>success(Resource.error(t)));
        }
    }
}
