package com.liceli.employees.service;

import com.liceli.employees.dto.EmployeeRequest;
import com.liceli.employees.dto.EmployeeResponse;
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

    public EmployeeResponse create(EmployeeRequest request) {
        Employee employee = toEntity(request);
        return toResponse(employeeRepository.save(employee));
    }

    public List<EmployeeResponse> findAll() {
        return employeeRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public EmployeeResponse findById(Long id) {
        return employeeRepository.findById(id)
                .map(this::toResponse)
                .orElse(null);
    }

    public EmployeeResponse update(Long id, EmployeeRequest request) {
        return employeeRepository.findById(id)
                .map(existing -> {
                    existing.setFirstName(request.getFirstName());
                    existing.setLastName(request.getLastName());
                    existing.setEmail(request.getEmail());
                    existing.setDepartment(request.getDepartment());
                    return toResponse(employeeRepository.save(existing));
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

    private Employee toEntity(EmployeeRequest request) {
        Employee e = new Employee();
        e.setFirstName(request.getFirstName());
        e.setLastName(request.getLastName());
        e.setEmail(request.getEmail());
        e.setDepartment(request.getDepartment());
        return e;
    }

    private EmployeeResponse toResponse(Employee e) {
        return new EmployeeResponse(
                e.getId(),
                e.getFirstName(),
                e.getLastName(),
                e.getEmail(),
                e.getDepartment()
        );
    }
}
