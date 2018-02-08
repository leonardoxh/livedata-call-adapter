package com.github.leonardoxh.livedatacalladapter;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

final class LiveDataTestUtil {

    static <T> T getLiveDataValue(final LiveData<T> liveData) throws InterruptedException {
        return getLiveDataValue(liveData, 1);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    static <T> T getLiveDataValue(final LiveData<T> liveData, final long timeOutInSeconds)
            throws InterruptedException {
        final Object[] resultHolder = new Object[1];
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        liveData.observeForever(new Observer<T>() {
            @Override
            public void onChanged(@Nullable T t) {
                resultHolder[0] = t;
                countDownLatch.countDown();
                liveData.removeObserver(this);
            }
        });
        countDownLatch.await(timeOutInSeconds, TimeUnit.SECONDS);
        if (resultHolder.length == 0) {
            throw new AssertionError("Unable to retrieve LiveData result");
        }
        return (T) resultHolder[0];
    }

}
