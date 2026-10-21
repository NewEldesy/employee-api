
package com.liceli.employees.service;

import com.liceli.employees.model.Employee;
import com.liceli.employees.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Employee create(Employee employee) {
        // L'identifiant est généré par la base de données.
        employee.setId(null);
        return employeeRepository.save(employee);
    }

    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    public Employee findById(Long id) {
        return employeeRepository.findById(id).orElse(null);
    }

    public Employee update(Long id, Employee updatedEmployee) {
        return employeeRepository.findById(id)
                .map(existingEmployee -> {
                    existingEmployee.setFirstName(updatedEmployee.getFirstName());
                    existingEmployee.setLastName(updatedEmployee.getLastName());
                    existingEmployee.setEmail(updatedEmployee.getEmail());
                    existingEmployee.setDepartment(updatedEmployee.getDepartment());

                    return employeeRepository.save(existingEmployee);
                })
                .orElse(null);
    }

    public boolean delete(Long id) {
        if (!employeeRepository.existsById(id)) {
            return false;
        }

        employeeRepository.deleteById(id);
        return true;
    }
}
