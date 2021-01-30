package team.okky.personnel_management.attendance;

import lombok.*;
import team.okky.personnel_management.employee.Employee;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.Locale;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Attendance {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "att_id")
    private Long attID;

    private LocalDate attDate;
    private LocalTime attOnTime;
    private LocalTime attOffTime;
    @Enumerated(EnumType.STRING)
    private AttendanceStatus attStatus;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name="emp_id")
    private Employee employee;


    public void changeAttOnTime(LocalTime attOnTime) {
        this.attOnTime = attOnTime;
    }

    public void changeAttOffTime(LocalTime attOffTime) {this.attOffTime = attOffTime; }

    public void changeAttStatus(AttendanceStatus attStatus) {
        this.attStatus = attStatus;
    }

    @Builder
    public Attendance(LocalDate attDate, LocalTime attOnTime, LocalTime attOffTime, AttendanceStatus attStatus, Employee employee){
        this.attDate = attDate;
        this.attOnTime = attOnTime;
        this.attOffTime = attOffTime;
        this.attStatus = attStatus;
        this.employee = employee;
    }

    public AttendanceDTO.Index entityToIndex(){
        return AttendanceDTO.Index.builder()
                .attDate(getAttDate())
                .dayOfWeek(getAttDate().getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN))
                .empName(getEmployee().getEmpName())
                .deptName(getEmployee().getDepartment().getDeptName())
                .empPosition(getEmployee().getEmpPosition().getPosition())
                .attOnTime(getAttOnTime())
                .attOffTime(getAttOffTime())
                .attStatus(getAttStatus())
                .build();
    }

}
