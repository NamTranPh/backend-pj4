package com.example.backend_pj4.presentation.actor;

import java.io.IOException;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.backend_pj4.application.command.actor.CreateActorCommand;
import com.example.backend_pj4.application.command.actor.UpdateActorCommand;
import com.example.backend_pj4.application.dto.actor.ActorResult;
import com.example.backend_pj4.application.port.in.actor.CreateActorUseCase;
import com.example.backend_pj4.application.port.in.actor.DeleteActorUseCase;
import com.example.backend_pj4.application.port.in.actor.GetActorByIdUseCase;
import com.example.backend_pj4.application.port.in.actor.ListActorsQuery;
import com.example.backend_pj4.application.port.in.actor.UpdateActorUseCase;
import com.example.backend_pj4.application.port.in.actor.UploadActorAvatarUseCase;
import com.example.backend_pj4.common.annotation.AuthRequired;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.repository.filter.ActorFilter;
import com.example.backend_pj4.presentation.actor.request.CreateActorRequest;
import com.example.backend_pj4.presentation.actor.request.RequestAdminActorDto;
import com.example.backend_pj4.presentation.actor.request.UpdateActorRequest;
import com.example.backend_pj4.presentation.common.request.PageableFactory;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Actors - Admin")
@AuthRequired
@RestController
@RequestMapping("/api/v1/admin/actors")
public class AdminActorController {

    private final CreateActorUseCase createActorUseCase;
    private final UpdateActorUseCase updateActorUseCase;
    private final GetActorByIdUseCase getActorByIdUseCase;
    private final ListActorsQuery listActorsQuery;
    private final DeleteActorUseCase deleteActorUseCase;
    private final UploadActorAvatarUseCase uploadActorAvatarUseCase;

    public AdminActorController(
            CreateActorUseCase createActorUseCase,
            UpdateActorUseCase updateActorUseCase,
            GetActorByIdUseCase getActorByIdUseCase,
            ListActorsQuery listActorsQuery,
            DeleteActorUseCase deleteActorUseCase,
            UploadActorAvatarUseCase uploadActorAvatarUseCase
    ) {
        this.createActorUseCase = createActorUseCase;
        this.updateActorUseCase = updateActorUseCase;
        this.getActorByIdUseCase = getActorByIdUseCase;
        this.listActorsQuery = listActorsQuery;
        this.deleteActorUseCase = deleteActorUseCase;
        this.uploadActorAvatarUseCase = uploadActorAvatarUseCase;
    }

    @Operation(summary = "Tạo mới diễn viên. Quyền truy cập: ADMIN.")
    @PostMapping
    public ResponseEntity<ActorResult> create(@Valid @RequestBody CreateActorRequest request) {
        ActorResult result = createActorUseCase.execute(new CreateActorCommand(
                request.name(),
                request.originalName(),
                request.avatarUrl(),
                request.bio(),
                request.birthDate(),
                request.nationality()
        ));
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(summary = "Cập nhật thông tin diễn viên theo ID. Quyền truy cập: ADMIN.")
    @PatchMapping("/{id}")
    public ResponseEntity<ActorResult> update(
            @PathVariable String id,
            @Valid @RequestBody UpdateActorRequest request
    ) {
        ActorResult result = updateActorUseCase.execute(id, new UpdateActorCommand(
                request.name(),
                request.originalName(),
                request.avatarUrl(),
                request.bio(),
                request.birthDate(),
                request.nationality()
        ));
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Lấy chi tiết diễn viên theo ID. Quyền truy cập: ADMIN.")
    @GetMapping("/{id}")
    public ResponseEntity<ActorResult> getById(@PathVariable String id) {
        return ResponseEntity.ok(getActorByIdUseCase.execute(id));
    }

    @Operation(summary = "Lấy danh sách tất cả diễn viên cho quản trị viên. Quyền truy cập: ADMIN.")
    @GetMapping
    public PageResult<ActorResult> list(@ParameterObject @ModelAttribute RequestAdminActorDto params) {
        return listActorsQuery.execute(
                new ActorFilter(params.search(), params.getNationality()),
                PageableFactory.create(params, ListActorsQuery.SORTABLE, ListActorsQuery.DEFAULT_SORT)
        );
    }

    @Operation(summary = "Xóa mềm diễn viên theo ID. Quyền truy cập: ADMIN.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        deleteActorUseCase.execute(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Upload ảnh đại diện avatar cho diễn viên. Quyền truy cập: ADMIN.")
    @PostMapping("/{id}/avatar")
    public ResponseEntity<ActorResult> uploadAvatar(
            @PathVariable String id,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        ActorResult result = uploadActorAvatarUseCase.execute(
                id,
                file.getOriginalFilename(),
                file.getInputStream(),
                file.getSize(),
                file.getContentType()
        );
        return ResponseEntity.ok(result);
    }
}
