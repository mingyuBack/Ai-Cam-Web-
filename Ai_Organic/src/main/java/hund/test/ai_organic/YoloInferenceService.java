package hund.test.ai_organic;

import hund.test.ai_organic.DTO.YoloDetectionResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType; // 이 import가 있는지 확인
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

@Service
public class YoloInferenceService {

    private final WebClient webClient;

    public YoloInferenceService(WebClient.Builder webClientBuilder,
                                @Value("${yolo.api.url}") String yoloApiUrl) {
        this.webClient = webClientBuilder.baseUrl(yoloApiUrl).build();
    }

    /**
     * DetectionController에서 호출하는 핵심 메서드입니다.
     * 업로드된 이미지를 Flask YOLO 서버로 전송하고 탐지 결과를 받습니다.
     */
    public YoloDetectionResponse detectObjects(MultipartFile imageFile) throws IOException {

        MultipartBodyBuilder builder = new MultipartBodyBuilder();

        // [핵심 수정]
        // 1. 업로드된 파일의 원본 Content-Type을 가져옵니다.
        //    .parse() -> .parseMediaType()으로 메서드 이름 수정
        MediaType mediaType = MediaType.parseMediaType(imageFile.getContentType());

        builder.part("image", new ByteArrayResource(imageFile.getBytes()))
                .filename(imageFile.getOriginalFilename())
                // 2. 하드코딩된 JPEG 대신, 원본 파일의 mediaType을 설정합니다.
                .contentType(mediaType);

        // 2. WebClient를 이용한 비동기 POST 요청 및 응답 처리
        return webClient.post()
                .uri("/detect") // baseUrl이 생성자에서 설정됨
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), response -> {
                    // Flask 서버에서 오류(4xx, 5xx) 발생 시 예외 처리
                    return response.bodyToMono(String.class)
                            .map(errorBody -> new RuntimeException(
                                    String.format("Flask API Error (%s): %s", response.statusCode(), errorBody)
                            ));
                })
                .bodyToMono(YoloDetectionResponse.class) // 응답을 DTO로 변환
                .block(); // 블로킹하여 동기적으로 결과를 기다림
    }
}