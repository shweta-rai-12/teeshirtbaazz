package com.tsb.controller;

import com.tsb.dto.ReturnRequestDto;
import com.tsb.model.ReturnRequest;
import com.tsb.service.ReturnService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/returns")
public class ReturnController {

}
