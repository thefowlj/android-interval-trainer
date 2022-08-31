package com.thefowlj.intervaltrainer

import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.thefowlj.intervaltrainer.databinding.FragmentIntervalBinding


/**
 * IntervalFragment
 * Create a Fragment with an array of of work/rest intervals
 *
 * @constructor Create empty Interval fragment
 */
class IntervalFragment : Fragment() {
    private var _binding: FragmentIntervalBinding? = null
    private val binding get() = _binding!!

    // Initialize global variables
    private var countdownRunning = false
    private var intervals: Array<Interval> = emptyArray()
    private var currentInterval: Interval? = null
    private var nCurrentInterval = 1
    private var currentIntervalState: String? = null
    private var totalIntervals = 1
    private var defaultMillis: Long = 0
    private var currentMillis: Long = defaultMillis
    private var timer: CountDownTimer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentIntervalBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val args: IntervalFragmentArgs by navArgs()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**
         * Set interval variables and UI values based on Interval array passed to the fragment
         */
        intervals = args.intervals
        totalIntervals = intervals.size
        currentInterval = intervals[0]
        nCurrentInterval = intervals[0].n
        binding.textViewIntervalCount.text = getString(R.string.interval_count_text, nCurrentInterval, totalIntervals)
        currentIntervalState = getString(R.string.interval_work_state)
        defaultMillis = TimeUtil.millisFromTime(currentInterval!!.workTime)
        currentMillis = defaultMillis
        binding.textviewCountdown.text = TimeUtil.countdownStringFromMillis(currentMillis)
        binding.intervalStateProgress.min = 0
        binding.intervalStateProgress.max = currentMillis.toInt()
        binding.intervalStateProgress.progress = currentMillis.toInt()

        // Start button
        binding.buttonStart.setOnClickListener {
            if (!countdownRunning) {
                timer = createCountDownTimer(currentMillis)
                timer!!.start()
                countdownRunning = true
            }
        }

        // Pause button
        binding.buttonPause.setOnClickListener {
            if (countdownRunning) {
                timer!!.cancel()
                countdownRunning = false
            }
        }

        // Reset button
        binding.buttonReset.setOnClickListener {
            timer?.cancel()
            currentMillis = defaultMillis
            binding.textviewCountdown.text = TimeUtil.countdownStringFromMillis(currentMillis)
            binding.intervalStateProgress.progress = currentMillis.toInt()
            countdownRunning = false

        }
    }

    /**
     * Create countdown timer
     *
     * @param millis
     * @return
     */
    private fun createCountDownTimer(millis: Long): CountDownTimer {
        val cdTimer = object: CountDownTimer(millis, 1000) {
            /**
             * On tick
             * Updates UI based on the time that has passed on the CountDownTimer.
             *
             * @param msUntilFinished
             */
            override fun onTick(msUntilFinished: Long) {
                currentMillis = msUntilFinished
                binding.textviewCountdown.text = TimeUtil.countdownStringFromMillis(msUntilFinished)
                binding.intervalStateProgress.progress = currentMillis.toInt()
            }

            /**
             * On finish
             * When CountDownTimer ends move to next interval state or to the next interval.
             * If no intervals remain after the last work state, set the current state to complete.
             */
            override fun onFinish() {
                binding.textviewCountdown.text = TimeUtil.countdownStringFromMillis(0)
                binding.intervalStateProgress.progress = 0


                if (nCurrentInterval == totalIntervals && currentIntervalState == getString(R.string.interval_rest_state)) {
                    // Move to complete state
                    currentIntervalState = getString(R.string.interval_complete_text)
                    binding.textViewIntervalState.text = currentIntervalState
                }
                else if (currentIntervalState == getString(R.string.interval_work_state)) {
                    // Move work state to rest state
                    currentIntervalState = getString(R.string.interval_rest_state)
                    binding.textViewIntervalState.text = currentIntervalState
                    defaultMillis = TimeUtil.millisFromTime(currentInterval!!.restTime)
                    currentMillis = defaultMillis
                    binding.textviewCountdown.text = TimeUtil.countdownStringFromMillis(currentMillis)
                    binding.intervalStateProgress.max = defaultMillis.toInt()
                    binding.intervalStateProgress.progress = currentMillis.toInt()
                    timer = createCountDownTimer(currentMillis)
                    timer?.start()
                } else {
                    // Move to next work/rest interval
                    currentInterval = intervals[nCurrentInterval]
                    nCurrentInterval++
                    currentMillis = TimeUtil.millisFromTime(currentInterval!!.workTime)
                    defaultMillis = currentMillis
                    currentIntervalState = getString(R.string.interval_work_state)
                    binding.textViewIntervalCount.text = getString(R.string.interval_count_text, nCurrentInterval, totalIntervals)
                    binding.textViewIntervalState.text = currentIntervalState
                    binding.intervalStateProgress.max = defaultMillis.toInt()

                    // Create a new CountDownTimer for new work/rest interval
                    timer = createCountDownTimer(currentMillis)
                    timer?.start()
                }
            }
        }
        return cdTimer
    }
}