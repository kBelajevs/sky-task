package com.sky.kbelajevs.task.security.jpa;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "privileges")
@Getter
@Setter
@NoArgsConstructor
public class PrivilegeEntity {

    public PrivilegeEntity(String name) {
        this.name = name;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "privileges")
    private Set<RoleEntity> roles;
}
