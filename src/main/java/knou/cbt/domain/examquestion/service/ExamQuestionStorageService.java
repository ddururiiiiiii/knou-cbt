package knou.cbt.domain.examquestion.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExamQuestionStorageService {

    private final WebClient.Builder webClientBuilder;

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    public String uploadQuestionImage(Long examId, MultipartFile file) throws IOException {
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
        client.post()
                .uri(supabaseUrl + "/storage/v1/object/" + bucket + "/" + path)
                .header("Authorization", "Bearer " + supabaseKey)
                .header("apikey", supabaseKey)
                .contentType(MediaType.parseMediaType(file.getContentType())) // <-- 여기 수정
                .bodyValue(file.getBytes())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return supabaseUrl + "/storage/v1/object/public/" + bucket + "/" + path;
    }


}
