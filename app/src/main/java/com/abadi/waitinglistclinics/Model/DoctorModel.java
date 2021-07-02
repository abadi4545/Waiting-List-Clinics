package com.abadi.waitinglistclinics.Model;

public class DoctorModel {
    private String id;
    private String name;
    private String imageURL;
    private String poliDoctor;
    private String workday;
    private String worktimestart;
    private String worktimefinish;
    private String limit;
    private String lastPatient;
    private String lastDate;

    public DoctorModel() {
    }

    public String getLastDate() {
        return lastDate;
    }

    public void setLastDate(String lastDate) {
        this.lastDate = lastDate;
    }

    public String getId() {
        return id;
    }

    public String getLastPatient() {
        return lastPatient;
    }

    public void setLastPatient(String lastPatient) {
        this.lastPatient = lastPatient;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getPoliDoctor() {
        return poliDoctor;
    }

    public void setPoliDoctor(String poliDoctor) {
        this.poliDoctor = poliDoctor;
    }

    public String getWorkday() {
        return workday;
    }

    public void setWorkday(String workday) {
        this.workday = workday;
    }

    public String getWorktimestart() {
        return worktimestart;
    }

    public void setWorktimestart(String worktimestart) {
        this.worktimestart = worktimestart;
    }

    public String getWorktimefinish() {
        return worktimefinish;
    }

    public void setWorktimefinish(String worktimefinish) {
        this.worktimefinish = worktimefinish;
    }

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }
}
