package com.example.backend_pj4.presentation.movie.request;

import org.springdoc.core.annotations.ParameterObject;

import com.example.backend_pj4.common.constants.enums.AgeRating;
import com.example.backend_pj4.common.constants.enums.AudioTrackType;
import com.example.backend_pj4.common.constants.enums.MovieType;
import com.example.backend_pj4.common.constants.enums.VideoQuality;
import com.example.backend_pj4.presentation.common.request.PageQueryParamDto;

import io.swagger.v3.oas.annotations.Parameter;

/**
 * DTO nhận tham số tìm kiếm và lọc danh sách phim công khai.
 */
@ParameterObject
public class RequestPublicMovieDto extends PageQueryParamDto {

    @Parameter(description = "Loại phim: SINGLE (Phim lẻ), SERIES (Phim bộ)")
    private MovieType type;

    @Parameter(description = "Lọc theo ID thể loại phim")
    private String genreId;

    @Parameter(description = "Lọc theo năm phát hành", example = "2026")
    private Integer year;

    @Parameter(description = "Lọc theo quốc gia sản xuất", example = "Vietnam")
    private String country;

    @Parameter(description = "Phân loại độ tuổi: P, K, T13, T16, T18, C")
    private AgeRating ageRating;

    @Parameter(description = "Chất lượng video: HD, FULL_HD, FOUR_K")
    private VideoQuality quality;

    @Parameter(description = "Âm thanh / Phụ đề: VIETSUB, DUBBED, VOICEOVER, MULTI_AUDIO")
    private AudioTrackType audioTrack;

    public RequestPublicMovieDto() {
    }

    public RequestPublicMovieDto(MovieType type, String genreId, Integer year, String country,
                                Integer page, Integer limit, String search, String sortBy, String sortDir) {
        super(page, limit, search, sortBy, sortDir);
        this.type = type;
        this.genreId = genreId;
        this.year = year;
        this.country = country;
    }

    public RequestPublicMovieDto(MovieType type, String genreId, Integer year, String country,
                                AgeRating ageRating, VideoQuality quality, AudioTrackType audioTrack,
                                Integer page, Integer limit, String search, String sortBy, String sortDir) {
        super(page, limit, search, sortBy, sortDir);
        this.type = type;
        this.genreId = genreId;
        this.year = year;
        this.country = country;
        this.ageRating = ageRating;
        this.quality = quality;
        this.audioTrack = audioTrack;
    }

    public MovieType getType() {
        return type;
    }

    public void setType(MovieType type) {
        this.type = type;
    }

    public String getGenreId() {
        return genreId;
    }

    public void setGenreId(String genreId) {
        this.genreId = genreId;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public AgeRating getAgeRating() {
        return ageRating;
    }

    public void setAgeRating(AgeRating ageRating) {
        this.ageRating = ageRating;
    }

    public VideoQuality getQuality() {
        return quality;
    }

    public void setQuality(VideoQuality quality) {
        this.quality = quality;
    }

    public AudioTrackType getAudioTrack() {
        return audioTrack;
    }

    public void setAudioTrack(AudioTrackType audioTrack) {
        this.audioTrack = audioTrack;
    }
}
