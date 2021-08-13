package com.example.moody;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.format.DateFormat;

import androidx.core.app.NotificationCompat;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MyWorker extends Worker {

    public MyWorker(Context context, WorkerParameters workerParameters) {
        super(context, workerParameters);
    }

    @Override
    public Result doWork() {
        System.out.println("do worker");

        Long tsLong = System.currentTimeMillis() / 1000;

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(tsLong * 1000);
        String date = DateFormat.format("HH:mm:ss", cal).toString();
        try {
            System.out.println(MenuActivity.worker_range);

            if (MenuActivity.worker_range == 1) {
                if (isTimeBetweenTwoTime("08:00:00", "16:00:00", date)) {
                    displayNotification("How are you now?", "Please complete survey!");
                }else
                {
                    displayNoNotification("wait for time");
                }

            } else if (MenuActivity.worker_range == 2) {
                if (isTimeBetweenTwoTime("16:00:00", "23:00:00", date)) {
                    displayNotification("How are you now?", "Please complete survey!");
                }else
                {
                    displayNoNotification("wait for time");
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return Result.success();
    }

    private void displayNotification(String task, String desc) {
        System.out.println("done");
        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("simplifiedcoding", "simplifiedcoding", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }

        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 1,
                notificationIntent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "simplifiedcoding")
                .setContentTitle(task)
                .setContentText(desc)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent);

        manager.notify(1, builder.build());

    }

    private void displayNoNotification(String task) {
        System.out.println(task);
    }

    public static boolean isTimeBetweenTwoTime(String initialTime, String finalTime, String currentTime) throws ParseException {

        String reg = "^([0-1][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$";
        boolean valid = false;
        if (initialTime.matches(reg) && finalTime.matches(reg) && currentTime.matches(reg)) {

            //Start Time
            //all times are from java.util.Date
            Date inTime = new SimpleDateFormat("HH:mm:ss").parse(initialTime);
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(inTime);

            //Current Time
            Date checkTime = new SimpleDateFormat("HH:mm:ss").parse(currentTime);
            Calendar calendar3 = Calendar.getInstance();
            calendar3.setTime(checkTime);

            //End Time
            Date finTime = new SimpleDateFormat("HH:mm:ss").parse(finalTime);
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(finTime);

           if (finalTime.compareTo(initialTime) < 0) {
                calendar2.add(Calendar.DATE, 1);
                calendar3.add(Calendar.DATE, 1);
            }

            java.util.Date actualTime = calendar3.getTime();

            if ((actualTime.after(calendar1.getTime()) || actualTime.compareTo(calendar1.getTime()) == 0) && actualTime.before(calendar2.getTime())) {
                valid = true;
            }

        }
        return valid;

    }
}
