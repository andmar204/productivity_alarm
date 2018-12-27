package com.example.andre.productivealarm;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
{

    TimePicker alarmTime;
    Context appContext;
    String realAlarmTime;
    String formattedTime;
    int theAmount;
    int snoozeCount;

    final int NINE_MINUTE_SNOOZE = 540000;


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        theAmount = 2;
        snoozeCount = 0;
        alarmTime = findViewById(R.id.alarmSetter);

        /*Ringtone section*/
        appContext = getApplicationContext();
        final Ringtone ring = RingtoneManager.getRingtone(appContext, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));

        /*
         * Snooze button section
         * */
        final Button snooze = findViewById(R.id.snoozeButton);
        snooze.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (ring.isPlaying() && (snoozeCount < theAmount))
                {
                    Date currentTime = Calendar.getInstance().getTime();
                    DateFormat formatter = new SimpleDateFormat("HH:mm");
                    formattedTime = formatter.format(currentTime);

                    Date currentPlusNine = new Date(Calendar.getInstance().getTimeInMillis() + NINE_MINUTE_SNOOZE);
                    String snoozeTime = formatter.format(currentPlusNine);
                    realAlarmTime = snoozeTime;
                    Toast.makeText(appContext,
                            "currentTime: " + formattedTime + "\nsnoozeTime: " + snoozeTime,
                            Toast.LENGTH_LONG).show();
                    Toast.makeText(appContext, "Alarm snoozed until " + snoozeTime, Toast.LENGTH_LONG).show();
                    ring.stop();
                    snoozeCount++;
                }
            }
        });
        /*
         * End snooze button section
         * */


        /*
         * Set alarm button section
         * */
        final Button set = findViewById(R.id.setButton);
        set.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                AlertDialog.Builder dBuilder = new AlertDialog.Builder(MainActivity.this);
                View theView = getLayoutInflater().inflate(R.layout.snooze_amount_chooser, null);
                final EditText snoozeAmount = (EditText) theView.findViewById(R.id.snoozeEditText);
                Button confirmButton = (Button) theView.findViewById(R.id.confirmButton);


                dBuilder.setView(theView);
                final AlertDialog dialog = dBuilder.create();
                confirmButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        String amountString = snoozeAmount.getText().toString();
                        if(amountString.isEmpty())
                        {
                            Toast.makeText(MainActivity.this,
                                    "Please fill the one input box there...",
                                    Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            theAmount = Integer.parseInt(amountString);
                            if (theAmount > 4)
                            {
                                Toast.makeText(MainActivity.this,
                                        "You snooze, you lose! (\"Try a number less than 5\", " +
                                                "is what I was trying to say.)",
                                        Toast.LENGTH_LONG).show();
                            } else
                            {
                                Toast.makeText(MainActivity.this, "Got it!", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            }
                        }
                    }
                });

                dialog.show();


                int realAlarmHour = alarmTime.getHour();
                int realAlarmMinute = alarmTime.getMinute();
                if (realAlarmMinute < 10)
                {
                    realAlarmTime = realAlarmHour + ":0" + realAlarmMinute;
                } else
                {
                    realAlarmTime = realAlarmHour + ":" + realAlarmMinute;
                }

                String amPmTime;
                if (realAlarmHour > 12)
                {
                    if (realAlarmMinute < 10)
                    {
                        amPmTime = (realAlarmHour - 12) + ":0" + realAlarmMinute + " PM";
                    } else
                    {
                        amPmTime = (realAlarmHour - 12) + ":" + realAlarmMinute + " PM";
                    }
                } else
                {
                    amPmTime = realAlarmTime + " AM";
                }

                Toast.makeText(appContext,"Alarm set at " + amPmTime, Toast.LENGTH_LONG).show();
            }
        });
        /*
         * End set alarm button section
         * */

        /*
         * Stop alarm button section
         * */
        final Button stop = findViewById(R.id.stopButton);
        stop.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ring.stop();
                realAlarmTime = null;
                theAmount = 2;
                snoozeCount = 0;
                Toast.makeText(appContext, "Alarm turned off", Toast.LENGTH_LONG).show();
            }
        });
        /*
         * End stop alarm button section
         * */

        Timer theTimer = new Timer();

        theTimer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                /*
                 * formattedTime and realAlarmTime are the two times in string form. Ideally, they're
                 * both in "HH:mm" format so that they're simple to compare. Since I'll be checking if they're
                 * equal every second, they don't need to check their seconds. Only their minutes and hours.
                 *
                 * Every second, this will create one string, the current time. The realAlarmTime is
                 * set when I hit the setAlarm button. We don't want an alarm when we don't hit the
                 * setAlarm. That'd be super damn annoying. So, if the user never sets an alarm, the
                 * realAlarmTime will just be null, and this will compare the formattedTime to null.
                 * Which will always be false. Once the user hits the setAlarm, the realAlarmTime is
                 * set to an actual value that isn't null. That value will be the timePicker time in
                 * HH:mm form. This will then compare them and if they're equal, it'll ring. Else, it
                 * won't.
                 * */
                Date currentTime = Calendar.getInstance().getTime();
                DateFormat formatter = new SimpleDateFormat("HH:mm");
                formattedTime = formatter.format(currentTime);


                if (formattedTime.equals(realAlarmTime))
                {
                    ring.play();
                }
            }
        }, 0, 1000);
    }
}
