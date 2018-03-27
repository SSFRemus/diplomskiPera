package com.vidovic.petar.diplomski.fragment;

import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.vidovic.petar.diplomski.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ReservationDialogFragment extends DialogFragment {

    public ReservationDialogFragmentCallback callback;

    private View rootView;
    private Calendar startTime;

    public static ReservationDialogFragment newInstance(Calendar time) {
        ReservationDialogFragment fragment = new ReservationDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("startTime", time);
        fragment.setArguments(bundle);

        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_reservation_dialog, container, false);

        startTime = (Calendar) getArguments().getSerializable("startTime");

        Button cancel = rootView.findViewById(R.id.cancelButton);
        Button reserve = rootView.findViewById(R.id.reserveButton);
        final EditText eventNameEditText = rootView.findViewById(R.id.eventNameEditText);
        final Spinner startMinutesSpinner = rootView.findViewById(R.id.startMinutesSpinner);
        final Spinner endHourSpinner = rootView.findViewById(R.id.endHourSpinner);
        final Spinner endMinutesSpinner = rootView.findViewById(R.id.endMinutesSpinner);
        TextView startHourTextView = rootView.findViewById(R.id.startHourTextView);

        startHourTextView.setText(Integer.toString(startTime.get(Calendar.HOUR_OF_DAY)));

        setFixedSpinners(startMinutesSpinner, endMinutesSpinner, endHourSpinner);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        reserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = eventNameEditText.getText().toString();

                if (name.isEmpty()) {
                    Toast.makeText(getActivity(), "Molimo unesite naziv rezervacije.", Toast.LENGTH_LONG).show();
                } else {
                    startTime.set(Calendar.MINUTE, (Integer) startMinutesSpinner.getSelectedItem());

                    Calendar endTime = (Calendar) startTime.clone();
                    endTime.set(Calendar.HOUR_OF_DAY, (Integer) endHourSpinner.getSelectedItem());
                    endTime.set(Calendar.MINUTE, (Integer) endMinutesSpinner.getSelectedItem());

                    boolean isTimeCorrect = true;

                    if (startTime.get(Calendar.HOUR_OF_DAY) > endTime.get(Calendar.HOUR_OF_DAY)) {
                        Toast.makeText(getActivity(), "Vreme rezervacije nije ispravno", Toast.LENGTH_LONG).show();
                        isTimeCorrect = false;
                    } else if (startTime.get(Calendar.HOUR_OF_DAY) == endTime.get(Calendar.HOUR_OF_DAY) &&
                            startTime.get(Calendar.MINUTE) >= endTime.get(Calendar.MINUTE)) {
                        Toast.makeText(getActivity(), "Vreme rezervacije nije ispravno", Toast.LENGTH_LONG).show();
                        isTimeCorrect = false;
                    }

                    if (isTimeCorrect) {
                        if (callback.onReserve(startTime, endTime, name)) {
                            dismiss();
                        } else {
                            Toast.makeText(getActivity(), "Nije moguće napraviti rezervaciju u traženom terminu", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });

        return rootView;
    }

    private void setFixedSpinners(Spinner startMinutesSpinner, Spinner endMinutesSpinner, Spinner endHourSpinner) {
        List<Integer> list = new ArrayList<>();

        list.add(0);
        list.add(15);
        list.add(30);
        list.add(45);

        ArrayAdapter adapter = new ArrayAdapter(this.getContext(), R.layout.support_simple_spinner_dropdown_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        startMinutesSpinner.setAdapter(adapter);
        endMinutesSpinner.setAdapter(adapter);

        List<Integer> hourList = new ArrayList<>();

        for (int i = 8; i <= 22; i++) {
            hourList.add(i);
        }

        ArrayAdapter hourAdapter = new ArrayAdapter(this.getContext(), R.layout.support_simple_spinner_dropdown_item, hourList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        endHourSpinner.setAdapter(hourAdapter);
    }

}