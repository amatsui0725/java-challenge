package jp.co.axa.apidemo.controllers;

import jp.co.axa.apidemo.entities.Employee;
import jp.co.axa.apidemo.services.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    public void setEmployeeService(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/employees")
    public List<Employee> getEmployees() {
        List<Employee> employees = employeeService.retrieveEmployees();
        return employees;
    }

    @GetMapping("/employees/{employeeId}")
    public ResponseEntity<Employee> getEmployee(@PathVariable(name = "employeeId") Long employeeId) {
        Optional<Employee> emp = employeeService.getEmployee(employeeId);
        return emp.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/employees")
    public ResponseEntity<Employee> saveEmployee(Employee employee) {
        return ResponseEntity.ok(employeeService.saveEmployee(employee));
    }

    @DeleteMapping("/employees/{employeeId}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable(name = "employeeId") Long employeeId) {
        Optional<Employee> emp = employeeService.getEmployee(employeeId);
        if (emp.isPresent()) {
            employeeService.deleteEmployee(employeeId);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/employees/{employeeId}")
    public ResponseEntity<Employee> updateEmployee(@RequestBody Employee employee,
            @PathVariable(name = "employeeId") Long employeeId) {
        Optional<Employee> emp = employeeService.getEmployee(employeeId);
        if (emp.isPresent()) {
            Employee updatedEmp = emp.get();
            updatedEmp.setName(employee.getName());
            updatedEmp.setDepartment(employee.getDepartment());
            updatedEmp.setSalary(employee.getSalary());
            return ResponseEntity.ok(employeeService.updateEmployee(updatedEmp));

        }
        return ResponseEntity.notFound().build();

    }

}
