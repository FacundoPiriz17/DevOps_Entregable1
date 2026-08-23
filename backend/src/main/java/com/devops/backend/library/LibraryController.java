package com.devops.backend.library;

import com.devops.backend.common.CurrentUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/library")
public class LibraryController {

    private final LibraryService libraryService;
    private final CurrentUser currentUser;

    public LibraryController(LibraryService libraryService, CurrentUser currentUser) {
        this.libraryService = libraryService;
        this.currentUser = currentUser;
    }

    @PostMapping("/games/{gameId}")
    public ResponseEntity<LibraryEntryResponse> addGame(@PathVariable Long gameId, JwtAuthenticationToken auth) {
        LibraryEntryResponse response = libraryService.addToLibrary(currentUser.idFrom(auth), gameId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public List<LibraryEntryResponse> listLibrary(JwtAuthenticationToken auth) {
        return libraryService.listLibrary(currentUser.idFrom(auth));
    }
}
