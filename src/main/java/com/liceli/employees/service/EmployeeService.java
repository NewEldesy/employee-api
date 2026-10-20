
package com.liceli.employees.service;

import com.liceli.employees.model.Employee;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class EmployeeService {

    private final ConcurrentMap<Long, Employee> employees =
            new ConcurrentHashMap<>();

    private final AtomicLong nextId = new AtomicLong(1);

    public Employee create(Employee employee) {
        Long id = nextId.getAndIncrement();
        employee.setId(id);
        employees.put(id, employee);
        return employee;
    }

    public List<Employee> findAll() {
        return new ArrayList<>(employees.values());
    }

    public Employee findById(Long id) {
        return employees.get(id);
    }

    public Employee update(Long id, Employee updatedEmployee) {
        if (!employees.containsKey(id)) {
            return null;
        }

        updatedEmployee.setId(id);
        employees.put(id, updatedEmployee);
        return updatedEmployee;
    }

    public boolean delete(Long id) {
        return employees.remove(id) != null;
    }
}