package knou.cbt.domain.examquestion.service;

import knou.cbt.common.util.ImageUploadValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExamQuestionStorageService {

    private final WebClient.Builder webClientBuilder;

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    public String uploadQuestionImage(Long examId, MultipartFile file) throws IOException {
        ImageUploadValidator.validate(file);

        String bucket = "exam"; // exam 전용 버킷

        // 확장자
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // 안전 파일명
        String safeFileName = UUID.randomUUID().toString() + extension;
        String path = examId + "/" + safeFileName;

        WebClient client = webClientBuilder.build();
        try {
            client.post()
                    .uri(supabaseUrl + "/storage/v1/object/" + bucket + "/" + path)
                    .header("Authorization", "Bearer " + supabaseKey)
                    .header("apikey", supabaseKey)
                    .contentType(MediaType.parseMediaType(file.getContentType())) // <-- 여기 수정
                    .bodyValue(file.getBytes())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("Supabase 이미지 업로드 실패: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new IOException("Supabase 업로드 실패 (" + e.getStatusCode() + "): " + e.getResponseBodyAsString());
        } catch (WebClientRequestException e) {
            log.error("Supabase 이미지 업로드 요청 실패", e);
            throw new IOException("Supabase 연결 실패: " + e.getMessage());
        }

        return supabaseUrl + "/storage/v1/object/public/" + bucket + "/" + path;
    }


}
