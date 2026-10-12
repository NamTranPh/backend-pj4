package com.example.backend_pj4.infrastructure.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

/**
 * Khoá sợi dây nối {@code .env} → {@code application.yml} → {@code @ConfigurationProperties}.
 *
 * <p>Đứt một mắt ở giữa thì <b>không có lỗi biên dịch nào</b>: Spring bind được bao nhiêu thì
 * bind, phần còn lại để null, hệ thống vẫn khởi động và chỉ hỏng lúc ai đó dùng tới tính năng.
 *
 * <p>Đúng chuyện đã xảy ra với {@code app.payos} và {@code app.playback}: hai class
 * {@code @ConfigurationProperties} tồn tại từ commit đầu, {@code .env} có đủ 12 biến, nhưng
 * {@code application.yml} chưa từng có section {@code app:} để bắc cầu — nên
 * {@code PAYOS_CLIENT_ID} chỉ map tới {@code payos.client-id}, thiếu tiền tố {@code app.}.
 * Kết quả: {@code PayOsProperties.clientId} luôn null, và
 * {@code PlaybackProperties.tokenSecret} dùng giá trị mặc định hardcode trong file .java.
 *
 * <p>Đọc file thuần, không dựng Spring context nên không cần database.
 */
class ConfigurationBindingTest {

    private static final Path YML = Path.of("src/main/resources/application.yml");
    private static final Path ENV_EXAMPLE = Path.of(".env.example");
    private static final Path PROPERTIES_DIR =
            Path.of("src/main/java/com/example/backend_pj4/infrastructure/config/properties");

    private static final Pattern PREFIX =
            Pattern.compile("@ConfigurationProperties\\(prefix\\s*=\\s*\"([^\"]+)\"");
    private static final Pattern PLACEHOLDER = Pattern.compile("\\$\\{([A-Z_][A-Z0-9_]*)");
    private static final Pattern ENV_LINE = Pattern.compile("^([A-Z_][A-Z0-9_]*)=", Pattern.MULTILINE);

    @Test
    void moiPrefixConfigurationProperties_deuCoSectionTuongUngTrongYml() throws IOException {
        String yml = Files.readString(YML, StandardCharsets.UTF_8);
        List<String> thieu = new ArrayList<>();

        for (Path f : javaFiles()) {
            Matcher m = PREFIX.matcher(Files.readString(f, StandardCharsets.UTF_8));
            if (!m.find()) {
                continue;
            }
            String prefix = m.group(1);
            if (!ymlHasPath(yml, prefix)) {
                thieu.add(f.getFileName() + " -> prefix \"" + prefix + "\"");
            }
        }

        assertThat(thieu)
                .as("Class @ConfigurationProperties không có section tương ứng trong application.yml "
                        + "sẽ bind ra null mà không báo lỗi gì")
                .isEmpty();
    }

    @Test
    void moiBienTrongEnvExample_deuDuocApplicationYmlDoc() throws IOException {
        Set<String> dungTrongYml = placeholders(Files.readString(YML, StandardCharsets.UTF_8));
        List<String> chet = new ArrayList<>();

        Matcher m = ENV_LINE.matcher(Files.readString(ENV_EXAMPLE, StandardCharsets.UTF_8));
        while (m.find()) {
            if (!dungTrongYml.contains(m.group(1))) {
                chet.add(m.group(1));
            }
        }

        assertThat(chet)
                .as("Biến khai trong .env.example nhưng application.yml không đọc — "
                        + "người sau tưởng chỉnh được, thực ra không có tác dụng gì")
                .isEmpty();
    }

    @Test
    void moiBienApplicationYmlCan_deuCoTrongEnvExample() throws IOException {
        Set<String> khaiTrongEnv = new LinkedHashSet<>();
        Matcher m = ENV_LINE.matcher(Files.readString(ENV_EXAMPLE, StandardCharsets.UTF_8));
        while (m.find()) {
            khaiTrongEnv.add(m.group(1));
        }

        List<String> thieu = placeholders(Files.readString(YML, StandardCharsets.UTF_8)).stream()
                .filter(v -> !khaiTrongEnv.contains(v))
                .toList();

        assertThat(thieu)
                .as(".env.example là tài liệu cấu hình duy nhất; thiếu biến thì không ai biết chỉnh được")
                .isEmpty();
    }

    /** Giá trị mặc định dạng literal: chuỗi, số, hoặc boolean. */
    private static final Pattern LITERAL_DEFAULT =
            Pattern.compile("^private\\s+\\S+\\s+\\w+\\s*=\\s*(\"|'|-?\\d|true\\b|false\\b)");

    /**
     * Chỉ bắt giá trị mặc định dạng <b>literal</b>.
     *
     * <p>Khởi tạo collection rỗng kiểu {@code = new ArrayList<>()} là hợp lệ — nó chỉ tránh NPE
     * chứ không giả vờ rằng cấu hình đã có. Thứ nguy hiểm là một literal trông như cấu hình thật,
     * khiến hệ thống chạy bằng giá trị cứng mà không ai biết.
     */
    @Test
    void khongClassPropertiesNaoDatGiaTriMacDinhDangLiteral() throws IOException {
        List<String> viPham = new ArrayList<>();
        for (Path f : javaFiles()) {
            for (String line : Files.readString(f, StandardCharsets.UTF_8).split("\n")) {
                String t = line.trim();
                if (!t.contains("static") && LITERAL_DEFAULT.matcher(t).find()) {
                    viPham.add(f.getFileName() + ": " + t);
                }
            }
        }

        assertThat(viPham)
                .as("Mặc định phải ở application.yml. Để literal trong file .java thì khi cấu hình "
                        + "không tới nơi, hệ thống chạy bằng giá trị cứng mà không ai biết — đúng "
                        + "chuyện đã xảy ra với PlaybackProperties.tokenSecret")
                .isEmpty();
    }

    private List<Path> javaFiles() throws IOException {
        try (Stream<Path> s = Files.list(PROPERTIES_DIR)) {
            return s.filter(p -> p.toString().endsWith(".java")).toList();
        }
    }

    private Set<String> placeholders(String yml) {
        Set<String> out = new LinkedHashSet<>();
        Matcher m = PLACEHOLDER.matcher(yml);
        while (m.find()) {
            out.add(m.group(1));
        }
        return out;
    }

    /** Dò đường dẫn lồng nhau kiểu {@code app.payos} theo đúng mức thụt lề của YAML. */
    private boolean ymlHasPath(String yml, String prefix) {
        int from = 0;
        int indent = 0;
        for (String part : prefix.split("\\.")) {
            Pattern p = Pattern.compile("^ {" + indent + "}" + Pattern.quote(part) + ":", Pattern.MULTILINE);
            Matcher m = p.matcher(yml);
            if (!m.find(from)) {
                return false;
            }
            from = m.end();
            indent += 2;
        }
        return true;
    }
}
