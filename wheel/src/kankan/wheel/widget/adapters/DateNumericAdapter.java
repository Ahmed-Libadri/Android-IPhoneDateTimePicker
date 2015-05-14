/*
 *  Copyright 2011 Yuri Kanivets
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

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DateNumericAdapter extends NumericWheelAdapter {
    // Index of current item
    int currentItem;
    // Index of item to be highlighted
    int currentValue;

    /**
     * Constructors
     */
    public DateNumericAdapter(Context context, int minValue, int maxValue, int current) {
        super(context, minValue, maxValue);
        currentValue = current;
        setTextSize(16);
    }

    public DateNumericAdapter(Context context, int minValue, int maxValue, int incrementValue, int current) {
        super(context, minValue, maxValue, incrementValue);
        currentValue = current;
        setTextSize(16);
    }

    @Override
    protected void configureTextView(TextView view) {
        super.configureTextView(view);
        if (currentItem == currentValue) {
            view.setTextColor(0xFF0000F0);
        }
        view.setTypeface(Typeface.SANS_SERIF);
    }

    @Override
    public View getItem(int index, View cachedView, ViewGroup parent) {
        currentItem = index;
        return super.getItem(index, cachedView, parent);
    }
}