package com.thefowlj.intervaltrainer

import kotlin.math.roundToInt
import android.os.Parcelable
import kotlinx.parcelize.Parcelize


/**
 * TimeUtil
 * Helper class for Time objects and process functions.
 *
 * @constructor Create empty Time util
 */
class TimeUtil {
    /**
     * Time
     * Class used to define Time objects to store hours, minutes, and seconds.
     *
     * @property hours
     * @property minutes
     * @property seconds
     * @constructor Create empty Time
     */
    @Parcelize
    class Time(internal var hours: Int, internal var minutes: Int, internal var seconds: Int): Parcelable

    companion object Factory {
        /**
         * Countdown string from millis
         *
         * @param millis
         * @return String representing timer in the format of "0:00:00"
         */
        fun countdownStringFromMillis(millis: Long): String {
            var remainingSeconds = millis.toFloat() / 1000
            var hours = (remainingSeconds / 3600).toInt()
            remainingSeconds -= (hours * 3600)
            var minutes = (remainingSeconds / 60).toInt()
            remainingSeconds -= (minutes * 60)
            var seconds = remainingSeconds.roundToInt()
            if (seconds < 0) {
                seconds = 59
                minutes -= 1
            } else if (seconds == 60) {
                seconds = 0
                minutes += 1
            }
            if (minutes == 60) {
                minutes = 0
                hours += 1
            }
            return "${hours}:${minutes.toString().padStart(
                2,
                '0'
            )}:${seconds.toString().padStart(
                2,
                '0'
            )}"
        }

        /**
         * Millis from time
         *
         * @param t
         * @return milliseconds from Time object
         */
        fun millisFromTime(t: Time): Long {
            return (t.hours * 3600000 + t.minutes * 60000 + t.seconds * 1000).toLong()
        }
    }

}