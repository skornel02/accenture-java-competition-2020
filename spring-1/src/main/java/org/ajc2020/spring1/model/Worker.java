package org.ajc2020.spring1.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import org.ajc2020.utility.communication.WorkerCreationRequest;
import org.ajc2020.utility.communication.WorkerResource;
import org.ajc2020.utility.resource.PermissionLevel;
import org.ajc2020.utility.resource.WorkerStatus;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

    public Worker(WorkerCreationRequest workerCreationRequest, String password) {
        setEmail(workerCreationRequest.getEmail());
        setName(workerCreationRequest.getName());
        setPassword(password);
        setRfid(workerCreationRequest.getRfId());
    }

    public boolean checkin(OffsetDateTime timestamp) {
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

    public boolean checkout(OffsetDateTime timestamp) {
        if (!getStatus().equals(WorkerStatus.InOffice)) return false;
        Optional<OfficeHours> login = openLogin();
        if (!login.isPresent()) return false;
        login.get().setLeave(timestamp);
        averageTime = getAverageTimeInOffice();
        return true;
    }

    public LocalDate today() {
        return LocalDate.now();
    }

    public OffsetDateTime now() {
        return OffsetDateTime.now();
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
                .mapToLong(x -> ChronoUnit.MICROS.between(x.getArrive(), x.getLeave()))
                .average().orElse(Double.NaN);
    }

    public Ticket getTicketForDay(LocalDate targetDay) {
        return tickets.stream()
                .filter(x -> x.getTargetDay().isEqual(targetDay))
                .findFirst().orElse(null);
    }

    public boolean register(LocalDate targetDay) {
        if (targetDay.isBefore(today())) return false;
        if (getTicketForDay(targetDay) != null) {
            return false;
        }
        Ticket ticket = new Ticket()
                .setWorker(this)
                .setCreationDate(now())
                .setTargetDay(targetDay);

        tickets.add(ticket);
        return true;
    }

    public boolean cancel(LocalDate targetDay) {
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
        return "Worker{" + "uuid='" + uuid + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", rfid='" + rfid + '\'' +
                ", name='" + name + '\'' +
                ", averageTime='" + averageTime + '\'' +
                '}';
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
