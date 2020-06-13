package org.ajc2020.spring1.model;

import lombok.Data;
import org.ajc2020.utilty.communication.WorkerCreationRequest;
import org.ajc2020.utilty.communication.WorkerResource;
import org.ajc2020.utilty.resource.WorkerStatus;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
public class Worker {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String uuid;

    @Column(unique = true)
    private String email;

    private String password;

    private String rfid;

    private String name;

    private double averageTime;

    @Enumerated(EnumType.STRING)
    private WorkerStatus status;

    @OneToMany(mappedBy = "worker", cascade = CascadeType.ALL)
    private final List<Login> loginHistory = new ArrayList<>();

    @OneToMany(mappedBy = "worker", cascade = CascadeType.ALL)
    private final List<WaitListItem> tickets = new ArrayList<>();

    public Worker() {
    }

    public Worker(WorkerCreationRequest workerCreationRequest) {
        setEmail(workerCreationRequest.getEmail());
        setName(workerCreationRequest.getName());
        setPassword(workerCreationRequest.getPassword());
        setRfid(workerCreationRequest.getRfId());
        setStatus(WorkerStatus.WorkingFromHome);

    }

    public boolean checkin(Date timestamp) {
        if (getStatus().equals(WorkerStatus.InOffice)) return false;
        setStatus(WorkerStatus.InOffice);
        Login login = openOrCreateLogin();
        login.setArrive(timestamp);
        return true;
    }

    public boolean checkout(Date timestamp) {
        if (!getStatus().equals(WorkerStatus.InOffice)) return false;
        setStatus(WorkerStatus.WorkingFromHome);
        Login login = openLogin();
        if (login == null) return false;
        login.setLeave(timestamp);
        averageTime = getAverageTimeInOffice();
        return true;
    }


    private Login openOrCreateLogin() {
        return loginHistory
                .stream()
                .filter(x -> x.getLeave() == null)
                .findFirst()
                .orElse(addLogin(new Login().setWorker(this)));
    }

    private Login openLogin() {
        return loginHistory
                .stream()
                .filter(x -> x.getLeave() == null)
                .findFirst()
                .orElse(null);
    }

    private Login addLogin(Login login) {
        loginHistory.add(login);
        return login;
    }

    public double getAverageTimeInOffice() {
        return loginHistory.stream()
                .filter(x -> x.getLeave() != null)
                .mapToLong(x -> (x.getLeave().getTime() - x.getArrive().getTime()) * 1000)
                .average().orElse(Double.NaN);
    }

    public static Date truncateDay(Date date) {
        return DateUtils.truncate(date, 5);
    }

    public WaitListItem getTicketForDay(Date targetDay) {
        return tickets.stream()
                .filter(x -> truncateDay(x.getTargetDay()).equals(truncateDay(targetDay)))
                .findFirst().orElse(null);
    }

    public boolean register(Date targetDay) {
        targetDay = truncateDay(targetDay);
        if (!getStatus().equals(WorkerStatus.WorkingFromHome)) return false;
        if (targetDay.before(truncateDay(new Date()))) return false;
        if (getTicketForDay(targetDay) != null) {
            return false;
        }
        WaitListItem waitListItem = new WaitListItem()
                .setWorker(this)
                .setCreationDate(new Date())
                .setTargetDay(targetDay);

        setStatus(WorkerStatus.OnList);
        tickets.add(waitListItem);
        return true;
    }

    public boolean cancel(Date targetDay) {
        WaitListItem ticket = getTicketForDay(targetDay);
        if (!getStatus().equals(WorkerStatus.OnList)) return false;
        if (ticket == null) {
            return false;
        }
        tickets.remove(ticket);
        ticket.setWorker(null);
        return true;
    }

    public WorkerResource toResource() {
        return WorkerResource.builder()
                .status(getStatus())
                .rfId(getRfid())
                .name(getName())
                .email(getEmail())
                .averageTime(getAverageTime())
                .id(getUuid())
                .build();
    }
}
