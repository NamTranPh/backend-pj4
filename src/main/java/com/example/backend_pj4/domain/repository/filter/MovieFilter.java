package com.example.backend_pj4.domain.repository.filter;

import com.example.backend_pj4.common.constants.enums.AgeRating;
import com.example.backend_pj4.common.constants.enums.AudioTrackType;
import com.example.backend_pj4.common.constants.enums.MovieType;
import com.example.backend_pj4.common.constants.enums.VideoQuality;
import com.example.backend_pj4.common.constants.enums.VideoStatus;

/**
 * Bộ lọc danh sách phim.
 *
 * @param q          từ khoá tìm theo tiêu đề; null hoặc rỗng là không lọc
 * @param publicOnly true thì chỉ lấy phim READY + PUBLIC, và bỏ qua {@code status}
 */
public record MovieFilter(
        String q,
        MovieType movieType,
        VideoStatus status,
        String genreId,
        Integer year,
        String country,
        AgeRating ageRating,
        VideoQuality quality,
        AudioTrackType audioTrack,
        boolean publicOnly
) {
    public MovieFilter(String q, MovieType movieType, VideoStatus status, String genreId, Integer year, String country, boolean publicOnly) {
        this(q, movieType, status, genreId, year, country, null, null, null, publicOnly);
    }
}
