package com.tsb.controller;

import com.tsb.dto.AddressRequest;
import com.tsb.model.Address;
import com.tsb.service.AddressService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {
    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping
    public ResponseEntity<List<Address>> list(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(addressService.list(user.getUsername()));
    }

    @PostMapping
    public ResponseEntity<Address> create(@AuthenticationPrincipal UserDetails user,
                                          @RequestBody AddressRequest request) {
        return ResponseEntity.ok(addressService.create(user.getUsername(), request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Address> update(@AuthenticationPrincipal UserDetails user,
                                          @PathVariable Long id,
                                          @RequestBody AddressRequest request) {
        return ResponseEntity.ok(addressService.update(user.getUsername(), id, request));
    }

    @PutMapping("/{id}/default")
    public ResponseEntity<Address> setDefault(@AuthenticationPrincipal UserDetails user,
                                              @PathVariable Long id) {
        return ResponseEntity.ok(addressService.setDefault(user.getUsername(), id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal UserDetails user,
                                       @PathVariable Long id) {
        addressService.delete(user.getUsername(), id);
        return ResponseEntity.noContent().build();
    }
}
