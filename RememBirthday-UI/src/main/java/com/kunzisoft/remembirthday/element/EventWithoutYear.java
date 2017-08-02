package com.kunzisoft.remembirthday.element;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by joker on 28/07/17.
 */

public class EventWithoutYear {


    private static final String TAG = "EventWithoutYear";

    private List<CalendarEvent> calendarEvents;

    /**
     * Manage calendar between X years and Y Years of current year
     */
    private static final int X_YEAR = 3;
    private static final int Y_YEAR = 5;

    public EventWithoutYear(CalendarEvent baseEvent) {

        calendarEvents = new ArrayList<>();

        Calendar currCal = Calendar.getInstance();
        int currYear = currCal.get(Calendar.YEAR);
        int startYear = currYear - X_YEAR;
        int endYear = currYear + Y_YEAR;

        for (int iteratedYear = startYear; iteratedYear <= endYear; iteratedYear++) {
            CalendarEvent calendarEvent = new CalendarEvent(baseEvent);
            calendarEvent.setYear(iteratedYear);
            calendarEvent.setAllDay(true);
            calendarEvents.add(calendarEvent);
        }
    }

    /**
     * Get events for the past X years and the next Y years
     */
    public List<CalendarEvent> getEventsAroundThisYear() {
        return calendarEvents;
    }
}
