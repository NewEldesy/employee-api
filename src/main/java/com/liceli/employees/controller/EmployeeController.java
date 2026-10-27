
package com.liceli.employees.controller;

import com.liceli.employees.dto.EmployeeRequest;
import com.liceli.employees.dto.EmployeeResponse;
import com.liceli.employees.service.EmployeeService;

import com.liceli.employees.exception.GlobalExceptionHandler.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/employees")
@Tag(name = "Employees", description = "API de gestion des employés")
@SecurityRequirement(name = "bearerAuth")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }


    @Operation(
        summary = "Création d'un employé",
        description = "Enregistre un nouvel employé dans la base de données.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Informations du nouvel employé",
            required = true,
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Ajout d'un employé",
                    value = """
                        {
                        "firstName": "Amina",
                        "lastName": "Kabore",
                        "email": "amina.kabore@example.com",
                        "department": "Ressources Humaines"
                        }
                        """
                )
            )
        )
    )
    @ApiResponse(responseCode = "201", description = "Employé créé avec succès")
    @ApiResponse(
        responseCode = "400",
        description = "Données invalides : les champs ne respectent pas les règles de validation.",
        content = @Content(
            mediaType = "application/json",
            array = @ArraySchema(
                schema = @Schema(
                    type = "string",
                    example = "email: doit être une adresse email valide"
                )
            ),
            examples = @ExampleObject(
                name = "Erreur de validation",
                value = """
                    [
                    "email: doit être une adresse email valide",
                    "firstName: ne doit pas être vide"
                    ]
                    """
            )
        )
    )
    @PostMapping
    public ResponseEntity<EmployeeResponse> create(
            @Valid @RequestBody EmployeeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(employeeService.create(request));
    }

    @Operation(
        summary = "Lister tous les employés",
        description = "Affiche la liste de tous les employés."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Liste des employés récupérée avec succès",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(
                implementation = EmployeeResponse.class,
                type = "array"
            ),
            examples = @ExampleObject(
                name = "Liste des employés",
                value = """
                    [
                    {
                        "id": 1,
                        "firstName": "Amina",
                        "lastName": "Kabore",
                        "email": "amina.kabore@example.com",
                        "department": "Ressources Humaines"
                    },
                    {
                        "id": 2,
                        "firstName": "Paul",
                        "lastName": "Traore",
                        "email": "paul.traore@example.com",
                        "department": "Informatique"
                    }
                    ]
                    """
            )
        )
    )
    @GetMapping
    public List<EmployeeResponse> findAll() {
        return employeeService.findAll();
    }

    @Operation(
        summary = "Afficher un employé",
        description = "Recherche un employé à partir de son identifiant."
    )
    @ApiResponse(responseCode = "200", description = "Employé trouvé")
    @ApiResponse(responseCode = "404", description = "Employé introuvable",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = @ExampleObject(value = """
                {"message": "Employé introuvable : id=42"}
                """)))
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> findById(
            @PathVariable Long id) {
        return ResponseEntity.ok(employeeService.findById(id));
    }

    @Operation(
        summary = "Modifier un employé",
        description = "Modifier les informations d'un employé existant à partir de son identifiant.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Nouvelles informations de l'employé",
            required = true,
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Modification d'un employé",
                    value = """
                        {
                        "firstName": "Amina",
                        "lastName": "Kabore",
                        "email": "amina.kabore@example.com",
                        "department": "Informatique"
                        }
                        """
                )
            )
        )
    )
    @ApiResponse(responseCode = "200", description = "Employé modifié avec succès")
    @ApiResponse(
        responseCode = "400",
        description = "Données invalides",
        content = @Content(
            mediaType = "application/json",
            array = @io.swagger.v3.oas.annotations.media.ArraySchema(
                schema = @Schema(type = "string")
            ),
            examples = @ExampleObject(
                name = "Erreur de validation",
                value = """
                    [
                    "firstName: Le prénom est obligatoire",
                    "email: L'adresse e-mail est invalide"
                    ]
                    """
            )
        )
    )
    @ApiResponse(responseCode = "404", description = "Employé introuvable",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = @ExampleObject(value = """
                {"message": "Employé introuvable : id=42"}
                """)))
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequest request) {
        return ResponseEntity.ok(employeeService.update(id, request));
    }

    @Operation(
        summary = "Supprimer un employé",
        description = "Supprime un employé à partir de son identifiant."
    )
    @ApiResponse(responseCode = "204", description = "Employé supprimé avec succès")
    @ApiResponse(responseCode = "404", description = "Employé introuvable",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = @ExampleObject(value = """
                {"message": "Employé introuvable : id=42"}
                """)))
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {
        employeeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
