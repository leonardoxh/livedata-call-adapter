LiveData Call Adapter for Retrofit
---
A Retrofit 2 `CallAdapter.Factory` for Android [LiveData](https://developer.android.com/topic/libraries/architecture/livedata.html)

Usage
---
Add `LiveDataCallAdapterFactory` as a `Call` adapter when building your `Retrofit` instance:

```java
Retrofit retrofit = new Retrofit.Builder()
        .baseUrl("https://example.com")
        .addCallAdapterFactory(LiveDataCallAdapterFactory.create())
        .build();
```

Your service methods can now use `LiveData` as their return type.

```java
public interface SuperService {
    @GET("/pimba") LiveData<Resource<Pimba>> getPimba();
    @GET("/pimba") LiveData<Response<Resource<Pimba>>> getPimbas();
}
```

Please note the usage of the `Resource` object, it is required to provide the 
error callback to the `LiveData` object, so when you want verify what is happening 
on your network call for example:

```java
retrofit.create(SuperService.class)
        .getPimba()
        .observe(this, new Observer<Resource<Pimba>>() {
            @Override
            public void onChange(@Nullable Resource<Pimba> resource) {
                if (resource.isSuccess()) {
                    //doSuccessAction with resource.resource
                } else {
                    //doErrorAction with resource.error
                }
            }
        })
```

Gradle dependencie
---
```groovy
dependencies {
    implementation "com.github.leonardoxh:retrofit2-livedata-adapter:1.0.0"
}
```

Inspiration
---
* [Kotlin courtines adapter](https://github.com/JakeWharton/retrofit2-kotlin-coroutines-adapter)
* [Retrofit RXJava2 adapter](https://github.com/square/retrofit)

License
---
```
Copyright 2018 Leonardo Rossetto

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```