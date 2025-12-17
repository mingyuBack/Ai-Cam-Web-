package hund.test.ai_organic.DTO;

import java.util.List;

/**
 * Flask 서버의 JSON {"detections": [...]} 구조와
 * 정확히 일치하는 메인 DTO입니다.
 */
public class YoloDetectionResponse {

    // JSON의 "detections" 키와 매핑됩니다.
    // [수정] List<DetectionResult.Detection>에서 List<Detection>으로 변경
    private List<Detection> detections;

    // JSON 라이브러리(Jackson)는 역직렬화를 위해 기본 생성자가 필요합니다.
    public YoloDetectionResponse() {}

    // Getter와 Setter
    public List<Detection> getDetections() {
        return detections;
    }

    public void setDetections(List<Detection> detections) {
        this.detections = detections;
    }
}