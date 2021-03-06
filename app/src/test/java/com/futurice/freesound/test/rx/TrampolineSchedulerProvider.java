/*
 * Copyright 2017 Futurice GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.futurice.freesound.test.rx;

import com.futurice.freesound.feature.common.scheduling.SchedulerProvider;

import android.support.annotation.NonNull;

import io.reactivex.Scheduler;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * A SchedulerProvider used for testing which allows uses the trampoline Scheduler by default,
 * but allows overriding of the time Scheduler based upon supplied criteria.
 */
public class TrampolineSchedulerProvider implements SchedulerProvider {

    private Function<String, Scheduler> timeSchedulerHandler = null;

    @NonNull
    @Override
    public Scheduler computation() {
        return Schedulers.trampoline();
    }

    @NonNull
    @Override
    public Scheduler trampoline() {
        return Schedulers.trampoline();
    }

    @NonNull
    @Override
    public Scheduler single() {
        return Schedulers.trampoline();
    }

    @NonNull
    @Override
    public Scheduler newThread() {
        return Schedulers.trampoline();
    }

    @NonNull
    @Override
    public Scheduler io() {
        return Schedulers.trampoline();
    }

    @NonNull
    @Override
    public Scheduler time(@NonNull final String tag) {
        if (timeSchedulerHandler != null) {
            return applyOrThrow(timeSchedulerHandler, tag);
        }
        return Schedulers.trampoline();
    }

    @NonNull
    @Override
    public Scheduler ui() {
        return Schedulers.trampoline();
    }

    @Override
    public boolean isUiThread() {
        return false;
    }

    @Override
    public void assertUiThread() {
        if (!isUiThread()) {
            throw new IllegalStateException(
                "This task must be run on the Main thread and not on a worker thread.");
        }
    }

    /**
     * Override the time Scheduler with the Scheduler evaluated from the supplied function.
     *
     * @param schedulerFunction a function which returns the override or null to use the default.
     */
    public void setTimeScheduler(@NonNull Function<String, Scheduler> schedulerFunction) {
        this.timeSchedulerHandler = schedulerFunction;
    }

    /**
     * Resets the time Scheduler to use the default trampoline Scheduler.
     */
    public void reset() {
        this.timeSchedulerHandler = null;
    }

    private static <T, R> R applyOrThrow(@NonNull Function<T, R> function, T value) {
        try {
            return function.apply(value);
        } catch (Exception e) {
            throw new RuntimeException("Error evaluating Scheduler", e);
        }
    }
}
