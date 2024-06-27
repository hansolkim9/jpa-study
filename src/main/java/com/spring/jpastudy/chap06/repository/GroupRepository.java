package com.spring.jpastudy.chap06.repository;

import com.spring.jpastudy.chap06.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long> {

}
