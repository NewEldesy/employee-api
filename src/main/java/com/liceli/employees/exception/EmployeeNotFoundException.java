package com.liceli.employees.exception;

public class EmployeeNotFoundException extends RuntimeException {

    public EmployeeNotFoundException(Long id) {
        super("Employé introuvable : id=" + id);
    }
}
