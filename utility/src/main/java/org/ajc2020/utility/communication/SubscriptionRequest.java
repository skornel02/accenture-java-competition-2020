package org.ajc2020.utility.communication;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Data
@NoArgsConstructor
public class SubscriptionRequest {

    @NotNull
    @FutureOrPresent
    private LocalDate targetDate;

    private LocalTime length;

    @JsonIgnore
    public Optional<LocalTime> getLengthOpt() {
        return Optional.ofNullable(length);
    }
}
