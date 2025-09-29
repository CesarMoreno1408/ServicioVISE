package com.vise.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vise.model.Client;

public interface ClientRepository extends JpaRepository<Client, Long> {
}
