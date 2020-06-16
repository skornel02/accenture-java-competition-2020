package org.ajc2020.spring1.model;

import lombok.Data;
import org.ajc2020.utility.communication.AdminCreationRequest;
import org.ajc2020.utility.communication.AdminResource;
import org.ajc2020.utility.resource.PermissionLevel;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
public class Admin implements User {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String uuid;

    @Column(unique = true)
    private String email;

    private String password;

    private String name;

    private boolean superAdmin;

    public Admin() {
    }

    public Admin(AdminCreationRequest adminCreationRequest, String password) {
        setEmail(adminCreationRequest.getEmail());
        setName(adminCreationRequest.getName());
        setPassword(password);
        setSuperAdmin(adminCreationRequest.isSuperAdmin());
    }

    public AdminResource toResource() {
        return AdminResource.builder()
                .email(getEmail())
                .name(getName())
                .uuid(getUuid())
                .superAdmin(isSuperAdmin())
                .build();
    }

    public Admin updateWith(AdminCreationRequest adminUpdateRequest) {
        if (adminUpdateRequest.getEmail() != null && !adminUpdateRequest.getEmail().isEmpty()) {
            setEmail(adminUpdateRequest.getEmail());
        }
        if (adminUpdateRequest.getEmail() != null && !adminUpdateRequest.getName().isEmpty()) {
            setName(adminUpdateRequest.getName());
        }
        if (adminUpdateRequest.getEmail() != null && !adminUpdateRequest.getPassword().isEmpty()) {
            setPassword(adminUpdateRequest.getPassword());
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
        return isSuperAdmin() ? PermissionLevel.SUPER_ADMIN : PermissionLevel.ADMIN;
    }
}
