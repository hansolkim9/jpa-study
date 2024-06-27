package com.spring.jpastudy.chap04_relation.repository;

import com.spring.jpastudy.chap04_relation.entity.Department;
import com.spring.jpastudy.chap04_relation.entity.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import javax.transaction.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class DepartmentRepositoryTest {

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    DepartmentRepository departmentRepository;

    @Test
    @DisplayName("특정 부서를 조회하면 해당 소속부서원들이 함께 조회된다.")
    void findDeptTest() {
        //given
        Long id = 1L;
        //when
        Department department = departmentRepository.findById(id).orElseThrow();
        //then

        System.out.println("\n\n\n\n");
        System.out.println("department = " + department);
        System.out.println("\n\n\n\n");

        List<Employee> employees = department.getEmployees();
        System.out.println("\n\n\n\n");
        employees.forEach(System.out::println);
        System.out.println("\n\n\n\n");
    }

    // 양방향 연관관계에서 리스트에 데이터 갱신시 주의사항

    @Test
    @DisplayName("양방향 연관관계에서 연관데이터 수정")
    void changeTest() {
        //given

        // 3번 사원의 부서를 2번부서에서 1번부서로 수정

        // 3번 사원 정보 조회
        Employee employee = employeeRepository.findById(3L).orElseThrow();

        // 1번 부서 정보 조회
        Department department = departmentRepository.findById(1L).orElseThrow();

        //when
        /*
            사원정보가 Employee 엔터티에서 수정되어도
            반대편 엔터티인 Department 리스트에 바로 반영되지 않는다.

            해결방안은 데이터 수정 시 반대편 엔터티에도 같이 수정을 해야 한다.
         */
//        // 사원 정보 수정
//        employee.setDepartment(department);
//        // 핵심: 양방향에서는 수정시 반대편도 같이 수정!
//        department.getEmployees().add(employee);

        employee.changeDepartment(department);

        employeeRepository.save(employee);

        //then
        // 바뀐부서의 사원목록 조회
        List<Employee> employees = department.getEmployees();
        System.out.println("\n\n\n\n");
        employees.forEach(System.out::println);
        System.out.println("\n\n\n\n");

    }


    @Test
    @DisplayName("고아 객체 삭제하기")
    void orphanRemovalTest() {
        //given

        // 1번 부서 조회
        Department department = departmentRepository.findById(1L).orElseThrow();

        // 1번 부서 사원 목록 가져오기
        List<Employee> employeesList = department.getEmployees();

        // 2번 사원 조회
        Employee employee = employeeRepository.findById(2L).orElseThrow();

        //when
        // 부서목록에서 사원 삭제
//        employeesList.remove(employee);
//        employee.setDepartment(null);
        department.removeEmployee(employee);

        // 갱신 반영
//        departmentRepository.save(department);

        //then
    }

    @Test
    @DisplayName("양방향관계에서 리스트에 데이터를 추가하면 DB에도 INSERT된다.")
    void cascadePersistTest() {
        //given

        // 2번 부서 조회
        Department department = departmentRepository.findById(2L).orElseThrow();

        // 새로운 사원 생성
        Employee employee = Employee.builder()
                .name("뽀로로")
                .build();

        //when
        department.addEmployee(employee);

        //then
    }

    @Test
    @DisplayName("부서가 사라지면 해당 사원들도 함께 사라진다.")
    void cascadeRemoveTest() {
        //given
        Department department = departmentRepository.findById(2L).orElseThrow();
        //when
        departmentRepository.delete(department);
        //then
    }

}