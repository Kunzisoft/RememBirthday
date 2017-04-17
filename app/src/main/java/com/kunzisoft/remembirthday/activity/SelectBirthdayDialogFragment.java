package com.kunzisoft.remembirthday.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.kunzisoft.remembirthday.R;
import com.kunzisoft.remembirthday.Utility;
import com.kunzisoft.remembirthday.element.DateUnknownYear;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/**
 * DialogFragment used for selecting birthday, with day, month and year. Displays the delta between the current date and the date selected.
 */
public class SelectBirthdayDialogFragment extends DialogFragment {

    private final static String TAG = "SelectBirthdayDialogFrg";

    private final static int YEAR_DELTA = 150;

    private Spinner spinnerMonth;
    private Spinner spinnerDay;
    private Spinner spinnerYear;
    private SwitchCompat switchYear;
    private TextView textViewDaysRemaining;

    private OnClickBirthdayListener onClickListener;

    private DateUnknownYear dateUnknownYearSelected;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.Chocolate_Dialog);
        builder.setTitle(getString(R.string.dialog_select_birthday_title));
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View root = inflater.inflate(R.layout.fragment_birthday_select, null);
        // Get views
        spinnerMonth = (Spinner) root.findViewById(R.id.fragment_birthday_select_month);
        spinnerDay = (Spinner) root.findViewById(R.id.fragment_birthday_select_day);
        spinnerYear = (Spinner) root.findViewById(R.id.fragment_birthday_select_year);
        switchYear = (SwitchCompat) root.findViewById(R.id.fragment_birthday_select_enable_year);
        textViewDaysRemaining = (TextView) root.findViewById(R.id.fragment_birthday_select_days_left);

        // Create a calendar object and set year and month
        final Calendar calendar = new GregorianCalendar();

        // MONTHS
        String[] months = new DateFormatSymbols().getMonths();
        List<String> listMonth = new ArrayList<>(Arrays.asList(months));
        // Current position of month in array
        int positionCurrentMonth = calendar.get(Calendar.MONTH);

        // DAYS
        List<Integer> listDays = new ArrayList<>();
        // Get the number of days in that month
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH); // 28
        for(int i = 1; i<=daysInMonth; i++) {
            listDays.add(i);
        }
        // Current day of month
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        // YEARS
        // Current year
        int currentYear = calendar.get(Calendar.YEAR);
        List<Integer> listYears = new ArrayList<>();
        for(int i = currentYear-YEAR_DELTA; i<=currentYear; i++) {
            listYears.add(i);
        }

        // Spinners and Adapters
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(getContext(),
                R.layout.spinner_item_month, listMonth);
        spinnerMonth.setAdapter(monthAdapter);
        spinnerMonth.setSelection(positionCurrentMonth);
        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                assignDaysRemainingText();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter<Integer> daysAdapter = new ArrayAdapter<>(getContext(),
                R.layout.spinner_item_day, listDays);
        spinnerDay.setAdapter(daysAdapter);
        spinnerDay.setSelection(currentDay-1);
        spinnerDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                assignDaysRemainingText();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter<Integer> yearsAdapter = new ArrayAdapter<>(getContext(),
                R.layout.spinner_item_year, listYears);
        spinnerYear.setAdapter(yearsAdapter);
        spinnerYear.setSelection(YEAR_DELTA);
        spinnerYear.setEnabled(false);
        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                assignDaysRemainingText();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        switchYear.setChecked(false);
        switchYear.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                spinnerYear.setEnabled(b);
            }
        });

        // Show days left
        assignDaysRemainingText();

        builder.setView(root)
                .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(onClickListener!=null)
                            onClickListener.onClickPositiveButton(
                                    dateUnknownYearSelected = new DateUnknownYear(calcDate(), switchYear.isChecked()));
                    }
                })
                .setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(onClickListener!=null)
                            onClickListener.onClickNegativeButton(
                                    dateUnknownYearSelected = new DateUnknownYear(calcDate(), switchYear.isChecked()));
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    /**
     * Define string corresponding to the delta between current date and date selected and add the string in the view
     */
    private void assignDaysRemainingText() {
        dateUnknownYearSelected = new DateUnknownYear(calcDate(), switchYear.isChecked());
        int numberDaysLeft = dateUnknownYearSelected.getDeltaDaysInAYear();
        Utility.assignDaysRemainingInTextView(textViewDaysRemaining, numberDaysLeft);
    }

    /**
     * Calculate the date with day, month and year selected
     * @return Date object corresponding to day, month and year
     */
    private Date calcDate() {
        DateFormat dateFormat = new SimpleDateFormat("d MMMMM yyyy", Locale.getDefault());
        Date date = new Date();
        try {
            date = dateFormat.parse(
                    spinnerDay.getSelectedItem()+ " "+
                    spinnerMonth.getSelectedItem()+ " "+
                    spinnerYear.getSelectedItem());
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage());
        }
        return date;
    }

    /**
     * Return listener linked to dialog
     * @return OnClickBirthdayListener: the listener linked
     */
    public OnClickBirthdayListener getOnClickListener() {
        return onClickListener;
    }

    /**
     * Assign the listener for event who managed click on buttons
     * @param onClickListener OnClickBirthdayListener: the listener
     */
    public void setOnClickListener(OnClickBirthdayListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    /**
     * Callback listener for manage click on buttons
     */
    interface OnClickBirthdayListener {
        /**
         * Event called when click on positive button is performed
         * @param selectedDate DateUnknownYear: Date selected
         */
        void onClickPositiveButton(DateUnknownYear selectedDate);

        /**
         * Event called when click on negative button is performed
         * @param selectedDate DateUnknownYear: Date selected
         */
        void onClickNegativeButton(DateUnknownYear selectedDate);
    }
}
