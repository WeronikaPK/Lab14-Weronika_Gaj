package edu_gaj.pasir_gaj_weronika.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class InfoController {

    @GetMapping("/api/info")
    public Map<String, String> info() {
        Map<String, String> data = new LinkedHashMap<>();

        data.put("appName", "Aplikacja Budżetowa");
        data.put("version", "1.0");
        data.put("message", "Witaj w aplikacji budżetowej stworzonej ze Spring Boot!");
        data.put("author", "Weronika Gaj");
        data.put("field", "Geoinformatyka II rok");
        data.put("subject", "Programowanie - Java");

        return data;
    }
}