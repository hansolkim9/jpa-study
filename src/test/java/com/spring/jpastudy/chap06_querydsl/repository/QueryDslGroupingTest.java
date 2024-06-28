package com.spring.jpastudy.chap06_querydsl.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spring.jpastudy.chap06_querydsl.dto.GroupAverageAgeDto;
import com.spring.jpastudy.chap06_querydsl.entity.Group;
import com.spring.jpastudy.chap06_querydsl.entity.Idol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.spring.jpastudy.chap06_querydsl.entity.QIdol.idol;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
//@Rollback(false)
class QueryDslGroupingTest {

    @Autowired
    IdolRepository idolRepository;

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    JPAQueryFactory factory;


    @BeforeEach
    void setUp() {

        //given
        Group leSserafim = new Group("르세라핌");
        Group ive = new Group("아이브");
        Group bts = new Group("방탄소년단");
        Group newjeans = new Group("뉴진스");

        groupRepository.save(leSserafim);
        groupRepository.save(ive);
        groupRepository.save(bts);
        groupRepository.save(newjeans);

        Idol idol1 = new Idol("김채원", 24, "여", leSserafim);
        Idol idol2 = new Idol("사쿠라", 26, "여", leSserafim);
        Idol idol3 = new Idol("가을", 22, "여", ive);
        Idol idol4 = new Idol("리즈", 20, "여", ive);
        Idol idol5 = new Idol("장원영", 20, "여", ive);
        Idol idol6 = new Idol("안유진", 21, "여", ive);
        Idol idol7 = new Idol("카즈하", 21, "여", leSserafim);
        Idol idol8 = new Idol("RM", 29, "남", bts);
        Idol idol9 = new Idol("정국", 26, "남", bts);
        Idol idol10 = new Idol("해린", 18, "여", newjeans);
        Idol idol11 = new Idol("혜인", 16, "여", newjeans);

        idolRepository.save(idol1);
        idolRepository.save(idol2);
        idolRepository.save(idol3);
        idolRepository.save(idol4);
        idolRepository.save(idol5);
        idolRepository.save(idol6);
        idolRepository.save(idol7);
        idolRepository.save(idol8);
        idolRepository.save(idol9);
        idolRepository.save(idol10);
        idolRepository.save(idol11);

    }

    @Test
    @DisplayName("성별별로 그룹화하여 아이돌의 숫자가 3명 이하인 그룹만 조회")
    void groupByGenderTest() {
        //given

        /*
            SELECT G.*, I,gender, COUNT(I.idol_id)
            FROM tbl_idol I
            JOIN tbl_group G
            ON I.group_id = G.group_id
            GROUP BY G.group_id, I.gender
         */
        List<Tuple> idolList = factory
                .select(idol.group, idol.gender, idol.count())
                .from(idol)
                .groupBy(idol.gender, idol.group)
                .having(idol.count().loe(3))
                .fetch();

        //then
        System.out.println("\n\n");

        for (Tuple tuple : idolList) {
            Group group = tuple.get(idol.group);
            String gender = tuple.get(idol.gender);
            Long count = tuple.get(idol.count());

            System.out.println(String.format("\n그룹명: %s, 성별: %s, 인원수: %d", group.getGroupName(), gender, count));
        }

        System.out.println("\n\n");
    }

    @Test
    @DisplayName("연령대별로 그룹화하여 아이돌 수를 조회")
    void ageGroupTest() {
        //given

        // QueryDSL로 CASE WHEN THEN 표현식 만들기
        NumberExpression<Integer> ageGroupExpression = new CaseBuilder()
                .when(idol.age.between(10, 19)).then(10)
                .when(idol.age.between(20, 29)).then(20)
                .when(idol.age.between(30, 39)).then(30)
                .otherwise(0);

        //when
        List<Tuple> result = factory
                .select(ageGroupExpression, idol.count())
                .from(idol)
                .groupBy(ageGroupExpression)
                .fetch();

        //then
        assertFalse(result.isEmpty());
        for (Tuple tuple : result) {
            int ageGroupValue = tuple.get(ageGroupExpression);
            long count = tuple.get(idol.count());

            System.out.println("\n\nAge Group: " + ageGroupValue + "대, Count: " + count);
        }
    }

    @Test
    @DisplayName("그룹별 평균 나이 조회")
    void groupAverageAgeTest() {
        //given

         /*
            SELECT G.group_name, AVG(I.age)
            FROM tbl_idol I
            JOIN tbl_group G
            ON I.group_id = G.group_id
            GROUP BY G.group_id
            HAVING AVG(I.age) BETWEEN 20 AND 25
         */

        List<Tuple> result = factory
                .select(idol.group.groupName, idol.age.avg())
                .from(idol)
                .groupBy(idol.group)
                .having(idol.age.avg().between(20, 25))
                .fetch();

        // then
        assertFalse(result.isEmpty());

        System.out.println("\n\n");
        for (Tuple tuple : result) {
            String groupName = tuple.get(idol.group.groupName);
            Double avgAge = tuple.get(idol.age.avg());

            System.out.println(String.format("그룹명: %s, 평균 나이: %.2f", groupName, avgAge));
        }
        System.out.println("\n\n");

    }

    @Test
    @DisplayName("그룹별 평균 나이 조회 (결과 DTO 처리)")
    void groupAverageAgeDtoTest() {
        //given

         /*
            SELECT G.group_name, AVG(I.age)
            FROM tbl_idol I
            JOIN tbl_group G
            ON I.group_id = G.group_id
            GROUP BY G.group_id
            HAVING AVG(I.age) BETWEEN 20 AND 25
         */

        // Projections: 커스텀 DTO를 포장해주는 객체
        List<GroupAverageAgeDto> result = factory
                .select(
                        Projections.constructor(
                                GroupAverageAgeDto.class,
                                idol.group.groupName,
                                idol.age.avg()
                        )
                )
                .from(idol)
                .groupBy(idol.group)
                .having(idol.age.avg().between(20, 25))
                .fetch();

        // then
        assertFalse(result.isEmpty());

        System.out.println("\n\n");
        for (GroupAverageAgeDto tdto : result) {
            String groupName = tdto.getGroupName();
            Double avgAge = tdto.getAverageAge();

            System.out.println(String.format("그룹명: %s, 평균 나이: %.2f", groupName, avgAge));
        }
        System.out.println("\n\n");

    }



}