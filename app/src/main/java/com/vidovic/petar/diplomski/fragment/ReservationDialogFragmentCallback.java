package com.vidovic.petar.diplomski.fragment;

import java.util.Calendar;

/**
 * Created by Pera on 20-Mar-18.
 */

public interface ReservationDialogFragmentCallback {

    boolean onReserve(Calendar startTime, Calendar endTime, String name, Integer multiply);

}
