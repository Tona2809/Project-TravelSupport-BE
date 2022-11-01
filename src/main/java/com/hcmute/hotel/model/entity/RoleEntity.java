package com.hcmute.hotel.model.entity;
import com.hcmute.hotel.common.AppUserRole;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.*;

@RestResource(exported = false)
@Entity
@Table(name = "\"roles\"")
@NoArgsConstructor
public class RoleEntity {

    public RoleEntity( AppUserRole name) {
        this.name = name;
    }

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "\"id\"")
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private AppUserRole name;

    public AppUserRole getName() {
        return name;
    }

    public void setName(AppUserRole name) {
        this.name = name;
    }
}
