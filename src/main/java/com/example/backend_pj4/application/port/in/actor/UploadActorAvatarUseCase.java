package com.example.backend_pj4.application.port.in.actor;

import java.io.InputStream;

import com.example.backend_pj4.application.dto.actor.ActorResult;

public interface UploadActorAvatarUseCase {
    ActorResult execute(String actorId, String filename, InputStream data, long size, String contentType);
}
