package com.handsofretail.hor.controller.admin;

import com.handsofretail.hor.dto.request.ClientUserRequest;
import com.handsofretail.hor.dto.request.ClientUserUpdateRequest;
import com.handsofretail.hor.dto.response.ApiResponse;
import com.handsofretail.hor.dto.response.ClientUserResponse;
import com.handsofretail.hor.service.ClientUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/clients")
@RequiredArgsConstructor
public class ClientUserController {

    private final ClientUserService clientUserService;

    @PostMapping
    public ResponseEntity<ApiResponse<ClientUserResponse>> createClient(
            @Valid @RequestBody ClientUserRequest request) {

        ClientUserResponse response = clientUserService.createClient(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Client created", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ClientUserResponse>>> getAllClients() {

        List<ClientUserResponse> response = clientUserService.getAllClients();
        return ResponseEntity.ok(
                ApiResponse.success("Clients fetched", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ClientUserResponse>> updateClient(
            @PathVariable Long id,
            @Valid @RequestBody ClientUserUpdateRequest request) {

        ClientUserResponse response = clientUserService.updateClient(id, request);
        return ResponseEntity.ok(ApiResponse.success("Client updated", response));
    }

}