package org.adarssh.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

/**
 * Health check controller.
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Health", description = "Health check endpoints")
public class HealthController {

    /**
     * Basic health check endpoint.
     *
     * @return Health status
     */
    @Operation(
        summary = "Health check",
        description = "Returns the health status of the API. Does not require authentication."
    )
    @ApiResponse(
        responseCode = "200",
        description = "API is healthy",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = HealthResponse.class)
        )
    )
    @GetMapping("/health")
    public ResponseEntity<HealthResponse> health() {
        return ResponseEntity.ok(new HealthResponse("UP", Instant.now()));
    }

    /**
     * Health response DTO.
     */
    @Schema(description = "Health check response")
    public record HealthResponse(
        @Schema(description = "Health status", example = "UP") String status,
        @Schema(description = "Current timestamp") Instant timestamp
    ) { }
}
