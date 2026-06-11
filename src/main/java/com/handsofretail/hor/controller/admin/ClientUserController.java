package com.handsofretail.hor.controller.admin;

import com.handsofretail.hor.dto.request.ClientStatusRequest;
import com.handsofretail.hor.dto.request.ClientUserRequest;
import com.handsofretail.hor.dto.request.ClientUserUpdateRequest;
import com.handsofretail.hor.dto.response.ApiResponse;
import com.handsofretail.hor.dto.response.ClientUserResponse;
import com.handsofretail.hor.service.ClientUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(
            summary = "Create client user",
            description = "Creates a client user account. Email must be globally unique across admin_users and client_users.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Client created"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation failed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Email already exists")
    })
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

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update client status", description = "Activate or deactivate a client account.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Status updated"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Client not found")
    })
    public ResponseEntity<ApiResponse<ClientUserResponse>> updateClientStatus(
            @PathVariable Long id,
            @Valid @RequestBody ClientStatusRequest request) {

        ClientUserResponse response = clientUserService.updateClientStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success("Client status updated", response));
    }

}
