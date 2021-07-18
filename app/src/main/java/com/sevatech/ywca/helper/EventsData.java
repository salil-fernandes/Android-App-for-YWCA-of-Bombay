package com.sevatech.ywca.helper;

public class EventsData {
    private String eventTitle;
    private String eventDescription;
    //    private String eventGoogleLink;
    private String eventVenue;
    private String eventDate;
    private String eventDeadline;
    private String eventTime;
    private String eventAmount;
    private String eventType;
    private String eventImageUrl;
    private String eventKey;
    private int eventClickCount;
    private int eventRegisterCount;
    private String imageId;

    public EventsData() {
        //empty constructor needed
    }

    public EventsData(String title, String date, String imageUrl, int click, int register) {
        this.eventTitle = title;
        this.eventDate = date;
        this.eventImageUrl = imageUrl;
        this.eventClickCount = click;
        this.eventRegisterCount = register;
    }


    public EventsData(String title, String description, String venue, String amount, String time, String date, String deadline, String type, String imageUrl) {

        this.eventTitle = title;
        this.eventDescription = description;
        this.eventVenue = venue;
        this.eventAmount = amount;
        this.eventTime = time;
        this.eventDate = date;
        this.eventDeadline = deadline;
        this.eventType = type;
        this.eventImageUrl = imageUrl;

    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getEventImageUrl() {
        return eventImageUrl;
    }

    public void setEventImageUrl(String eventImageUrl) {
        this.eventImageUrl = eventImageUrl;
    }

    public String getEventVenue() {
        return eventVenue;
    }

    public void setEventVenue(String eventVenue) {
        this.eventVenue = eventVenue;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getEventAmount() {
        return eventAmount;
    }

    public void setEventAmount(String eventAmount) {
        this.eventAmount = eventAmount;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventDeadline() {
        return eventDeadline;
    }

    public void setEventDeadline(String eventDeadline) {
        this.eventDeadline = eventDeadline;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventKey() {
        return eventKey;
    }

    public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
    }

    public int getEventClickCount() {
        return eventClickCount;
    }

    public void setEventClickCount(int eventClickCount) {
        this.eventClickCount = eventClickCount;
    }

    public int getEventRegisterCount() {
        return eventRegisterCount;
    }

    public void setEventRegisterCount(int eventRegisterCount) {
        this.eventRegisterCount = eventRegisterCount;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }


}

