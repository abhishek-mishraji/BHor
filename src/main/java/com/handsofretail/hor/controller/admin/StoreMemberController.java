package com.handsofretail.hor.controller.admin;

import com.handsofretail.hor.dto.request.StoreMemberRequest;
import com.handsofretail.hor.dto.response.ApiResponse;
import com.handsofretail.hor.dto.response.StoreMemberResponse;
import com.handsofretail.hor.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Store Members", description = "Admin endpoints for managing client memberships within stores")
@RestController
@RequestMapping("/api/v1/admin/store-members")
@RequiredArgsConstructor
public class StoreMemberController {

    private final StoreService storeService;

    // -------------------------------------------------------------------------
    // GET /api/v1/admin/store-members?storeId={id}
    // -------------------------------------------------------------------------

    @Operation(
            summary = "Get all members of a store",
            description = "Returns all clients (OWNER and PARTNERs) assigned to the given store."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Members fetched successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "Store not found",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403", description = "Forbidden — ADMIN role required")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<StoreMemberResponse>>> getStoreMembers(
            @Parameter(description = "ID of the store", required = true, example = "1")
            @RequestParam Long storeId) {

        List<StoreMemberResponse> members = storeService.getStoreMembers(storeId);
        return ResponseEntity.ok(ApiResponse.success("Store members fetched", members));
    }

    // -------------------------------------------------------------------------
    // POST /api/v1/admin/store-members
    // -------------------------------------------------------------------------

    @Operation(
            summary = "Add a client to a store",
            description = """
                    Assigns a client to a store with the specified role.

                    Business rules:
                    - Store and client must exist.
                    - Each (client_id, store_id) pair must be unique.
                    - A store may have at most one OWNER.
                    - Multiple PARTNERs are allowed.
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201", description = "Member added successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = "Store already has an owner",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "Store or client not found",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409", description = "Client is already assigned to this store",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403", description = "Forbidden — ADMIN role required")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<StoreMemberResponse>> addStoreMember(
            @Valid @RequestBody StoreMemberRequest request) {

        StoreMemberResponse response = storeService.addStoreMember(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Store member added successfully", response));
    }

    // -------------------------------------------------------------------------
    // DELETE /api/v1/admin/store-members/{storeId}/{clientId}
    // -------------------------------------------------------------------------

    @Operation(
            summary = "Remove a client from a store",
            description = "Removes the mapping between a client and a store. The OWNER of a store cannot be removed — reassign ownership first."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Member removed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = "Cannot remove the store OWNER",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "Store or membership not found",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403", description = "Forbidden — ADMIN role required")
    })
    @DeleteMapping("/{storeId}/{clientId}")
    public ResponseEntity<ApiResponse<Void>> removeStoreMember(
            @Parameter(description = "ID of the store", required = true, example = "1")
            @PathVariable Long storeId,
            @Parameter(description = "ID of the client to remove", required = true, example = "3")
            @PathVariable Long clientId) {

        storeService.removeStoreMember(storeId, clientId);
        return ResponseEntity.ok(ApiResponse.success("Store member removed successfully", null));
    }
}
