package com.keyee.datetime;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import android.app.AlertDialog;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.util.Calendar;
import android.text.format.DateFormat;
import java.text.SimpleDateFormat;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableMap;

@SuppressLint("ValidFragment")
public class DateTimePicker extends DialogFragment implements OnDateChangedListener,OnTimeChangedListener,DialogInterface.OnClickListener
{
    static final String TAG = DateTimePicker.class.getSimpleName();
    private ReadableMap options;
    private Callback callback;

    private LinearLayout dateTimeLayout;
    private ScrollView dateTimeScrollView;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private AlertDialog ad;

    private String cancelText;
    private String okText;

    private Calendar calendar;


    public DateTimePicker(ReadableMap options, Callback callback) {
        this.options = options;
        this.callback = callback;
        calendar = Calendar.getInstance();
        cancelText = options.getString("cancelText");
        okText = options.getString("okText");
        if (options.hasKey("year")) {
            calendar.set(Calendar.YEAR, options.getInt("year"));
        }
        if (options.hasKey("month")) {
            calendar.set(Calendar.MONTH, options.getInt("month"));
        }
        if (options.hasKey("day")) {
            calendar.set(Calendar.DAY_OF_MONTH, options.getInt("day"));
        }
        if (options.hasKey("hour")) {
            calendar.set(Calendar.HOUR_OF_DAY, options.getInt("hour"));
        }
        if (options.hasKey("minute")) {
            calendar.set(Calendar.MINUTE, options.getInt("minute"));
        }

    }

    private void initializePickers(){
        datePicker.init(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                this
        );
        datePicker.setCalendarViewShown(false);
        timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
        timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
    }

    private boolean is24Hour() {
        if (options.hasKey("is24Hour")) {
            return options.getBoolean("is24Hour");
        } else {
            return DateFormat.is24HourFormat(getActivity());
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (datePicker == null){
            datePicker = new DatePicker(this.getActivity(),null,0,0);
            datePicker.setCalendarViewShown(false);
        }
        if (timePicker == null){
            timePicker = new TimePicker(this.getActivity(),null,0,0);
            timePicker.setIs24HourView(is24Hour());
            timePicker.setOnTimeChangedListener(this);
        }
        if (dateTimeLayout == null){
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.FILL_PARENT,
                    LinearLayout.LayoutParams.FILL_PARENT
            );
            dateTimeLayout = new LinearLayout(this.getActivity());
            dateTimeLayout.setLayoutParams(params);
            dateTimeLayout.setOrientation(LinearLayout.VERTICAL);
            dateTimeLayout.addView(datePicker);
            dateTimeLayout.addView(timePicker);
        }
        if (dateTimeScrollView == null){
            dateTimeScrollView = new ScrollView(this.getActivity());
            dateTimeScrollView.addView(dateTimeLayout);
        }
        initializePickers();
        ad = new AlertDialog.Builder(this.getActivity())
                .setTitle(getFormattedDateTime())
                .setView(dateTimeScrollView)
                .setPositiveButton(okText, this)
                .setNegativeButton(cancelText, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {}
                }).show();
        return ad;
    }

    public void onClick(DialogInterface dialog, int whichButton) {
        onDateChanged(datePicker, datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
        onTimeChanged(timePicker, timePicker.getCurrentHour(), timePicker.getCurrentMinute());
        this.callback.invoke(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE)
        );
    }

    private String getFormattedDateTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
        return simpleDateFormat.format(calendar.getTime());
    }

    private void setTitle(){
        if (ad != null){
            ad.setTitle(getFormattedDateTime());
        }
    }

    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        setTitle();
    }

    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        setTitle();
    }
}
