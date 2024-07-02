package com.nx.boot.launch;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 *
 * <p>This class is not thread-safe</p>
 *
 * @since 2.0
 */
public class StopWatch {


    /**
     * The empty String {@code ""}.
     * @since 2.0
     */
    public static final String EMPTY = "";


    /**
     * A String for a space character.
     *
     * @since 3.2
     */
    public static final String SPACE = " ";



    /**
     * Enumeration type which indicates the split status of stopwatch.
     */
    private enum SplitState {
        SPLIT,
        UNSPLIT
    }

    /**
     * Enumeration type which indicates the status of stopwatch.
     */
    private enum State {

        RUNNING {
            @Override
            boolean isStarted() {
                return true;
            }
            @Override
            boolean isStopped() {
                return false;
            }
            @Override
            boolean isSuspended() {
                return false;
            }
        },
        STOPPED {
            @Override
            boolean isStarted() {
                return false;
            }
            @Override
            boolean isStopped() {
                return true;
            }
            @Override
            boolean isSuspended() {
                return false;
            }
        },
        SUSPENDED {
            @Override
            boolean isStarted() {
                return true;
            }
            @Override
            boolean isStopped() {
                return false;
            }
            @Override
            boolean isSuspended() {
                return true;
            }
        },
        UNSTARTED {
            @Override
            boolean isStarted() {
                return false;
            }
            @Override
            boolean isStopped() {
                return true;
            }
            @Override
            boolean isSuspended() {
                return false;
            }
        };



        /**
         * <p>
         * Returns whether the StopWatch is started. A suspended StopWatch is also started watch.
         * </p>
         *
         * @return boolean If the StopWatch is started.
         */
        abstract boolean isStarted();



        /**
         * <p>
         * Returns whether the StopWatch is stopped. The stopwatch which's not yet started and explicitly stopped stopwatch is
         * considered as stopped.
         * </p>
         *
         * @return boolean If the StopWatch is stopped.
         */
        abstract boolean isStopped();



        /**
         * <p>
         * Returns whether the StopWatch is suspended.
         * </p>
         *
         * @return boolean
         *             If the StopWatch is suspended.
         */
        abstract boolean isSuspended();
    }

    private static final long NANO_2_MILLIS = 1000000L;



    /**
     * Creates a stopwatch for convenience.
     *
     * @return StopWatch a stopwatch.
     *
     * @since 3.10
     */
    public static StopWatch create() {
        return new StopWatch();
    }



    /**
     * Creates a started stopwatch for convenience.
     *
     * @return StopWatch a stopwatch that's already been started.
     *
     * @since 3.5
     */
    public static StopWatch createStarted() {
        final StopWatch sw = new StopWatch();
        sw.start();
        return sw;
    }



    /**
     *
     * A message for string presentation.
     *
     * @since 3.10
     */
    private final String message;



    /**
     * The current running state of the StopWatch.
     */
    private State runningState = State.UNSTARTED;



    /**
     * Whether the stopwatch has a split time recorded.
     */
    private SplitState splitState = SplitState.UNSPLIT;



    /**
     * The start time in nanoseconds.
     */
    private long startTimeNanos;



    /**
     * The start time in milliseconds - nanoTime is only for elapsed time so we
     * need to also store the currentTimeMillis to maintain the old
     * getStartTime API.
     */
    private long startTimeMillis;

    /**
     * The end time in milliseconds - nanoTime is only for elapsed time so we
     * need to also store the currentTimeMillis to maintain the old
     * getStartTime API.
     */
    private long stopTimeMillis;

    /**
     * The stop time in nanoseconds.
     */
    private long stopTimeNanos;

    /**
     * <p>
     * Constructor.
     * </p>
     */
    public StopWatch() {
        this(null);
    }

    /**
     * <p>
     * Constructor.
     * </p>
     * @param message A message for string presentation.
     * @since 3.10
     */
    public StopWatch(final String message) {
        this.message = message;
    }


    public String formatSplitTime() {
        return formatDurationHMS(getSplitTime());
    }

    public String formatTime() {
        return formatDurationHMS(getTime());
    }

    /**
     * Gets the message for string presentation.
     *
     * @return the message for string presentation.
     * @since 3.10
     */
    public String getMessage() {
        return message;
    }

    /**
     * <p>
     * Gets the <em>elapsed</em> time in nanoseconds.
     * </p>
     *
     * <p>
     * This is either the time between the start and the moment this method is called, or the amount of time between
     * start and stop.
     * </p>
     *
     * @return the <em>elapsed</em> time in nanoseconds.
     * @see System#nanoTime()
     * @since 3.0
     */
    public long getNanoTime() {
        if (this.runningState == State.STOPPED || this.runningState == State.SUSPENDED) {
            return this.stopTimeNanos - this.startTimeNanos;
        } else if (this.runningState == State.UNSTARTED) {
            return 0;
        } else if (this.runningState == State.RUNNING) {
            return System.nanoTime() - this.startTimeNanos;
        }
        throw new IllegalStateException("Illegal running state has occurred.");
    }

    /**
     * <p>
     * Gets the split time in nanoseconds.
     * </p>
     *
     * <p>
     * This is the time between start and latest split.
     * </p>
     *
     * @return the split time in nanoseconds
     *
     * @throws IllegalStateException
     *             if the StopWatch has not yet been split.
     * @since 3.0
     */
    public long getSplitNanoTime() {
        if (this.splitState != SplitState.SPLIT) {
            throw new IllegalStateException("Stopwatch must be split to get the split time.");
        }
        return this.stopTimeNanos - this.startTimeNanos;
    }

    /**
     * <p>
     * Gets the split time on the stopwatch.
     * </p>
     *
     * <p>
     * This is the time between start and latest split.
     * </p>
     *
     * @return the split time in milliseconds
     *
     * @throws IllegalStateException
     *             if the StopWatch has not yet been split.
     * @since 2.1
     */
    public long getSplitTime() {
        return getSplitNanoTime() / NANO_2_MILLIS;
    }

    /**
     * Gets the time this stopwatch was started in milliseconds, between the current time and midnight, January 1, 1970
     * UTC.
     *
     * @return the time this stopwatch was started in milliseconds, between the current time and midnight, January 1,
     *         1970 UTC.
     * @throws IllegalStateException if this StopWatch has not been started
     * @since 2.4
     */
    public long getStartTime() {
        if (this.runningState == State.UNSTARTED) {
            throw new IllegalStateException("Stopwatch has not been started");
        }
        // System.nanoTime is for elapsed time
        return this.startTimeMillis;
    }

    /**
     * Gets the time this stopwatch was stopped in milliseconds, between the current time and midnight, January 1, 1970
     * UTC.
     *
     * @return the time this stopwatch was started in milliseconds, between the current time and midnight, January 1,
     *         1970 UTC.
     * @throws IllegalStateException if this StopWatch has not been started
     * @since 3.12.0
     */
    public long getStopTime() {
        if (this.runningState == State.UNSTARTED) {
            throw new IllegalStateException("Stopwatch has not been started");
        }
        // System.nanoTime is for elapsed time
        return this.stopTimeMillis;
    }

    /**
     * <p>
     * Gets the time on the stopwatch.
     * </p>
     *
     * <p>
     * This is either the time between the start and the moment this method is called, or the amount of time between
     * start and stop.
     * </p>
     *
     * @return the time in milliseconds
     */
    public long getTime() {
        return getNanoTime() / NANO_2_MILLIS;
    }

    /**
     * <p>
     * Gets the time in the specified TimeUnit.
     * </p>
     *
     * <p>
     * This is either the time between the start and the moment this method is called, or the amount of time between
     * start and stop. The resulting time will be expressed in the desired TimeUnit with any remainder rounded down.
     * For example, if the specified unit is {@code TimeUnit.HOURS} and the stopwatch time is 59 minutes, then the
     * result returned will be {@code 0}.
     * </p>
     *
     * @param timeUnit the unit of time, not null
     * @return the time in the specified TimeUnit, rounded down
     * @since 3.5
     */
    public long getTime(final TimeUnit timeUnit) {
        return timeUnit.convert(getNanoTime(), TimeUnit.NANOSECONDS);
    }

    /**
     * <p>
     * Returns whether the StopWatch is started. A suspended StopWatch is also started watch.
     * </p>
     *
     * @return boolean If the StopWatch is started.
     * @since 3.2
     */
    public boolean isStarted() {
        return runningState.isStarted();
    }

    /**
     * <p>
     * Returns whether StopWatch is stopped. The stopwatch which's not yet started and explicitly stopped stopwatch is considered
     * as stopped.
     * </p>
     *
     * @return boolean If the StopWatch is stopped.
     * @since 3.2
     */
    public boolean isStopped() {
        return runningState.isStopped();
    }

    /**
     * <p>
     * Returns whether the StopWatch is suspended.
     * </p>
     *
     * @return boolean
     *             If the StopWatch is suspended.
     * @since 3.2
     */
    public boolean isSuspended() {
        return runningState.isSuspended();
    }

    /**
     * <p>
     * Resets the stopwatch. Stops it if need be.
     * </p>
     *
     * <p>
     * This method clears the internal values to allow the object to be reused.
     * </p>
     */
    public void reset() {
        this.runningState = State.UNSTARTED;
        this.splitState = SplitState.UNSPLIT;
    }

    /**
     * <p>
     * Resumes the stopwatch after a suspend.
     * </p>
     *
     * <p>
     * This method resumes the watch after it was suspended. The watch will not include time between the suspend and
     * resume calls in the total time.
     * </p>
     *
     * @throws IllegalStateException
     *             if the StopWatch has not been suspended.
     */
    public void resume() {
        if (this.runningState != State.SUSPENDED) {
            throw new IllegalStateException("Stopwatch must be suspended to resume. ");
        }
        this.startTimeNanos += System.nanoTime() - this.stopTimeNanos;
        this.runningState = State.RUNNING;
    }

    /**
     * <p>
     * Splits the time.
     * </p>
     *
     * <p>
     * This method sets the stop time of the watch to allow a time to be extracted. The start time is unaffected,
     * enabling {@link #unsplit()} to continue the timing from the original start point.
     * </p>
     *
     * @throws IllegalStateException
     *             if the StopWatch is not running.
     */
    public void split() {
        if (this.runningState != State.RUNNING) {
            throw new IllegalStateException("Stopwatch is not running. ");
        }
        this.stopTimeNanos = System.nanoTime();
        this.splitState = SplitState.SPLIT;
    }

    /**
     * <p>
     * Starts the stopwatch.
     * </p>
     *
     * <p>
     * This method starts a new timing session, clearing any previous values.
     * </p>
     *
     * @throws IllegalStateException
     *             if the StopWatch is already running.
     */
    public void start() {
        if (this.runningState == State.STOPPED) {
            throw new IllegalStateException("Stopwatch must be reset before being restarted. ");
        }
        if (this.runningState != State.UNSTARTED) {
            throw new IllegalStateException("Stopwatch already started. ");
        }
        this.startTimeNanos = System.nanoTime();
        this.startTimeMillis = System.currentTimeMillis();
        this.runningState = State.RUNNING;
    }

    /**
     * <p>
     * Stops the stopwatch.
     * </p>
     *
     * <p>
     * This method ends a new timing session, allowing the time to be retrieved.
     * </p>
     *
     * @throws IllegalStateException
     *             if the StopWatch is not running.
     */
    public void stop() {
        if (this.runningState != State.RUNNING && this.runningState != State.SUSPENDED) {
            throw new IllegalStateException("Stopwatch is not running. ");
        }
        if (this.runningState == State.RUNNING) {
            this.stopTimeNanos = System.nanoTime();
            this.stopTimeMillis = System.currentTimeMillis();
        }
        this.runningState = State.STOPPED;
    }

    /**
     * <p>
     * Suspends the stopwatch for later resumption.
     * </p>
     *
     * <p>
     * This method suspends the watch until it is resumed. The watch will not include time between the suspend and
     * resume calls in the total time.
     * </p>
     *
     * @throws IllegalStateException
     *             if the StopWatch is not currently running.
     */
    public void suspend() {
        if (this.runningState != State.RUNNING) {
            throw new IllegalStateException("Stopwatch must be running to suspend. ");
        }
        this.stopTimeNanos = System.nanoTime();
        this.stopTimeMillis = System.currentTimeMillis();
        this.runningState = State.SUSPENDED;
    }



    /**
     * <p>
     * Gets a summary of the split time that the stopwatch recorded as a string.
     * </p>
     *
     * <p>
     * The format used is ISO 8601-like, [<i>message</i> ]<i>hours</i>:<i>minutes</i>:<i>seconds</i>.<i>milliseconds</i>.
     * </p>
     *
     * @return the split time as a String
     * @since 2.1
     * @since 3.10 Returns the prefix {@code "message "} if the message is set.
     */
    public String toSplitString() {
        final String msgStr = Objects.toString(message, EMPTY);
        final String formattedTime = formatSplitTime();
        return msgStr.isEmpty() ? formattedTime : msgStr + SPACE + formattedTime;
    }



    /**
     * <p>
     * Gets a summary of the time that the stopwatch recorded as a string.
     * </p>
     *
     * <p>
     * The format used is ISO 8601-like, [<i>message</i> ]<i>hours</i>:<i>minutes</i>:<i>seconds</i>.<i>milliseconds</i>.
     * </p>
     *
     * @return the time as a String
     * @since 3.10 Returns the prefix {@code "message "} if the message is set.
     */
    @Override
    public String toString() {
        final String msgStr = Objects.toString(message, EMPTY);
        final String formattedTime = formatTime();
        return msgStr.isEmpty() ? formattedTime : msgStr + SPACE + formattedTime;
    }

    /**
     * <p>
     * Removes a split.
     * </p>
     *
     * <p>
     * This method clears the stop time. The start time is unaffected, enabling timing from the original start point to
     * continue.
     * </p>
     *
     * @throws IllegalStateException
     *             if the StopWatch has not been split.
     */
    public void unsplit() {
        if (this.splitState != SplitState.SPLIT) {
            throw new IllegalStateException("Stopwatch has not been split. ");
        }
        this.splitState = SplitState.UNSPLIT;
    }





    //-----------------------------------------------------------------------
    /**
     * <p>Formats the time gap as a string.</p>
     *
     * <p>The format used is ISO 8601-like: {@code HH:mm:ss.SSS}.</p>
     *
     * @param durationMillis  the duration to format
     * @return the formatted duration, not null
     * @throws java.lang.IllegalArgumentException if durationMillis is negative
     */
    public static String formatDurationHMS(final long durationMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        return sdf.format(durationMillis);
    }

    private static String slf(double n) {
        return String.valueOf(Double.valueOf(Math.floor(n)).longValue());
    }


    public static String internalFormatDurationHMS(long timeInMs) {
        double t = Double.valueOf(timeInMs);
        if(t < 1000d)
            return slf(t) + "ms";
        if(t < 60000d)
            return slf(t / 1000d) + "s " +
                    slf(t % 1000d) + "ms";
        if(t < 3600000d)
            return slf(t / 60000d) + "m " +
                    slf((t % 60000d) / 1000d) + "s " +
                    slf(t % 1000d) + "ms";
        if(t < 86400000d)
            return slf(t / 3600000d) + "h " +
                    slf((t % 3600000d) / 60000d) + "m " +
                    slf((t % 60000d) / 1000d) + "s " +
                    slf(t % 1000d) + "ms";
        return slf(t / 86400000d) + "d " +
                slf((t % 86400000d) / 3600000d) + "h " +
                slf((t % 3600000d) / 60000d) + "m " +
                slf((t % 60000d) / 1000d) + "s " +
                slf(t % 1000d) + "ms";
    }

}
