/*
 * Copyright 2015 the original author or authors.
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

package com.jayway.awaitility.pollinterval;

import com.jayway.awaitility.Duration;

/**
 * A poll interval that is generated by a function and a start duration. The function is free to do anything with the duration.
 * For example:
 * <pre>
 * await().with().pollInterval(iterative(duration -> duration.multiply(2)), Duration.FIVE_HUNDRED_MILLISECONDS).until(..);
 * </pre>
 * This generates a poll interval sequence that looks like this (ms): 500, 1000, 2000, 4000, 8000, 16000, ...
 */
public class IterativePollInterval implements PollInterval {

    private final Function function;
    private final Duration startDuration;

    /**
     * Generate an iterative poll interval based on the supplied function and a start duration of 100 milliseconds.
     *
     * @param function The function to use.
     */
    public IterativePollInterval(Function function) {
        this(function, Duration.ONE_HUNDRED_MILLISECONDS);
    }

    /**
     * Generate an iterative poll interval based on the supplied function and start duration.
     *
     * @param function      The function to use.
     * @param startDuration The start duration (initial function value)
     */
    public IterativePollInterval(Function function, Duration startDuration) {
        if (function == null) {
            throw new IllegalArgumentException("Function cannot be null");
        }
        if (startDuration == null) {
            throw new IllegalArgumentException("Start duration cannot be null");
        } else if (startDuration.isForever()) {
            throw new IllegalArgumentException("Cannot use a poll interval of length 'forever'");
        }
        this.function = function;
        this.startDuration = startDuration;
    }

    public Duration next(int pollCount, Duration previousDuration) {
        return function.apply(previousDuration == null ? startDuration : previousDuration);
    }

    /**
     * Syntactic sugar for creating a {@link IterativePollInterval}.
     *
     * @param function The function to use
     * @return A new instance of {@link IterativePollInterval}
     */
    public static IterativePollInterval iterative(Function function) {
        return new IterativePollInterval(function);
    }

    /**
     * Syntactic sugar for creating a {@link IterativePollInterval}.
     *
     * @param function      The function to use
     * @param startDuration The start duration (initial function value)
     * @return A new instance of {@link IterativePollInterval}
     */
    public static IterativePollInterval iterative(Function function, Duration startDuration) {
        return new IterativePollInterval(function, startDuration);
    }

    /**
     * The iterative poll interval function
     */
    public interface Function {

        /**
         * Applies this function to the given argument.
         *
         * @param previousDuration The previous duration
         * @return The next duration
         */
        Duration apply(Duration previousDuration);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IterativePollInterval)) return false;

        IterativePollInterval that = (IterativePollInterval) o;

        return function.equals(that.function) && startDuration.equals(that.startDuration);

    }

    @Override
    public int hashCode() {
        int result = function.hashCode();
        result = 31 * result + startDuration.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "IterativePollInterval{" +
                "function=" + function +
                ", startDuration=" + startDuration +
                '}';
    }
}
