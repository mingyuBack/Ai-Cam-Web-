package hund.test.ai_organic.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * "detections" 리스트 내부의 개별 객체 DTO입니다.
 * JSON 필드명과 변수명이 정확히 일치해야 합니다.
 */
public class Detection {

    // JSON 키: "classId"
    private int classId;

    // JSON 키: "className"
    private String className;

    // JSON 키: "confidence"
    private double confidence;

    // --- [수정됨] ---
    // JSON 키: "boxAbsolute" (Python 서버가 보낸 키와 일치)
    // @JsonProperty 어노테이션을 사용하여 JSON 키 "boxAbsolute"와
    // Java 필드 "boxAbsolute"를 명시적으로 매핑합니다.
    @JsonProperty("boxAbsolute")
    private List<Double> boxAbsolute;
    // --- [수정 끝] ---


    // JSON 라이브러리(Jackson)는 역직렬화를 위해 기본 생성자가 필요합니다.
    public Detection() {}

    // --- 모든 필드에 대한 Getter와 Setter ---

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    // --- [수정됨] ---
    // Getter와 Setter 이름도 필드명과 일치시킵니다.
    public List<Double> getBoxAbsolute() {
        return boxAbsolute;
    }

    public void setBoxAbsolute(List<Double> boxAbsolute) {
        this.boxAbsolute = boxAbsolute;
    }
    // --- [수정 끝] ---
}
