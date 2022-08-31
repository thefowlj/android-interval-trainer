package com.thefowlj.intervaltrainer

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Interval
 *
 * @property n
 * @property workTime
 * @property restTime
 * @constructor Create empty Interval
 */
@Parcelize
class Interval(val n: Int, val workTime: TimeUtil.Time, val restTime: TimeUtil.Time): Parcelable