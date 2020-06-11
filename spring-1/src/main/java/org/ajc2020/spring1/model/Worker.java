package org.ajc2020.spring1.model;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
public class Worker {
    @Id
    private String email;

    private String password;

    private String rfid;

    private String name;

    private double averageTime;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "worker")
    private final List<Login> loginHistory = new ArrayList<>();

    public String getRfid() {
        return rfid;
    }

    public void setRfid(String rfid) {
        this.rfid = rfid;
    }

    public String getName() {
        return name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void checkin(Date timestamp) {
        setStatus(Status.InOffice);
        Login login = openOrCreateLogin();
        login.setArrive(timestamp);
    }

    public void checkout(Date timestamp) {
        setStatus(Status.WorkingFromHome);
        Login login = openLogin();
        login.setLeave(timestamp);
        averageTime = getAverageTimeInOffice();
    }


    private Login openOrCreateLogin() {
        return loginHistory
                .stream()
                .filter(x->x.getLeave() == null)
                .findFirst()
                .orElse(addLogin(new Login().setWorker(this)));
    }
    private Login openLogin() {
        return loginHistory
                .stream()
                .filter(x->x.getLeave() == null)
                .findFirst()
                .orElse(null);
    }

    private Login addLogin(Login login) {
        loginHistory.add(login);
        return login;
    }

    public double getAverageTimeInOffice() {
        return loginHistory.stream()
                .filter(x->x.getLeave() != null)
                .mapToLong(x->(x.getLeave().getTime() - x.getArrive().getTime()) * 1000)
                .average().orElse(Double.NaN);
    }

    public double getAverageTime() {
        return averageTime;
    }
}
