package com.laptop.Laptop.controller;

import com.laptop.Laptop.dto.DashboardDTO;
import com.laptop.Laptop.services.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDTO> getDashboard(Pageable pageable) {
        DashboardDTO dashboardData = dashboardService.getDashboardData(pageable);
        return ResponseEntity.ok(dashboardData);
    }

}
