package com.boxfit.backend.config;

import com.boxfit.backend.domain.entity.Role;
import com.boxfit.backend.domain.entity.User;
import com.boxfit.backend.repository.RoleRepository;
import com.boxfit.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Component
public class InitialDataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminEmail;
    private final String adminPassword;
    private final String adminFullName;
    private final String adminPhoneNumber;

    public InitialDataSeeder(RoleRepository roleRepository,
                             UserRepository userRepository,
                             PasswordEncoder passwordEncoder,
                             @Value("${app.admin.email:}") String adminEmail,
                             @Value("${app.admin.password:}") String adminPassword,
                             @Value("${app.admin.full-name:Administrator}") String adminFullName,
                             @Value("${app.admin.phone-number:}") String adminPhoneNumber) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
        this.adminFullName = adminFullName;
        this.adminPhoneNumber = adminPhoneNumber;
    }

    @Override
    @Transactional
    public void run(String... args) {
        createIfMissing(Role.ADMIN, "Administrator");
        createIfMissing(Role.STAFF, "Staff");
        createIfMissing(Role.CUSTOMER, "Customer");
        createAdminIfConfigured();
    }

    private void createIfMissing(String name, String description) {
        if (roleRepository.existsByName(name)) {
            return;
        }
        Role role = new Role();
        role.setName(name);
        role.setDescription(description);
        roleRepository.save(role);
    }

    private void createAdminIfConfigured() {
        if (!StringUtils.hasText(adminEmail) || !StringUtils.hasText(adminPassword)) {
            return;
        }

        String email = adminEmail.toLowerCase().trim();
        if (userRepository.existsByEmail(email)) {
            return;
        }

        Role adminRole = roleRepository.findByName(Role.ADMIN)
            .orElseThrow(() -> new IllegalStateException("Default admin role not found"));

        User admin = new User();
        admin.setEmail(email);
        admin.setPasswordHash(passwordEncoder.encode(adminPassword));
        admin.setFullName(StringUtils.hasText(adminFullName) ? adminFullName.trim() : "Administrator");
        admin.setPhoneNumber(StringUtils.hasText(adminPhoneNumber) ? adminPhoneNumber.trim() : null);
        admin.setRole(adminRole);
        admin.setActive(true);
        userRepository.save(admin);
    }
}
