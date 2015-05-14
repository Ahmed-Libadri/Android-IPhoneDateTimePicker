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
    private transient Animation _fadeInAnimation;
    private transient Animation _fadeOutAnimation;
    private transient WheelView _dayMonth;
    private transient WheelView _month;
    private transient WheelView _year;
    private transient WheelView _day;
    private transient WheelView _hour;
    private transient WheelView _minute;
    private transient WheelView _period;
    private transient View _timeFrameView;
    private Calendar _calendar;
    private boolean _allDay;
    private static final SimpleDateFormat stringDateFormatter = new SimpleDateFormat("EEE, MMM dd, yyyy");
    private boolean _visible;

    public DateTimePicker(final Activity activity, View timeFrameView, Calendar calendar) {
        if (activity == null)
            return;

        _timeFrameView = timeFrameView;

        _dayMonth = (WheelView) _timeFrameView.findViewById(R.id.day_month);
        _month = (WheelView) _timeFrameView.findViewById(R.id.month);
        _year = (WheelView) _timeFrameView.findViewById(R.id.year);
        _day = (WheelView) _timeFrameView.findViewById(R.id.day);
        _hour = (WheelView) _timeFrameView.findViewById(R.id.hour);
        _minute = (WheelView) _timeFrameView.findViewById(R.id.minute);
        _period = (WheelView) _timeFrameView.findViewById(R.id.period);
        _calendar = calendar;

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
        int curMonth = _calendar.get(Calendar.MONTH);
        String months[] = activity.getResources().getStringArray(R.array.month_array);
        _month.setViewAdapter(new DateArrayAdapter(activity, months, curMonth));
        _month.setCurrentItem(curMonth);
        _month.setDrawShadows(false);
        _month.addChangingListener(dayListener);

        // day month
        int dayOfMonth = _calendar.get(Calendar.DAY_OF_MONTH);
        String dayMonthArray[] = generateDayMonthArray();
        _dayMonth.setDrawShadows(false);
        _dayMonth.setViewAdapter(new ArrayWheelAdapter<>(activity, dayMonthArray, 16));

        // year
        int curYear = _calendar.get(Calendar.YEAR);
        _year.setViewAdapter(new DateNumericAdapter(activity, curYear, curYear + 10, 0));
        _year.setCurrentItem(curYear);
        _year.setDrawShadows(false);
        _year.addChangingListener(dayListener);

        // day
        updateDays(activity, _year, _month, _day);
        _day.setCurrentItem(dayOfMonth - 1);
        _day.setDrawShadows(false);

        // period
        _period.setViewAdapter(new ArrayWheelAdapter<>(activity, activity.getResources().getStringArray(R.array.period_array), 16));
        _period.setDrawShadows(false);

        // hour
        _hour.setViewAdapter(new ArrayWheelAdapter<>(activity, integerToObject(activity.getResources().getIntArray(R.array.hour_array)), 16));
        _hour.setCurrentItem(curYear);
        _hour.setCyclic(true);
        _hour.setDrawShadows(false);
        _hour.addChangingListener(timeListener);

        // minute
        _minute.setViewAdapter(new DateNumericAdapter(activity, 0, 59, 1, 0));
        _minute.setCurrentItem(curYear);
        _minute.setCyclic(true);
        _minute.setDrawShadows(false);

        _fadeInAnimation = AnimationUtils.loadAnimation(activity, R.anim.fadein);

        _fadeOutAnimation = AnimationUtils.loadAnimation(activity, R.anim.fadeout);
        _fadeOutAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                _timeFrameView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void setAllDay(boolean allDay){
        _allDay = allDay;
        _dayMonth.setVisibility(allDay ? View.GONE : View.VISIBLE);
        _hour.setVisibility(allDay ? View.GONE : View.VISIBLE);
        _minute.setVisibility(allDay ? View.GONE : View.VISIBLE);
        _period.setVisibility(allDay ? View.GONE : View.VISIBLE);
        _day.setVisibility(allDay ? View.VISIBLE : View.GONE);
        _month.setVisibility(allDay ? View.VISIBLE : View.GONE);
        _year.setVisibility(allDay ? View.VISIBLE : View.GONE);
    }

    public void slide(final boolean visible) {
        if (visible)
            _timeFrameView.setVisibility(View.VISIBLE);
        
        _timeFrameView.startAnimation(visible ? _fadeInAnimation : _fadeOutAnimation);
    }

    private String[] generateDayMonthArray() {
        String[] dayMonthArray = new String[365];
        Calendar newCalendar = Calendar.getInstance();

        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd");

        dayMonthArray[0] = formatter.format(newCalendar.getTime());

        for (int i = 1; i < 365; i++){
            newCalendar.add(Calendar.DAY_OF_YEAR, 1);
            dayMonthArray[i] = formatter.format(newCalendar.getTime());
        }

        return dayMonthArray;
    }

    /**
     * Updates day wheel. Sets max days according to selected month and year
     */
    private void updateDays(Activity activity, WheelView year, WheelView month, WheelView day) {
        Calendar calendar = Calendar.getInstance();
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

    private int getSelectedYear(){
        return _calendar.get(Calendar.YEAR) + _year.getCurrentItem();
    }

    private int getSelectedMonth(){
        return _month.getCurrentItem();
    }

    private int getSelectedDay(){
        return _day.getCurrentItem();
    }

    private int getSelectedHour(){
        return _hour.getCurrentItem();
    }

    private int getSelectedMinute(){
        return _minute.getCurrentItem();
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

    @Override
    public String toString() {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(getSelectedYear(), getSelectedMonth(), getSelectedDay(), getSelectedHour(), getSelectedMinute());
        return stringDateFormatter.format(calendar.getTime());
    }
}
