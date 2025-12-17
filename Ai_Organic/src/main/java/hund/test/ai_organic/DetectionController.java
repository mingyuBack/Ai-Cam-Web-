package hund.test.ai_organic;

import hund.test.ai_organic.DTO.YoloDetectionResponse;
import hund.test.ai_organic.YoloInferenceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Base64;

@Controller
public class DetectionController {

    private final YoloInferenceService yoloInferenceService;

    @Autowired
    public DetectionController(YoloInferenceService yoloInferenceService) {
        this.yoloInferenceService = yoloInferenceService;
    }

    /**
     * 메인 페이지 (파일 업로드 폼)를 반환합니다.
     */
    @GetMapping("/")
    public String index(Model model) {
        // detectionResult가 없는 초기 상태의 index.html을 로드
        return "index";
    }

    /**
     * POST 요청을 받아 YOLO 탐지 추론을 수행하고, 결과를 템플릿에 전달합니다.
     * 엔드포인트는 index.html의 폼 action="/api/v1/yolo/detect"와 일치해야 합니다.
     */
    @PostMapping("/api/v1/yolo/detect")
    public String detect(
            @RequestPart("image") MultipartFile imageFile,
            Model model
    ) {
        if (imageFile.isEmpty()) {
            model.addAttribute("error", "이미지 파일이 제공되지 않았습니다.");
            return "index";
        }

        try {
            // 1. YOLO 추론 서비스 호출 (Flask 서버와 통신)
            YoloDetectionResponse response = yoloInferenceService.detectObjects(imageFile);

            // 2. 템플릿에 결과 객체 전달
            model.addAttribute("detectionResult", response);

            // 3. 업로드된 이미지를 Base64로 변환하여 템플릿에 전달 (시각화용)
            String base64Image = Base64.getEncoder().encodeToString(imageFile.getBytes());
            String uploadedImagePath = "data:" + imageFile.getContentType() + ";base64," + base64Image;

            model.addAttribute("uploadedImagePath", uploadedImagePath);

        } catch (IOException e) {
            model.addAttribute("error", "파일 처리 중 오류가 발생했습니다.");
            return "index";
        } catch (Exception e) {
            // WebClient 통신 또는 YOLO 추론 중 오류 발생 시
            model.addAttribute("error", "객체 탐지 서버(Flask)와 통신 중 오류가 발생했습니다: " + e.getMessage());
            return "index";
        }

        // 결과가 포함된 index.html 템플릿을 다시 렌더링
        return "index";
    }
}
