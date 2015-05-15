/*
 *  Android-IPhoneDateTimePicker.
 *  
 *  Copyright 2015 Ahmed Al-Badri
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package kankan.wheel.widget;

import android.app.Activity;
import android.view.View;

import kankan.wheel.R;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import kankan.wheel.widget.adapters.DateArrayAdapter;
import kankan.wheel.widget.adapters.DateNumericAdapter;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Ahmed on 13/05/2015.
 */
public class DateTimePicker implements Serializable {
    public static final Integer[] EMPTY_INTEGER_OBJECT_ARRAY = new Integer[0];
    private static final int DEFAULT_MINUTE_INCREMENT = 5;
    private int _minuteIncrement = DEFAULT_MINUTE_INCREMENT;
    private transient Animation _fadeInAnimation;
    private transient Animation _fadeOutAnimation;
    private transient View _timeFrameDateWheelView;
    private transient TextView _timeFrameDateTextView;
    private transient WheelView _dayMonth;
    private transient WheelView _month;
    private transient WheelView _year;
    private transient WheelView _day;
    private transient WheelView _hour;
    private transient WheelView _minute;
    private transient WheelView _period;
    private Calendar _initialCalendar;
    private Calendar _resultCalendar;
    private static final SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.US);
    private static final SimpleDateFormat allDayDateFormatter = new SimpleDateFormat("EEE, MMM dd, yyyy", Locale.US);
    private boolean _timeFrameHeaderClicked;
    private boolean _allDay;
    private int _additionalYearsToPopulate = 1;

    public DateTimePicker(final Activity activity, View timeFrameView, Calendar calendar, String header, int minuteIncrement, int numberOfAdditionalYearsToPopulate) {
        if (activity == null)
            return;

        View timeFrameHeaderView = timeFrameView.findViewById(R.id.time_frame_header);
        TextView timeFrameHeaderTextView = (TextView) timeFrameView.findViewById(R.id.time_frame_header_text);
        _dayMonth = (WheelView) timeFrameView.findViewById(R.id.day_month);
        _month = (WheelView) timeFrameView.findViewById(R.id.month);
        _year = (WheelView) timeFrameView.findViewById(R.id.year);
        _day = (WheelView) timeFrameView.findViewById(R.id.day);
        _hour = (WheelView) timeFrameView.findViewById(R.id.hour);
        _minute = (WheelView) timeFrameView.findViewById(R.id.minute);
        _period = (WheelView) timeFrameView.findViewById(R.id.period);
        _timeFrameDateTextView = (TextView) timeFrameView.findViewById(R.id.time_frame_date_text);
        _timeFrameDateWheelView = timeFrameView.findViewById(R.id.time_frame_date_wheel);
        _additionalYearsToPopulate = numberOfAdditionalYearsToPopulate;
        _minuteIncrement = minuteIncrement;

        timeFrameHeaderTextView.setText(header);
        _timeFrameDateWheelView.setVisibility(View.GONE);

        _initialCalendar = (Calendar) calendar.clone();
        _initialCalendar.set(Calendar.HOUR, 12);
        _initialCalendar.set(Calendar.MINUTE, 0);
        _initialCalendar.set(Calendar.AM_PM, 0);

        _resultCalendar = (Calendar) calendar.clone();
        _resultCalendar.set(Calendar.HOUR, 12);
        _resultCalendar.set(Calendar.MINUTE, 0);
        _initialCalendar.set(Calendar.AM_PM, 0);

        OnWheelChangedListener dayListener = new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                updateDays(activity, _year, _month, _day);
            }

            @Override
            public void onCycled(WheelView wheelView, CycleType cycleType) {

            }
        };

        OnWheelChangedListener timeListener = new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
            }

            @Override
            public void onCycled(WheelView wheelView, CycleType cycleType) {
                updateTime(_period);
            }
        };

        // month
        int curMonth = _initialCalendar.get(Calendar.MONTH);
        String months[] = activity.getResources().getStringArray(R.array.month_array);
        _month.setViewAdapter(new DateArrayAdapter(activity, months, curMonth));
        _month.setCurrentItem(curMonth);
        _month.setDrawShadows(false);
        _month.addChangingListener(dayListener);
        _month.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                _resultCalendar.set(Calendar.MONTH, newValue);
                updateTimeFrameText();
            }

            @Override
            public void onCycled(WheelView wheelView, CycleType cycleType) {

            }
        });

        // day month
        int dayOfMonth = _initialCalendar.get(Calendar.DAY_OF_MONTH);
        String dayMonthArray[] = generateDayMonthArray();
        _dayMonth.setDrawShadows(false);
        _dayMonth.setViewAdapter(new ArrayWheelAdapter<>(activity, dayMonthArray, 16));
        _dayMonth.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                _resultCalendar.set(Calendar.DAY_OF_YEAR, _initialCalendar.get(Calendar.DAY_OF_YEAR));
                _resultCalendar.set(Calendar.YEAR, _initialCalendar.get(Calendar.YEAR));
                _resultCalendar.add(Calendar.DAY_OF_YEAR, newValue);
                updateTimeFrameText();
            }

            @Override
            public void onCycled(WheelView wheelView, CycleType cycleType) {

            }
        });

        // year
        int curYear = _initialCalendar.get(Calendar.YEAR);
        _year.setViewAdapter(new DateNumericAdapter(activity, curYear, curYear + _additionalYearsToPopulate, 0));
        _year.setCurrentItem(curYear);
        _year.setDrawShadows(false);
        _year.addChangingListener(dayListener);
        _year.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                _resultCalendar.set(Calendar.YEAR, _initialCalendar.get(Calendar.YEAR) + newValue);
                updateTimeFrameText();
            }

            @Override
            public void onCycled(WheelView wheelView, CycleType cycleType) {

            }
        });

        // day
        updateDays(activity, _year, _month, _day);
        _day.setCurrentItem(dayOfMonth - 1);
        _day.setDrawShadows(false);
        _day.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                _resultCalendar.set(Calendar.DAY_OF_MONTH, newValue + 1);
                updateTimeFrameText();
            }

            @Override
            public void onCycled(WheelView wheelView, CycleType cycleType) {

            }
        });

        // period
        _period.setViewAdapter(new ArrayWheelAdapter<>(activity, activity.getResources().getStringArray(R.array.period_array), 16));
        _period.setDrawShadows(false);
        _period.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                _resultCalendar.set(Calendar.AM_PM, newValue);
                updateTimeFrameText();
            }

            @Override
            public void onCycled(WheelView wheelView, CycleType cycleType) {

            }
        });

        // hour
        _hour.setViewAdapter(new ArrayWheelAdapter<>(activity, integerToObject(activity.getResources().getIntArray(R.array.hour_array)), 16));
        _hour.setCurrentItem(curYear);
        _hour.setCyclic(true);
        _hour.setDrawShadows(false);
        _hour.addChangingListener(timeListener);
        _hour.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                _resultCalendar.set(Calendar.HOUR, newValue);
                updateTimeFrameText();
            }

            @Override
            public void onCycled(WheelView wheelView, CycleType cycleType) {

            }
        });

        // minute
        _minute.setViewAdapter(new DateNumericAdapter(activity, 0, 11, _minuteIncrement, 0));
        _minute.setCurrentItem(curYear);
        _minute.setCyclic(true);
        _minute.setDrawShadows(false);
        _minute.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                _resultCalendar.set(Calendar.MINUTE, newValue * _minuteIncrement);
                updateTimeFrameText();
            }

            @Override
            public void onCycled(WheelView wheelView, CycleType cycleType) {

            }
        });

        _fadeInAnimation = AnimationUtils.loadAnimation(activity, R.anim.fadein);

        _fadeOutAnimation = AnimationUtils.loadAnimation(activity, R.anim.fadeout);
        _fadeOutAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                _timeFrameDateWheelView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        timeFrameHeaderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _timeFrameHeaderClicked = !_timeFrameHeaderClicked;
                slide(_timeFrameHeaderClicked);
                _timeFrameDateTextView.setTextColor(_timeFrameHeaderClicked ? activity.getResources().getColor(R.color.color_red) : activity.getResources().getColor(R.color.color_black));
                updateTimeFrameText();

            }
        });

        updateTimeFrameText();
    }

    public void setAllDay(boolean allDay){
        _allDay = allDay;
        updateViews(allDay);
    }

    public void slide(final boolean visible) {
        if (visible)
            _timeFrameDateWheelView.setVisibility(View.VISIBLE);

        _timeFrameDateWheelView.startAnimation(visible ? _fadeInAnimation : _fadeOutAnimation);
    }

    private void updateViews(boolean allDay) {
        int yearDifference = _resultCalendar.get(Calendar.YEAR) - _initialCalendar.get(Calendar.YEAR);

        if (allDay){
            _year.setCurrentItem(yearDifference);
            _month.setCurrentItem(_resultCalendar.get(Calendar.MONTH));
            _day.setCurrentItem(_resultCalendar.get(Calendar.DAY_OF_MONTH) - 1);
        }
        else{
            _dayMonth.setCurrentItem(_resultCalendar.get(Calendar.DAY_OF_YEAR) - _initialCalendar.get(Calendar.DAY_OF_YEAR) + yearDifference * 365);
        }

        _dayMonth.setVisibility(allDay ? View.GONE : View.VISIBLE);
        _hour.setVisibility(allDay ? View.GONE : View.VISIBLE);
        _minute.setVisibility(allDay ? View.GONE : View.VISIBLE);
        _period.setVisibility(allDay ? View.GONE : View.VISIBLE);
        _day.setVisibility(allDay ? View.VISIBLE : View.GONE);
        _month.setVisibility(allDay ? View.VISIBLE : View.GONE);
        _year.setVisibility(allDay ? View.VISIBLE : View.GONE);
        updateTimeFrameText();
    }

    private String[] generateDayMonthArray() {
        int finalYear = _initialCalendar.get(Calendar.YEAR) + _additionalYearsToPopulate;

        Calendar newCalendar = (Calendar) _initialCalendar.clone();
        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd", Locale.US);


        ArrayList<String> dayMonthArray = new ArrayList<String>();

        // until we have progressed until the final year, keep adding days
        do {
            dayMonthArray.add(formatter.format(newCalendar.getTime()));
            newCalendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        while ((newCalendar.get(Calendar.YEAR) <= finalYear));

        return dayMonthArray.toArray(new String[dayMonthArray.size()]);
    }

    /**
     * Updates day wheel. Sets max days according to selected month and year
     */
    private void updateDays(Activity activity, WheelView year, WheelView month, WheelView day) {
        Calendar calendar = (Calendar) _initialCalendar.clone();
        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + year.getCurrentItem());
        calendar.set(Calendar.MONTH, month.getCurrentItem());

        int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        day.setViewAdapter(new DateNumericAdapter(activity, 1, maxDays, calendar.get(Calendar.DAY_OF_MONTH) - 1));
        int curDay = Math.min(maxDays, day.getCurrentItem() + 1);
        day.setCurrentItem(curDay - 1, true);
    }

    /**
     * Updates period wheel. Sets period after hour cycle
     */
    private void updateTime(WheelView period) {
        period.setCurrentItem((period.getCurrentItem() + 1) % 2, true);
    }

    private static Integer[] integerToObject(int[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return EMPTY_INTEGER_OBJECT_ARRAY;
        }
        final Integer[] result = new Integer[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i];
        }
        return result;
    }

    private void updateTimeFrameText() {
        _timeFrameDateTextView.setText(this.toString());
    }

    @Override
    public String toString() {
        return _allDay ? allDayDateFormatter.format(_resultCalendar.getTime()) : dateTimeFormatter.format(_resultCalendar.getTime());
    }
}
