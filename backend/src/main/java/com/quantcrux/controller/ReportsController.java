package com.quantcrux.controller;

import com.quantcrux.dto.ReportRequest;
import com.quantcrux.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;
import java.util.HashMap;

// @CrossOrigin(origins = "http://localhost:3000")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"}, maxAge = 3600)
@RestController
@RequestMapping("/api/reports")
public class ReportsController {

    @Autowired
    private ReportService reportService;

    @PostMapping("/generate")
    public ResponseEntity<?> generateReport(@Valid @RequestBody ReportRequest request, Authentication authentication) {
        String reportId = reportService.generateReport(request, authentication.getName());
        
        Map<String, String> response = new HashMap<>();
        response.put("reportId", reportId);
        response.put("status", "GENERATED");
        response.put("message", "Report generated successfully");
        
        return ResponseEntity.ok(reportId);
    }
}