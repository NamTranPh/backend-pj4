package com.example.backend_pj4.presentation.common.request;

/**
 * Tham số phân trang dùng chung cho mọi API lấy danh sách (Query Parameter DTO).
 *
 * <p>Controller bind bằng {@code @ModelAttribute} nên chữ ký chỉ còn một tham số thay vì năm
 * dòng {@code @RequestParam} lặp ở từng endpoint.
 *
 * <p>Mọi trường để kiểu bọc (nullable) thay vì kiểu nguyên thuỷ: cần phân biệt "client không
 * truyền" với "client truyền 0". Giá trị mặc định do {@link PageableFactory} áp, không đặt ở
 * đây, để chỉ có một chỗ duy nhất biết luật.
 *
 * @param page    đếm từ 1 (null → 1). Spring Data đếm từ 0, factory lo việc trừ đi
 * @param limit   số dòng mỗi trang (null → 15, trần 50)
 * @param search  từ khoá tìm kiếm (null → không lọc). Field nào được tìm do từng module quyết định
 * @param sortBy  tên field sắp xếp (null → field mặc định của module). Phải nằm trong whitelist
 * @param sortDir "asc" hoặc "desc" (null → desc)
 */
public record PageQueryParamDto(
        Integer page,
        Integer limit,
        String search,
        String sortBy,
        String sortDir
) {}
