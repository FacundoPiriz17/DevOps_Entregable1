package com.devops.backend.modules.library.controller;

import com.devops.backend.common.config.OpenApiConfig;
import com.devops.backend.common.security.CurrentUser;
import com.devops.backend.modules.library.dto.LibraryEntryResponse;
import com.devops.backend.modules.library.dto.FavoriteRequest;
import com.devops.backend.modules.library.service.LibraryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/library")
@Tag(name = "Library", description = "Gestión de la biblioteca personal")
@SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
public class LibraryController {

    private final LibraryService libraryService;
    private final CurrentUser currentUser;

    public LibraryController(LibraryService libraryService, CurrentUser currentUser) {
        this.libraryService = libraryService;
        this.currentUser = currentUser;
    }

    @PostMapping("/games/{gameId}")
    public ResponseEntity<LibraryEntryResponse> addGame(@PathVariable Long gameId, JwtAuthenticationToken auth) {
        LibraryEntryResponse response = libraryService.addToLibrary(currentUser.emailFrom(auth), gameId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public List<LibraryEntryResponse> listLibrary(JwtAuthenticationToken auth) {
        return libraryService.listLibrary(currentUser.emailFrom(auth));
    }

    @PatchMapping("/games/{gameId}/favorite")
    public LibraryEntryResponse setFavorite(@PathVariable Long gameId,
                                            @Valid @RequestBody FavoriteRequest request,
                                            JwtAuthenticationToken auth) {
        return libraryService.setFavorite(currentUser.emailFrom(auth), gameId, request.favorite());
    }
}
