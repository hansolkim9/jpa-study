package com.spring.jpastudy.chap06.repository;

import com.spring.jpastudy.chap06.entity.Idol;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdolRepository extends JpaRepository<Idol, Long> {
}
