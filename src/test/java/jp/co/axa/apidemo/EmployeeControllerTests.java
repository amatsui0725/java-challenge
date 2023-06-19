package jp.co.axa.apidemo;

import com.fasterxml.jackson.databind.ObjectMapper;
import jp.co.axa.apidemo.entities.Employee;
import jp.co.axa.apidemo.services.EmployeeService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.Base64Utils;
import org.springframework.http.HttpHeaders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest
public class EmployeeControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    // JUnit test for POST /api/vi/employees
    @Test
    public void givenEmployeeObject_whenCreateEmployee_thenReturnSavedEmployee() throws Exception {

        // given - precondition or setup
        Employee employee = Employee.builder().name("Akira").department("Development").salary(20000).build();

        given(employeeService.saveEmployee(any(Employee.class)))
                .willAnswer((invocation) -> invocation.getArgument(0));

        // when - action or behaviour that we are going test
        ResultActions response = mockMvc.perform(post("/api/v1/employees")
                .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64Utils.encodeToString("akira:password".getBytes()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employee)));

        // then - verify the result or output using assert statements
        response.andDo(print()).andExpect(status().isCreated())
                .andExpect(jsonPath("$.name",
                        is(employee.getName())))
                .andExpect(jsonPath("$.department",
                        is(employee.getDepartment())))
                .andExpect(jsonPath("$.salary",
                        is(employee.getSalary())));

    }

    // JUnit test for GET /api/vi/employees
    @Test
    public void givenListOfEmployees_whenGetAllEmployees_thenReturnEmployeesList() throws Exception {
        // given - precondition or setup
        List<Employee> listOfEmployees = new ArrayList<>();
        listOfEmployees
                .add(Employee.builder().name("Test1").department("Customer Service").salary(10000)
                        .build());
        listOfEmployees.add(Employee.builder().name("Test2").department("HR").salary(15000).build());
        given(employeeService.retrieveEmployees()).willReturn(listOfEmployees);

        // when - action or the behaviour that we are going test
        ResultActions response = mockMvc.perform(get("/api/v1/employees").header(HttpHeaders.AUTHORIZATION,
                "Basic " + Base64Utils.encodeToString("akira:password".getBytes())));

        // then - verify the output
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()",
                        is(listOfEmployees.size())));

    }

    // JUnit test for valid GET /api/v1/employees/employeeId
    @Test
    public void givenEmployeeId_whenGetEmployeeById_thenReturnEmployeeObject() throws Exception {
        // given - precondition or setup
        long employeeId = 1L;
        Employee employee = Employee.builder().name("Akira").department("Development").salary(20000).build();
        given(employeeService.getEmployee(employeeId)).willReturn(Optional.of(employee));

        // when - action or the behaviour that we are going test
        ResultActions response = mockMvc
                .perform(get("/api/v1/employees/{id}", employeeId).header(HttpHeaders.AUTHORIZATION,
                        "Basic " + Base64Utils.encodeToString("akira:password".getBytes())));

        // then - verify the output
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.name", is(employee.getName())))
                .andExpect(jsonPath("$.department", is(employee.getDepartment())))
                .andExpect(jsonPath("$.salary", is(employee.getSalary())));

    }

    // JUnit test for invalid GET /api/v1/employees/employeeId
    @Test
    public void givenInvalidEmployeeId_whenGetEmployeeById_thenReturnEmpty() throws Exception {
        // given - precondition or setup
        long employeeId = 1L;
        given(employeeService.getEmployee(employeeId)).willReturn(Optional.empty());

        // when - action or the behaviour that we are going test
        ResultActions response = mockMvc
                .perform(get("/api/v1/employees/{id}", employeeId).header(HttpHeaders.AUTHORIZATION,
                        "Basic " + Base64Utils.encodeToString("akira:password".getBytes())));

        // then - verify the output
        response.andExpect(status().isNotFound())
                .andDo(print());

    }

    // JUnit test for valid PUT /api/v1/employees/employeeId
    @Test
    public void givenUpdatedEmployee_whenUpdateEmployee_thenReturnUpdateEmployeeObject() throws Exception {
        // given - precondition or setup
        long employeeId = 1L;
        Employee savedEmployee = Employee.builder()
                .name("Akira")
                .department("Development")
                .salary(10000)
                .build();

        Employee updatedEmployee = Employee.builder()
                .name("Akira")
                .department("Development")
                .salary(20000)
                .build();
        given(employeeService.getEmployee(employeeId)).willReturn(Optional.of(savedEmployee));
        given(employeeService.updateEmployee(any(Employee.class)))
                .willAnswer((invocation) -> invocation.getArgument(0));

        // when - action or the behaviour that we are going test
        ResultActions response = mockMvc.perform(put("/api/v1/employees/{id}", employeeId)
                .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64Utils.encodeToString("akira:password".getBytes()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedEmployee)));

        // then - verify the output
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.name", is(updatedEmployee.getName())))
                .andExpect(jsonPath("$.department", is(updatedEmployee.getDepartment())))
                .andExpect(jsonPath("$.salary", is(updatedEmployee.getSalary())));
    }

    // JUnit test for invalid PUT /api/v1/employees/employeeId
    @Test
    public void givenUpdatedEmployee_whenUpdateEmployee_thenReturn404() throws Exception {
        // given - precondition or setup
        long employeeId = 1L;
        Employee updatedEmployee = Employee.builder()
                .name("Akira")
                .department("Development")
                .salary(20000)
                .build();
        given(employeeService.getEmployee(employeeId)).willReturn(Optional.empty());
        given(employeeService.updateEmployee(any(Employee.class)))
                .willAnswer((invocation) -> invocation.getArgument(0));

        // when - action or the behaviour that we are going test
        ResultActions response = mockMvc.perform(put("/api/v1/employees/{id}", employeeId)
                .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64Utils.encodeToString("akira:password".getBytes()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedEmployee)));

        // then - verify the output
        response.andExpect(status().isNotFound())
                .andDo(print());
    }

    // JUnit test for DELETE /api/v1/employees/employeeId
    @Test
    public void givenEmployeeId_whenDeleteEmployee_thenReturn200() throws Exception {
        // given - precondition or setup
        long employeeId = 1L;
        Employee savedEmployee = Employee.builder()
                .name("Akira")
                .department("Development")
                .salary(10000)
                .build();
        given(employeeService.getEmployee(employeeId)).willReturn(Optional.of(savedEmployee));
        willDoNothing().given(employeeService).deleteEmployee(employeeId);

        // when - action or the behaviour that we are going test
        ResultActions response = mockMvc
                .perform(delete("/api/v1/employees/{id}", employeeId).header(HttpHeaders.AUTHORIZATION,
                        "Basic " + Base64Utils.encodeToString("akira:password".getBytes())));

        // then - verify the output
        response.andExpect(status().is2xxSuccessful())
                .andDo(print());
    }
}
