/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.codepath.data;

import android.provider.BaseColumns;
import android.text.format.Time;

/**
 * Defines table and column names for the todo database.
 */
public class TodoContract {

    // To make it easy to query for the exact date, we normalize all dates that go into
    // the database to the start of the the Julian day at UTC.
    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }


    /* Inner class that defines the table contents of the todos table */
    public static final class TodoEntry implements BaseColumns {

        public static final String TABLE_NAME = "todos";

        // Column with the primary key
        public static final String COLUMN_ID = "_id";

        // Title, stored as string
        public static final String COLUMN_TITLE = "title";

        // Date, stored as long in milliseconds since the epoch
        public static final String COLUMN_DATE = "date";

        // boolean to mark todo as urgent
        public static final String COLUMN_URGENT = "urgent";

    }
}
