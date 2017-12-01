package com.example.kalah.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("")
public class RootController {

    @GetMapping("")
    public ResponseEntity<String> ping() {
        final ResponseEntity<String> result;
        final URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/kalah")
                .build()
                .toUri();
        final String text = "Welcome!" +
                " Try POST request at " + location + " to create a new game." +
                "  " + LocalTime.now().format(DateTimeFormatter.ISO_TIME);
        result = ResponseEntity.ok()
                               .location(location)
                               .body(text);
        return result;
    }

}
