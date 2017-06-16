package com.example.fine.auraui;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SMSReminderGeneration extends BroadcastReceiver {

    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private static final String TAG = "SMSBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        long startMillis = 0;
        long endMillis = 0;
        Calendar beginTime = Calendar.getInstance();
        //beginTime.set(2017, 03, 25, 7/*hour*/, 30/*min*/);
        startMillis = beginTime.getTimeInMillis();
        //Calendar endTime = Calendar.getInstance();
        //endTime.set(2012, 9, 14, 8, 45);
        //endMillis = endTime.getTimeInMillis();

        if (intent.getAction().equals(SMS_RECEIVED)) {

            Bundle bundle = intent.getExtras();
            if (bundle != null)
            {
                Object[] pdus = (Object[])bundle.get("pdus");
                final SmsMessage[] messages = new SmsMessage[pdus.length];

                for (int i = 0; i < pdus.length; i++)
                {
                    messages[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                }//end for

                if (messages.length > -1)
                {
                    String string = messages[0].getMessageBody();
                    //Pattern p = Pattern.compile("(1[012]|[1-9]):[0-5][0-9](\\\\s)?(?i)(am|pm)");
                    Pattern p=Pattern.compile(".*[01]?[0-9]|2[0-3]:[0-5][0-9].*");
                    //Pattern d=Pattern.compile("^(?:(?:31(\\/|-|\\.)(?:0?[13578]|1[02]|(?:Jan|Mar|May|Jul|Aug|Oct|Dec)))\\1|(?:(?:29|30)(\\/|-|\\.)(?:0?[1,3-9]|1[0-2]|(?:Jan|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec))\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|^(?:29(\\/|-|\\.)(?:0?2|(?:Feb))\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:0?[1-9]|1\\d|2[0-8])(\\/|-|\\.)(?:(?:0?[1-9]|(?:Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep))|(?:1[0-2]|(?:Oct|Nov|Dec)))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$");
                    Pattern d=Pattern.compile("^(0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])[- /.](19|20)\\\\d\\\\d$");
                    Matcher m = p.matcher(string);
                    Matcher n=d.matcher(string);

                    if(m.matches())
                    {
                        context.sendBroadcast(new Intent("SMS_RECEIVED"));
                        Log.i(TAG,"Time Found");

                        //if(n.matches())
                        //{
                        Log.i(TAG, "Date Found");
                        Calendar cal = Calendar.getInstance();
                        Uri EVENTS_URI = Uri.parse(getCalendarUriBase(true) + "events");
                        ContentResolver cr = context.getContentResolver();
                        TimeZone timeZone = TimeZone.getDefault();

                        String time=m.group();
                        String str[] = time.split(":");
                        int hour = Integer.parseInt(str[0]);
                        int min = Integer.parseInt(str[1]);

                        String date=n.group();
                        String str1[] = time.split("/");
                        int day = Integer.parseInt(str1[0]);
                        int month = Integer.parseInt(str1[1]);
                        int year=Integer.parseInt(str1[2]);

                        beginTime.set(year, month, day, hour, min);
                        startMillis=beginTime.getTimeInMillis();

                        ContentValues values = new ContentValues();
                        values.put(CalendarContract.Events.CALENDAR_ID, 1);
                        values.put(CalendarContract.Events.TITLE, messages[0].getMessageBody());
                        values.put(CalendarContract.Events.DESCRIPTION, "A test Reminder.");
                        values.put(CalendarContract.Events.ALL_DAY, 0);
                        values.put(CalendarContract.Events.DTSTART, startMillis);
                        values.put(CalendarContract.Events.DTEND, cal.getTimeInMillis()+ 15 * 60 * 1000);
                        values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());
                        values.put(CalendarContract.Events.HAS_ALARM, 1);
                        Uri event = cr.insert(EVENTS_URI, values);

                        Uri REMINDERS_URI = Uri.parse(getCalendarUriBase(true) + "reminders");
                        values = new ContentValues();
                        values.put(CalendarContract.Reminders.EVENT_ID, Long.parseLong(event.getLastPathSegment()));
                        values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
                        values.put(CalendarContract.Reminders.MINUTES, 10);
                        cr.insert(REMINDERS_URI, values);
                        //}//end if5
                    }//end if4

                }//end if3

            }//end if2

        }//end if
    }//end onRecieve

    private String getCalendarUriBase(boolean eventUri)
    {
        Uri calendarURI = null;
        try
        {
            if (android.os.Build.VERSION.SDK_INT <= 7)
            {
                calendarURI = (eventUri) ? Uri.parse("content://calendar/") : Uri.parse("content://calendar/calendars");
            }//end if

            else
            {
                calendarURI = (eventUri) ? Uri.parse("content://com.android.calendar/") : Uri.parse("content://com.android.calendar/calendars");
            }//end else
        } //end try

        catch (Exception e)
        {
            e.printStackTrace();
        }//end catch

        return calendarURI.toString();
    }//end getCalenderUriBase()
}
