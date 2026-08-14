package com.example.backend_pj4.presentation.user.request;

import jakarta.validation.constraints.NotNull;

public record ToggleBanRequest(@NotNull Boolean banned) {}
