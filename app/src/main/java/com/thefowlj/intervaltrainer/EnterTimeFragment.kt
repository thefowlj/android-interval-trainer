package com.thefowlj.intervaltrainer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.thefowlj.intervaltrainer.databinding.FragmentEnterTimeBinding


/**
 * EnterTimeFragment
 * Fragment to allow a set of work/rest intervals to be created.
 *
 * @constructor Create empty Enter time fragment
 */
class EnterTimeFragment : Fragment() {
    private var _binding: FragmentEnterTimeBinding? = null
    private val binding get() = _binding!!

    private var interval: Interval? = null  // empty Interval to store current state

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentEnterTimeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**
         * Configure number pickers
         */
        val hoursWorkPicker = binding.numberpickerWorkHours
        hoursWorkPicker.minValue = 0
        hoursWorkPicker.maxValue = 10
        hoursWorkPicker.wrapSelectorWheel = true
        hoursWorkPicker.setOnValueChangedListener { hoursWorkPicker, oldVal, newVal ->
            timeChanged()
        }
        val minutesWorkPicker = binding.numberpickerWorkMinutes
        minutesWorkPicker.minValue = 0
        minutesWorkPicker.maxValue = 59
        minutesWorkPicker.wrapSelectorWheel = true
        minutesWorkPicker.setOnValueChangedListener { minutesWorkPicker, oldVal, newVal ->
            timeChanged()
        }
        val secondsWorkPicker = binding.numberpickerWorkSeconds
        secondsWorkPicker.minValue = 0
        secondsWorkPicker.maxValue = 59
        secondsWorkPicker.wrapSelectorWheel = true
        secondsWorkPicker.setOnValueChangedListener { secondsWorkPicker, oldVal, newVal ->
            timeChanged()
        }
        val hoursRestPicker = binding.numberpickerRestHours
        hoursRestPicker.minValue = 0
        hoursRestPicker.maxValue = 10
        hoursRestPicker.wrapSelectorWheel = true
        hoursRestPicker.setOnValueChangedListener { hoursRestPicker, oldVal, newVal ->
            timeChanged()
        }
        val minutesRestPicker = binding.numberpickerRestMinutes
        minutesRestPicker.minValue = 0
        minutesRestPicker.maxValue = 59
        minutesRestPicker.wrapSelectorWheel = true
        minutesRestPicker.setOnValueChangedListener { minutesRestPicker, oldVal, newVal ->
            timeChanged()
        }
        val secondsRestPicker = binding.numberpickerRestSeconds
        secondsRestPicker.minValue = 0
        secondsRestPicker.maxValue = 59
        secondsRestPicker.wrapSelectorWheel = true
        secondsRestPicker.setOnValueChangedListener { secondsRestPicker, oldVal, newVal ->
            timeChanged()
        }

        // Configure interval slider
        val slider = binding.discreetSliderRepeatIntervals
        slider.addOnChangeListener { slider, value, fromUser ->
            binding.textViewRepeatIntervals.text = getString(R.string.repeat_intervals, value.toInt())
            timeChanged()
        }

        /**
         * Set button on click listener.
         * Create interval array to send to IntervalFragment.
         */
        binding.buttonEnterTime.setOnClickListener {
            val intervalList = emptyList<Interval>().toMutableList()
            val nIntervals = binding.discreetSliderRepeatIntervals.value.toInt()
            val workTime = TimeUtil.Time(
                binding.numberpickerWorkHours.value,
                binding.numberpickerWorkMinutes.value,
                binding.numberpickerWorkSeconds.value
            )
            val restTime = TimeUtil.Time(
                binding.numberpickerRestHours.value,
                binding.numberpickerRestMinutes.value,
                binding.numberpickerRestSeconds.value
            )
            for (i in 1..nIntervals) {
                intervalList += Interval(i, workTime, restTime)
            }
            val intervalArray: Array<Interval> = intervalList.toTypedArray()

            // Naviage to IntervalFragment
            val action = EnterTimeFragmentDirections.actionEnterTimeFragmentToIntervalFragment(intervalArray)
            findNavController().navigate(action)
        }
    }

    /**
     * On view state restored
     * Restores interface values from stored interval variable.
     *
     * @param savedInstanceState
     */
    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        binding.numberpickerWorkHours.value = interval?.workTime?.hours ?: 0
        binding.numberpickerWorkMinutes.value = interval?.workTime?.minutes ?: 0
        binding.numberpickerWorkSeconds.value = interval?.workTime?.seconds ?: 0
        binding.numberpickerRestHours.value = interval?.restTime?.hours ?: 0
        binding.numberpickerRestMinutes.value = interval?.restTime?.minutes ?: 0
        binding.numberpickerRestSeconds.value = interval?.restTime?.seconds ?: 0
        timeChanged()
    }

    /**
     * On pause
     * Save current Interval values to interval variable.
     */
    override fun onPause() {
        val workTime = TimeUtil.Time(
            binding.numberpickerWorkHours.value,
            binding.numberpickerWorkMinutes.value,
            binding.numberpickerWorkSeconds.value
        )
        val restTime = TimeUtil.Time(
            binding.numberpickerRestHours.value,
            binding.numberpickerRestMinutes.value,
            binding.numberpickerRestSeconds.value
        )
        interval = Interval(1, workTime, restTime)
        super.onPause()
    }

    /**
     * Calculate milliseconds from UI
     */
    private fun calculateMillisFromUI(): Long {
        val hours = binding.numberpickerWorkHours.value + binding.numberpickerRestHours.value
        val minutes = binding.numberpickerWorkMinutes.value + binding.numberpickerRestMinutes.value
        val seconds = binding.numberpickerWorkSeconds.value + binding.numberpickerRestSeconds.value
        val time = TimeUtil.Time(hours, minutes, seconds)
        return TimeUtil.millisFromTime(time)
    }

    /**
     * Update total time based on UI input
     */
    private fun timeChanged() {
        val intervalCount = binding.discreetSliderRepeatIntervals.value
        val millis = (calculateMillisFromUI() * intervalCount).toLong()
        val timeString = TimeUtil.countdownStringFromMillis(millis)
        binding.textViewTotalTime.text = getString(R.string.interval_total_time, timeString)
    }
}