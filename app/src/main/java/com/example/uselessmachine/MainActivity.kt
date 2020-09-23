package com.example.uselessmachine

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        progressBar_main_countdown.visibility = View.GONE
        text_main_look_busy.visibility = View.GONE

        // View.OnClickListener
            // you know it's an interface through looking at the API
        // the one function that View.OnClickListener has is onClick(v: View!)
        // this lambda below is implementing that one function onClick without really mentioning it
        // explicitly. The one parameter is referenced by "it". So to access that view, I can use "it"
        // when there's one parameter in the function, "it" can be used to refer to that parameter
        button_main_look_busy.setOnClickListener {
            Toast.makeText(this, "Hello, this is the text on the button ${(it as Button).text.toString()}",
                Toast.LENGTH_SHORT).show()
        }
        switch_main_useless.setOnCheckedChangeListener{compoundButton, isChecked ->
            // toast status of button: checked or unchecked
            // if the button is checked, uncheck it
            if(isChecked){
                Toast.makeText(this, "This button is checked!", Toast.LENGTH_LONG).show()
                // ideally we wait a little bit of time, and then have this code execute
                // but Thread.sleep is illegal on the main UI thread
                // janky app --> when it doesn't really respond to your clicks and taps immediately
                // CountDownTimer is effectively making a separate thread to keep track of the time
                // we're going to make an anonymous inner class using CountDownTimer
                // it's anonymous because it's not being named ... only implementing the methods
                // it's a one-time use thing right here

                // making anonymous inner class saying this object extends CountDownTimer
                val uncheckTimer = object : CountDownTimer((Math.random()*10000).toLong(), 1000){
                    override fun onFinish() {
                        switch_main_useless.isChecked = false
                        layout_main.setBackgroundColor(Color.rgb((0..255).random(), (0..255).random(), (0..255).random()))
                    }

                    override fun onTick(millisRemaining: Long) {
                        // add in something for if switch is unchecked(false) --> cancel the timer/stop the timer
                        //"cancel function"
                        if(!switch_main_useless.isChecked){
                            cancel()
                        }
                    }
                }
                uncheckTimer.start()
            }
        }

        /*1. Switch
        a. randomize the time so it doesn't always turn off at a fixed interval.
        b. if the switch is manually turned off early, we cancel the timer (so if it gets turned back on, we don't get multiple timers running simultaneously)

        2. Self-Destruct Button

                Just like we made a lambda for onCheckedChangeListener, make a lambda for the onClickListener for the button
        a. 10 second countdown timer to display on the button
        b. when the timer is up, call finish() to close activity
        c. lock out the button by setting its enabled attribute to false (make the button no longer clickable)
        d. get the screen to flash a different color at an interval

        (to change to a random background color) :

         */

        button_main_self_destruct.setOnClickListener {
            val destructTimer = object: CountDownTimer(10000, 250){
                private var isRed = false
                private var flashes = 0
                private var timeBtwnFlashes = 1000
                private var lastFlashTime = 10000L

                override fun onFinish() {
                    layout_main.setBackgroundColor(Color.rgb(255,0,0))
                    finish()
                }

                override fun onTick(millisRemaining: Long) {
                    Log.d("countdown", "$lastFlashTime")
                    button_main_self_destruct.text = (lastFlashTime/ 1000).toString()
                    if(flashes % 5 == 0 && timeBtwnFlashes >= 2){
                        timeBtwnFlashes/=2
                    }
                    if(lastFlashTime.toInt() % timeBtwnFlashes == 0) {
                        flashes++
                        if (!isRed) {
                            layout_main.setBackgroundColor(Color.rgb(255, 0, 0))
                            isRed = true
                        }
                        else if (isRed) {
                            layout_main.setBackgroundColor(Color.rgb(255, 255, 255))
                            isRed = false
                            }
                        }
                    lastFlashTime -= 250
                }
            }
            destructTimer.start()
            button_main_self_destruct.isEnabled = false
            }

        button_main_look_busy.setOnClickListener {
            button_main_look_busy.visibility = View.INVISIBLE
            switch_main_useless.visibility = View.INVISIBLE
            button_main_self_destruct.visibility = View.INVISIBLE
            progressBar_main_countdown.visibility = View.VISIBLE
            text_main_look_busy.visibility = View.VISIBLE
            val lookBusyTimer = object: CountDownTimer(10000, 250){
                private var timeRemaining = 10000L
                override fun onFinish(){
                    cancel()
                    progressBar_main_countdown.visibility = View.INVISIBLE
                    text_main_look_busy.visibility = View.INVISIBLE
                    button_main_look_busy.visibility = View.VISIBLE
                    switch_main_useless.visibility = View.VISIBLE
                    button_main_self_destruct.visibility = View.VISIBLE
                }

                override fun onTick(millisRemaining:Long) {
                    if(timeRemaining.toInt() % 1000 == 0) {
                        progressBar_main_countdown.progress = timeRemaining.toInt() / 1000
                        text_main_look_busy.text = "${(timeRemaining.toInt() / 1000).toString()} /10 files loading"
                    }
                    timeRemaining -= 250L
                }
            }
            progressBar_main_countdown.progress = 10
            lookBusyTimer.start()
        }
    }
}
