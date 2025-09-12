package knou.cbt.web.notice;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notices")
public class NoticeImageController {

    private final WebClient webClient = WebClient.builder().build();

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    @Value("${supabase.bucket.notice}")
    private String noticeBucket;

    @PostMapping("/image")
    public Map<String, Object> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // 확장자 추출
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            // 안전한 파일명
            String safeFileName = UUID.randomUUID().toString() + extension;
            String path = noticeBucket + "/" + safeFileName;

            // Supabase Storage 업로드
            webClient.post()
                    .uri(supabaseUrl + "/storage/v1/object/" + path)
                    .header("Authorization", "Bearer " + supabaseKey)
                    .header("apikey", supabaseKey)
                    .contentType(MediaType.parseMediaType(file.getContentType()))
                    .bodyValue(file.getBytes())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // 퍼블릭 URL 리턴
            String url = supabaseUrl + "/storage/v1/object/public/" + path;
            return Map.of("success", true, "url", url);

        } catch (Exception e) {
            return Map.of("success", false, "error", e.getMessage());
        }
    }

}
