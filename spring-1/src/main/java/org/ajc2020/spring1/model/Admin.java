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
public class Admin {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String uuid;

    @Column(unique = true)
    private String email;

    private String password;

    private String name;

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
}
