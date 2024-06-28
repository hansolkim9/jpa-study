package com.spring.jpastudy.chap06_querydsl.repository;

import com.spring.jpastudy.chap06_querydsl.entity.Idol;

import java.util.List;

public interface IdolCustomRepository {

    // 이름으로 오름차해서 전체조회
    List<Idol> findAllSortedByName();

    // 그룹명으로 아이돌을 조회
    List<Idol> findByGroupName();
}