package org.weare4saken.controller;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.weare4saken.model.User;

@OpenAPIDefinition(
        info = @Info(
                title = "OpenApi specification for Logger-starter-system",
                version = "1.0",
                contact = @Contact(
                        name = "Ilya Kondratyuk",
                        url = "https://github.com/weare4saken",
                        email = "kondrashka91@gmail.com")),
        tags = @Tag(name = "Example Controller"))
@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
public class ExampleController {

    @Operation(summary = "Stub endpoint to greet a User by a name",
            description = "Greets a User by sending any name as a parameter",
            tags = "Example Controller",
            responses = @ApiResponse(
                    responseCode = "200",
                    content = @Content(examples = @ExampleObject("Hello, User!"))
            )
    )
    @GetMapping(path = "/greet", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<?> greet(@RequestParam(defaultValue = "User") String name) {
        return ResponseEntity.ok("Hello, " + name + "!");
    }

    @Operation(summary = "Stub endpoint to receive a User",
            description = "Receives a User and just sends it back to a client",
            tags = "Example Controller",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            schema = @Schema(implementation = User.class),
                            examples = @ExampleObject(value = "{\"firstName\": \"Mike\", \"lastName\": \"Johnson\"}")
                    )
            ),
            responses = @ApiResponse(
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = User.class))
            )
    )
    @PostMapping(path = "/user",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> user(@RequestBody User user) {
        String firstName = user.getFirstName();
        String lastName = user.getLastName();

        if (firstName == null || lastName == null || firstName.isBlank() || lastName.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Invalid user: either firstName or lastName is empty");
        }

        return ResponseEntity.ok(user);
    }
}