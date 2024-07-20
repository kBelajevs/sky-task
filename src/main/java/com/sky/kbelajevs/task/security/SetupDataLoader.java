package com.sky.kbelajevs.task.security;

import com.sky.kbelajevs.task.security.jpa.PrivilegeEntity;
import com.sky.kbelajevs.task.security.jpa.PrivilegeRepository;
import com.sky.kbelajevs.task.security.jpa.RoleEntity;
import com.sky.kbelajevs.task.security.jpa.RoleRepository;
import com.sky.kbelajevs.task.user.UserEntity;
import com.sky.kbelajevs.task.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class SetupDataLoader implements
        ApplicationListener<ContextRefreshedEvent> {

    boolean alreadySetup = false;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if (alreadySetup)
            return;
        var readUserPrivilege
                = createPrivilegeIfNotFound("READ_USER_PRIVILEGE");
        var readProjectPrivilege
                = createPrivilegeIfNotFound("READ_MANAGER_PRIVILEGE");
        var writePrivilege
                = createPrivilegeIfNotFound("WRITE_PRIVILEGE");

        var adminPrivileges = Set.of(
                readUserPrivilege, readProjectPrivilege, writePrivilege);

        createRoleIfNotFound("ROLE_ADMIN", adminPrivileges);
        createRoleIfNotFound("ROLE_USER", Set.of(readUserPrivilege));
        createRoleIfNotFound("ROLE_MANAGER", Set.of(readUserPrivilege, readProjectPrivilege));

        var adminRole = roleRepository.findByName("ROLE_ADMIN");
        var managerRole = roleRepository.findByName("ROLE_MANAGER");
        var adminUser = new UserEntity();
        adminUser.setName("Test");
        adminUser.setPassword(passwordEncoder.encode("test"));
        adminUser.setEmail("admin");
        adminUser.setRoles(Set.of(adminRole, managerRole));
        userRepository.save(adminUser);

        var user = new UserEntity();
        user.setName("Test");
        user.setPassword(passwordEncoder.encode("test"));
        user.setEmail("user");
        RoleEntity userRole = roleRepository.findByName("ROLE_USER");
        user.setRoles(Set.of(userRole));
        userRepository.save(user);

        alreadySetup = true;
    }

    @Transactional
    PrivilegeEntity createPrivilegeIfNotFound(String name) {

        var privilege = privilegeRepository.findByName(name);
        if (privilege == null) {
            privilege = new PrivilegeEntity(name);
            privilegeRepository.save(privilege);
        }
        return privilege;
    }

    @Transactional
    void createRoleIfNotFound(
            String name, Set<PrivilegeEntity> privileges) {

        var role = roleRepository.findByName(name);
        if (role == null) {
            role = new RoleEntity(name);
            role.setPrivileges(privileges);
            roleRepository.save(role);
        }
    }
}