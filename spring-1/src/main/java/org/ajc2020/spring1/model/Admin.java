package org.ajc2020.spring1.model;

import lombok.Data;
import org.ajc2020.utilty.communication.AdminCreationRequest;
import org.ajc2020.utilty.communication.AdminResource;
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

    public Admin(AdminCreationRequest adminCreationRequest) {
        setEmail(adminCreationRequest.getEmail());
        setName(adminCreationRequest.getName());
        setPassword(adminCreationRequest.getPassword());
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
        if (!adminUpdateRequest.getEmail().isEmpty()) {
            setEmail(adminUpdateRequest.getEmail());
        }
        if (!adminUpdateRequest.getName().isEmpty()) {
            setName(adminUpdateRequest.getName());
        }
        if (!adminUpdateRequest.getPassword().isEmpty()) {
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
