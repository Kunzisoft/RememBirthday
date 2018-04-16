package com.kunzisoft.remembirthday.element;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Class for build events each year before and after a base event
 */
public class EventWithoutYear {

    private static final String TAG = "EventWithoutYear";

    private CalendarEvent baseEvent;
    private Integer baseYear;
    private List<Integer> listYears;

    /**
     * Manage calendar between X years and Y Years of current year
     */
    public static final int X_YEAR = 3;
    public static final int Y_YEAR = 5;

    public EventWithoutYear(CalendarEvent baseEvent) {
        this.baseEvent = baseEvent;

        Calendar currCal = Calendar.getInstance();
        currCal.setTime(baseEvent.getDate());
        this.baseYear = currCal.get(Calendar.YEAR);
        int startYear = baseYear - X_YEAR;
        int endYear = baseYear + Y_YEAR;
        listYears = new ArrayList<>();
        for (int iteratedYear = startYear; iteratedYear <= endYear; iteratedYear++) {
            listYears.add(iteratedYear);
        }
    }

    /**
     * Get events for the past X years and the next Y years
     */
    public List<CalendarEvent> getEventsAroundAndForThisYear() {
        List<CalendarEvent> calendarEvents = new ArrayList<>();
        for (int year : listYears) {
            CalendarEvent calendarEvent = new CalendarEvent(baseEvent);
            calendarEvent.setYear(year);
            calendarEvent.setAllDay(true);
            for(Reminder reminder : calendarEvent.getReminders()) {
                reminder.setDateEvent(calendarEvent.getDate());
            }
            calendarEvents.add(calendarEvent);
        }
        Log.d(getClass().getSimpleName(), "Events around years : " + calendarEvents);
        return calendarEvents;
    }

    /**
     * Get events for the next Y years
     */
    public List<CalendarEvent> getEventsAfterThisYear() {
        List<CalendarEvent> calendarEvents = new ArrayList<>();
        for (int year : getListOfYearsForEventsAfterThisYear()) {
            CalendarEvent calendarEvent = new CalendarEvent(baseEvent);
            calendarEvent.setYear(year);
            calendarEvent.setAllDay(true);
            calendarEvents.add(calendarEvent);
        }
        return calendarEvents;
    }

    /**
     * Get list of years for each event around base event, with year of base event
     * @return List of years
     */
    public List<Integer> getListOfYearsForEachEvent() {
        return listYears;
    }

    /**
     * Get list of years for events after the base event
     * @return List of years
     */
    public List<Integer> getListOfYearsForEventsAfterThisYear() {
        //TODO TEST
        return listYears.subList(listYears.indexOf(baseYear),listYears.size());
    }
}
