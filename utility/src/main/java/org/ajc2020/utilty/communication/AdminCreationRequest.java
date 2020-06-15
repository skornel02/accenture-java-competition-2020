package org.ajc2020.utilty.communication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminCreationRequest {
    @Email
    @NotNull
    private String email;

    @NotEmpty
    @NotNull
    private String name;

    @NotNull
    @NotEmpty
    private String password;

    @NotNull
    private boolean superAdmin;
}
