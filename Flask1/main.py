import os
import io
import json
from flask import Flask, request, jsonify
from PIL import Image
from ultralytics import YOLO
import logging
# from flask_sqlalchemy import SQLAlchemy # 챗봇 관련 임포트 제거
# from datetime import datetime # 챗봇 관련 임포트 제거

# --- 1. 로거 및 Flask 앱 초기화 ---
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = Flask(__name__)

# --- 2. 기본 설정 (YOLO) ---

# YOLO 모델 경로
MODEL_PATH = 'C:/Users/MINGYU/runs/detect/train9/weights/best.pt'

# 최대 파일 크기 제한 (YOLO용)
app.config['MAX_CONTENT_LENGTH'] = 10 * 1024 * 1024 * 1024 # 10 GB

# (제거) 챗봇용 SQLite DB 설정
# (제거) SQLAlchemy DB 인스턴스 초기화

# --- 3. 모델 로드 ---

# 3.1. YOLO11n 모델 로드
try:
    model_yolo = YOLO(MODEL_PATH)
    logger.info(f"YOLO Model loaded successfully from: {MODEL_PATH}")
except Exception as e:
    logger.error(f"Error loading YOLO model from {MODEL_PATH}: {e}")
    model_yolo = None

# (제거) 챗봇 DB 모델 정의 (Items, ChatLog)

# --- 4. API 엔드포인트 정의 (YOLO) ---

# 4.1. YOLO 추론 API 엔드포인트
@app.route('/detect', methods=['POST'])
def detect_objects():
    if model_yolo is None:
        return jsonify({"error": "YOLO Model is not loaded. Check server startup logs."}), 500

    if 'image' not in request.files:
        return jsonify({"error": "No image file provided in request (expected 'image' field)."}), 400

    image_file = request.files['image']

    try:
        image_bytes = image_file.read()
        image = Image.open(io.BytesIO(image_bytes))

        # YOLOv8 추론 수행
        results = model_yolo(image, conf=0.5, iou=0.45, verbose=False)

        # 결과 파싱
        detections = []
        for r in results:
            boxes = r.boxes.xyxyn.cpu().tolist()
            classes = r.boxes.cls.cpu().tolist()
            confidences = r.boxes.conf.cpu().tolist()
            names_map = r.names

            for box, cls, conf in zip(boxes, classes, confidences):
                detections.append({
                    "classId": int(cls),
                    "className": names_map[int(cls)],
                    "confidence": round(conf, 4),
                    "boxNormalized": [round(c, 4) for c in box]
                })

        logger.info(f"Successfully processed image. Detected {len(detections)} objects.")
        return jsonify({"detections": detections}), 200

    except Exception as e:
        logger.error(f"An error occurred during inference: {e}", exc_info=True)
        return jsonify({"error": "Internal server error during image processing or inference."}), 500

# (제거) 챗봇 DB 초기 설정 API (/setup)
# (제거) 챗봇 응답 API (/api/chatbot)

# --- 5. 서버 실행 ---
if __name__ == '__main__':
    # Flask 서버 실행 설정
    # host='0.0.0.0'은 외부(안드로이드 앱, 스프링 부트) 접속 허용
    # port=5000은 앱에서 호출할 포트
    app.run(host='0.0.0.0', port=5000, debug=False)