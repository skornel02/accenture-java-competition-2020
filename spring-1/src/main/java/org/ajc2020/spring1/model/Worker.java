package org.ajc2020.spring1.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import org.ajc2020.utility.resource.PermissionLevel;
import org.ajc2020.utility.communication.WorkerCreationRequest;
import org.ajc2020.utility.communication.WorkerResource;
import org.ajc2020.utility.resource.WorkerStatus;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;

@Entity
@Data
public class Worker implements User {

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

    @OneToMany(mappedBy = "worker", cascade = CascadeType.ALL)
    @JsonManagedReference
    private final List<OfficeHours> officeHoursHistory = new ArrayList<>();

    @OneToMany(mappedBy = "worker", cascade = CascadeType.ALL)
    @JsonManagedReference
    private final List<Ticket> tickets = new ArrayList<>();

    public Worker() {
    }

    public Worker(WorkerCreationRequest workerCreationRequest) {
        setEmail(workerCreationRequest.getEmail());
        setName(workerCreationRequest.getName());
        setPassword(workerCreationRequest.getPassword());
        setRfid(workerCreationRequest.getRfId());
    }

    public boolean checkin(Date timestamp) {
        if (getStatus().equals(WorkerStatus.InOffice)) return false;
        Ticket ticket = getTicketForDay(today());
        if (ticket != null) {
            tickets.remove(ticket);
            ticket.setWorker(null);
        }
        OfficeHours officeHours = openOrCreateLogin();
        officeHours.setArrive(timestamp);
        return true;
    }

    public boolean checkout(Date timestamp) {
        if (!getStatus().equals(WorkerStatus.InOffice)) return false;
        Optional<OfficeHours> login = openLogin();
        if (!login.isPresent()) return false;
        login.get().setLeave(timestamp);
        averageTime = getAverageTimeInOffice();
        return true;
    }

    public Date today() {
        return truncateDay(new Date());
    }

    public boolean hasTicketForToday() {
        return getTicketForDay(today()) != null;
    }

    private boolean isLoggedIn() {
        return openLogin().map(OfficeHours::isLoggedIn).orElse(false);
    }

    public WorkerStatus getStatus() {
        if (hasTicketForToday()) return WorkerStatus.OnList;
        if (isLoggedIn()) return WorkerStatus.InOffice;
        return WorkerStatus.WorkingFromHome;
    }

    private OfficeHours openOrCreateLogin() {
        return officeHoursHistory
                .stream()
                .filter(x -> x.getLeave() == null)
                .findFirst()
                .orElse(addLogin(new OfficeHours().setWorker(this)));
    }

    private Optional<OfficeHours> openLogin() {
        return officeHoursHistory
                .stream()
                .filter(x -> x.getLeave() == null)
                .findFirst();
    }

    private OfficeHours addLogin(OfficeHours officeHours) {
        officeHoursHistory.add(officeHours);
        return officeHours;
    }

    public double getAverageTimeInOffice() {
        return officeHoursHistory.stream()
                .filter(x -> x.getLeave() != null)
                .mapToLong(x -> (x.getLeave().getTime() - x.getArrive().getTime()) * 1000)
                .average().orElse(Double.NaN);
    }

    public static Date truncateDay(Date date) {
        return DateUtils.truncate(date, 5);
    }

    public Ticket getTicketForDay(Date targetDay) {
        return tickets.stream()
                .filter(x -> truncateDay(x.getTargetDay()).equals(truncateDay(targetDay)))
                .findFirst().orElse(null);
    }

    public boolean register(Date targetDay) {
        targetDay = truncateDay(targetDay);
        if (targetDay.before(truncateDay(today()))) return false;
        if (getTicketForDay(targetDay) != null) {
            return false;
        }
        Ticket ticket = new Ticket()
                .setWorker(this)
                .setCreationDate(today())
                .setTargetDay(targetDay);

        tickets.add(ticket);
        return true;
    }

    public boolean cancel(Date targetDay) {
        Ticket ticket = getTicketForDay(targetDay);
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

    public Worker updateWith(WorkerCreationRequest workerUpdateRequest) {
        if (workerUpdateRequest.getEmail() != null && !workerUpdateRequest.getEmail().isEmpty()) {
            setEmail(workerUpdateRequest.getEmail());
        }
        if (workerUpdateRequest.getName() != null && !workerUpdateRequest.getName().isEmpty()) {
            setName(workerUpdateRequest.getName());
        }
        if (workerUpdateRequest.getPassword() != null && !workerUpdateRequest.getPassword().isEmpty()) {
            setPassword(workerUpdateRequest.getPassword());
        }
        if (workerUpdateRequest.getRfId() != null && !workerUpdateRequest.getRfId().isEmpty()) {
            setRfid(workerUpdateRequest.getRfId());
        }
        return this;
    }

    @Override
    public String getLoginName() {
        return getEmail();
    }

    @Override
    public String getLoginPassword() {
        return getPassword();
    }

    @Override
    public PermissionLevel getPermissionLevel() {
        return PermissionLevel.WORKER;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Worker{");
        sb.append("uuid='").append(uuid).append('\'');
        sb.append(", email='").append(email).append('\'');
        sb.append(", password='").append(password).append('\'');
        sb.append(", rfid='").append(rfid).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", averageTime='").append(averageTime).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Worker worker = (Worker) o;
        return Objects.equals(uuid, worker.uuid) &&
                Objects.equals(email, worker.email) &&
                Objects.equals(rfid, worker.rfid) &&
                Objects.equals(name, worker.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, email, rfid, name);
    }
}
