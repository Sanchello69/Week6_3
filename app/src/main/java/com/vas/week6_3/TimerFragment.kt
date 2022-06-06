package com.vas.week6_3

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.vas.week6_3.databinding.FragmentTimerBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

class TimerFragment : Fragment() {

    private var binding: FragmentTimerBinding? = null

    private var countColor = 0
    private var seconds = 0
    private var isActive = true

    private var job : Job? = null

    private var fragmentSendClick : OnFragmentSendClick? = null

    override fun onAttach(context: Context) {
        fragmentSendClick = context as (OnFragmentSendClick)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTimerBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("seconds", seconds)
        outState.putInt("count", countColor)
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        seconds = savedInstanceState?.getInt("seconds") ?: 0
        countColor = savedInstanceState?.getInt("count") ?: 0
        super.onViewStateRestored(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()

        job = CoroutineScope(Dispatchers.Main).launch {
            startColorTimer()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        job?.cancel()
    }

    private fun setupUI() {
        setTimerTextView("00:00")
        binding?.apply {
            playImageView.isClickable = false
            playImageView.setOnClickListener {
                fragmentSendClick?.onSendClick("start")
                playImageView.isClickable = false
                pauseImageView.isClickable = true
                rstImageView.isClickable = true

                job?.cancel()
                isActive = true
                job = CoroutineScope(Dispatchers.Main).launch {
                    startColorTimer()
                }

            }
            pauseImageView.setOnClickListener {
                fragmentSendClick?.onSendClick("pause")

                playImageView.isClickable = true
                pauseImageView.isClickable = false
                rstImageView.isClickable = true

                isActive = false
            }
            rstImageView.setOnClickListener {
                fragmentSendClick?.onSendClick("stop")

                playImageView.isClickable = true
                pauseImageView.isClickable = true
                rstImageView.isClickable = false

                isActive = false
                seconds = 0
                countColor = 0
            }
        }
    }

    private fun setTimerTextView(time: String) {
        binding?.timerTextView?.text = time
    }

    private fun runTimer(): Flow<String> = flow {
        while (isActive){
            delay(1000)
            var secondsTimer = seconds%60
            var minutesTimer = seconds/60

            var time = String.format("%02d:%02d", minutesTimer, secondsTimer)

            seconds ++
            emit(time)
        }
    }

    private suspend fun startColorTimer(){
        runTimer().collect {
            setTimerTextView(it)

            if (countColor==20){
                binding?.backgroundTimer?.setBackgroundColor(Color.argb(
                    255, (0..255).random(), (0..255).random(), (0..255).random()))
                countColor=0
            } else {
                countColor ++
            }
        }
    }

    interface OnFragmentSendClick {
        fun onSendClick(click: String)
    }

}