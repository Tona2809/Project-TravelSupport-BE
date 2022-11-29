package com.hcmute.hotel.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@ComponentScan
@RestController
@RequestMapping("/api/authenticate")
@RequiredArgsConstructor
public class AdminController {
}
