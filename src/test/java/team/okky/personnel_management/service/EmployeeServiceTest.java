package team.okky.personnel_management.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import team.okky.personnel_management.department.Department;
import team.okky.personnel_management.employee.Employee;
import team.okky.personnel_management.employee.EmployeeDTO;
import team.okky.personnel_management.utils.dto.PageRequestDTO;
import team.okky.personnel_management.employee.EmployeeService;
import team.okky.personnel_management.department.DepartmentRepository;
import team.okky.personnel_management.employee.EmployeeRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class EmployeeServiceTest {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private DepartmentRepository departmentRepository;

    @Test
    public void 전체_직원_목록() throws Exception {
        //given
        for (int i = 0; i < 105; i++) {
            Employee employee = Employee.builder()
                    .empName("테스터" + i)
                    .department(Department.builder()
                            .deptName("부서" + i)
                            .build())
                    .build();
            employeeRepository.save(employee);
        }
        //when
        List<EmployeeDTO.Index> result = employeeService.viewAll(new PageRequestDTO(11));
        //then
        assertThat(105).isEqualTo(employeeRepository.findTotal());
        assertThat(5).isEqualTo(result.size());

    }

    @Test
    public void 직원_이름_검색() throws Exception {
        //given
        List<String> findNameList = new ArrayList();
        for (int i = 0; i < 3; i++) {
            Employee employee = Employee.builder()
                    .empName("테스터")
                    .department(Department.builder()
                            .deptName("부서" + i)
                            .build())
                    .build();
            Employee employee2 = Employee.builder()
                    .empName("테스터" + i)
                    .build();
            findNameList.add("테스터");
            employeeRepository.save(employee);
            employeeRepository.save(employee2);
        }
        //when
        List<EmployeeDTO.Index> result = employeeService.viewAllByName("테스터", new PageRequestDTO(1));
        //then
        assertThat(result).extracting("empName")
                .containsAll(findNameList);

        assertThat(findNameList.size()).isEqualTo(result.size());
    }
    
    @Test
    public void 직원_부서_검색() throws Exception {
        //given
        Department department = Department.builder()
                .deptName("인사과")
                .build();
        Department department2 = Department.builder()
                .deptName("인사과2")
                .build();
        for (int i = 0; i < 3; i++) {
            Employee employee = Employee.builder()
                    .department(department)
                    .build();
            Employee employee2 = Employee.builder()
                    .department(department2)
                    .build();
            employeeRepository.save(employee);
            employeeRepository.save(employee2);
        }
        //when
        List<EmployeeDTO.Index> result = employeeService.viewAllByDept("인사과", new PageRequestDTO(1));
        //then
        assertThat(result).extracting("deptName")
                .containsOnly("인사과");
        assertThat(result.size()).isEqualTo(3);
    }

    @Test
    public void 사원_등록() throws Exception {
        //given
        Department department = Department.builder()
                .deptName("인사과")
                .build();
        departmentRepository.save(department);
        //when
        employeeService.createEmployee(EmployeeDTO.AddForm.builder()
                .empName("테스터")
                .deptId(department.getDeptId())
                .build());
        //then
        assertThat(employeeRepository.findAllByEmpName("테스터", new PageRequestDTO(1))).isNotEmpty();
    }
    
    @Test
    public void 사원_정보_변경() throws Exception {
        //given
        Employee employee = Employee.builder()
                .empName("테스터")
                .empPhoneNum("010-1234-5678")
                .empJoinDate(LocalDate.now().minusDays(1))
                .build();
        employeeRepository.save(employee);
        //when, then
        assertThat("010-1234-5678").isEqualTo(employee.getEmpPhoneNum());
        assertThat(LocalDate.now().minusDays(1)).isEqualTo(employee.getEmpJoinDate());
        employeeService.updateEmployee(
                EmployeeDTO.UpdateForm.builder()
                        .empId(employee.getEmpId())
                        .empPhoneNum("010-5678-1234")
                        .empJoinDate(LocalDate.now())
                        .build()
        );
        assertThat("010-5678-1234").as("핸드폰 번호가 맞지 않습니다")
                .isEqualTo(employee.getEmpPhoneNum());
        assertThat(LocalDate.now()).as("입사 날짜가 맞지 않습니다")
                .isEqualTo(employee.getEmpJoinDate());
    }
}