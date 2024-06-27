package com.spring.jpastudy.chap06.repository;

import static com.spring.jpastudy.chap06.entity.QIdol.idol;
import static org.junit.jupiter.api.Assertions.*;

import javax.persistence.EntityManager;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spring.jpastudy.chap06.entity.Group;
import com.spring.jpastudy.chap06.entity.Idol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Rollback
class QueryDslBasicTest {

    @Autowired
    IdolRepository idolRepository;
    @Autowired
    GroupRepository groupRepository;

    // JPA의 CRUD를 제어하는 객체
    @Autowired
    EntityManager em;

    @Autowired
    JPAQueryFactory factory;


    @BeforeEach
    void setUp() {
        //given
        Group leSserafim = new Group("르세라핌");
        Group ive = new Group("아이브");

        groupRepository.save(leSserafim);
        groupRepository.save(ive);

        Idol idol1 = new Idol("김채원", 24, leSserafim);
        Idol idol2 = new Idol("사쿠라", 26, leSserafim);
        Idol idol3 = new Idol("가을", 22, ive);
        Idol idol4 = new Idol("리즈", 20, ive);

        idolRepository.save(idol1);
        idolRepository.save(idol2);
        idolRepository.save(idol3);
        idolRepository.save(idol4);
    }

    @Test
    @DisplayName("JPQL로 특정이름의 아이돌 조회하기")
    void jpqlTest() {
        //given
        String jpqlQuery = "SELECT i FROM Idol i WHERE i.idolName = ?1";

        //when
        Idol foundIdol = em.createQuery(jpqlQuery, Idol.class)
                .setParameter(1, "가을")
                .getSingleResult();

        //then
        assertEquals("아이브", foundIdol.getGroup().getGroupName());

        System.out.println("\n\n\n\n");
        System.out.println("foundIdol = " + foundIdol);
        System.out.println("foundIdol.getGroup() = " + foundIdol.getGroup());
        System.out.println("\n\n\n\n");
    }

    @Test
    @DisplayName("QueryDsl로 특정 이름의 아이돌 조회하기")
    void queryDslTest() {
        //given
        JPAQueryFactory factory = new JPAQueryFactory(em);
        //when
        Idol foundIdol = factory
                .select(idol)
                .from(idol)
                .where(idol.idolName.eq("가을"))
                .fetchOne();
        //then
        assertEquals("아이브", foundIdol.getGroup().getGroupName());

        System.out.println("\n\n\n\n");
        System.out.println("foundIdol = " + foundIdol);
        System.out.println("foundIdol.getGroup() = " + foundIdol.getGroup());
        System.out.println("\n\n\n\n");
    }
}