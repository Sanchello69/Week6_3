package com.vas.week6_3

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity(), TimerFragment.OnFragmentSendClick {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onSendClick(click: String) {
        val fragment: PiFragment? = supportFragmentManager
            .findFragmentById(R.id.piFragment) as PiFragment?
        if (fragment != null) {
            when (click) {
                "start" -> fragment.onClickStart()
                "stop" -> fragment.onClickStop()
                "pause" -> fragment.onClickPause()
            }
        }
    }
}