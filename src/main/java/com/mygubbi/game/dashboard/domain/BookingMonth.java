package com.mygubbi.game.dashboard.domain;

/**
 * Created by User on 07-05-2018.
 */
public class BookingMonth {

    public static final String MONTH = "bookingMonth";

    private String bookingMonth;

    public BookingMonth(String bookingMonth) {
        this.bookingMonth = bookingMonth;
    }

    public String getBookingMonth() {
        return bookingMonth;
    }

    public BookingMonth() {
    }

    public void setBookingMonth(String bookingMonth) {
        this.bookingMonth = bookingMonth;
    }
}
