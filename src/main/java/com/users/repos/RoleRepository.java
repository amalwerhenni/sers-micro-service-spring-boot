package com.users.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.users.entity.Roles;

public interface RoleRepository extends JpaRepository<Roles,Long> {

    Roles findByRole(String r);
}
