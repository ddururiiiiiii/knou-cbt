#!/usr/bin/env python3
"""
PDF 기출문제 추출 스크립트 (Claude API 사용)

사용법:
  python extract.py --file 자료구조_2023_기말.pdf \
                    --department "컴퓨터과학과" \
                    --subject "자료구조" \
                    --year 2023 \
                    --type 기말

HWP 파일인 경우 먼저 PDF로 변환:
  libreoffice --headless --convert-to pdf 파일명.hwp
"""

import argparse
import base64
import json
import os
import re
import sys
from pathlib import Path

import anthropic
from dotenv import load_dotenv

load_dotenv()

EXAM_TYPE_MAP = {
    "기말": "FINAL",
    "출석": "ATTENDANCE",
    "계절": "SEASONAL",
    "FINAL": "FINAL",
    "ATTENDANCE": "ATTENDANCE",
    "SEASONAL": "SEASONAL",
}

PROMPT = """이 PDF는 한국방송통신대학교 기출문제입니다.
모든 객관식 문제를 추출하여 아래 JSON 형식으로 반환하세요.

반드시 아래 형식의 순수 JSON만 반환하고, 다른 설명은 쓰지 마세요:

{
  "questions": [
    {
      "question_no": 1,
      "question_text": "문제 내용 전체",
      "option1": "보기 1 내용",
      "option2": "보기 2 내용",
      "option3": "보기 3 내용",
      "option4": "보기 4 내용",
      "answer": 1
    }
  ]
}

규칙:
- question_no: 문제 번호 (정수)
- question_text: 문제 지문 전체 (표, 코드, 그림 설명 포함)
- option1~4: 각 보기 내용 (①②③④ 기호는 제외하고 내용만)
- answer: 정답 번호 1~4 (정수), 정답 알 수 없으면 null
- 보기가 4개가 아닌 문제는 제외
"""


def extract_from_pdf(pdf_path: str) -> list:
    api_key = os.environ.get("ANTHROPIC_API_KEY")
    if not api_key:
        print("오류: ANTHROPIC_API_KEY 환경변수가 없습니다.")
        sys.exit(1)

    client = anthropic.Anthropic(api_key=api_key)

    with open(pdf_path, "rb") as f:
        pdf_data = base64.standard_b64encode(f.read()).decode("utf-8")

    print(f"Claude API 호출 중... ({Path(pdf_path).name})")

    response = client.messages.create(
        model="claude-haiku-4-5-20251001",
        max_tokens=8192,
        messages=[
            {
                "role": "user",
                "content": [
                    {
                        "type": "document",
                        "source": {
                            "type": "base64",
                            "media_type": "application/pdf",
                            "data": pdf_data,
                        },
                    },
                    {
                        "type": "text",
                        "text": PROMPT,
                    },
                ],
            }
        ],
    )

    raw = response.content[0].text.strip()

    # ```json ... ``` 블록 처리
    json_match = re.search(r"```(?:json)?\s*([\s\S]*?)\s*```", raw)
    if json_match:
        raw = json_match.group(1)

    try:
        data = json.loads(raw)
    except json.JSONDecodeError as e:
        print(f"JSON 파싱 실패: {e}")
        print("Claude 응답 원문:")
        print(raw[:500])
        sys.exit(1)

    return data["questions"]


def main():
    parser = argparse.ArgumentParser(description="PDF 기출문제 추출기")
    parser.add_argument("--file", required=True, help="PDF 파일 경로")
    parser.add_argument("--department", required=True, help="학과명 (예: 컴퓨터과학과)")
    parser.add_argument("--subject", required=True, help="과목명 (예: 자료구조)")
    parser.add_argument("--subject-category", default="전공선택",
                        help="과목 구분 (전공필수/전공선택/교양필수/교양선택, 기본값: 전공선택)")
    parser.add_argument("--grade", type=int, default=2, help="학년 1~4 (기본값: 2)")
    parser.add_argument("--year", type=int, required=True, help="시험 연도 (예: 2023)")
    parser.add_argument("--type", required=True,
                        choices=["기말", "출석", "계절", "FINAL", "ATTENDANCE", "SEASONAL"],
                        help="시험 종류 (기말/출석/계절)")
    parser.add_argument("--output", help="출력 JSON 파일 경로 (기본: 입력파일명.json)")
    args = parser.parse_args()

    if not Path(args.file).exists():
        print(f"오류: 파일을 찾을 수 없습니다 → {args.file}")
        sys.exit(1)

    exam_type = EXAM_TYPE_MAP[args.type]
    output_path = args.output or str(Path(args.file).with_suffix(".json"))

    questions = extract_from_pdf(args.file)
    print(f"추출된 문제 수: {len(questions)}개")

    result = {
        "department": args.department,
        "subject": args.subject,
        "subject_category": args.subject_category,
        "grade": args.grade,
        "year": args.year,
        "exam_type": exam_type,
        "questions": questions,
    }

    with open(output_path, "w", encoding="utf-8") as f:
        json.dump(result, f, ensure_ascii=False, indent=2)

    print(f"저장 완료: {output_path}")
    print("upload.py로 DB에 업로드하기 전에 JSON 내용을 확인하세요.")


if __name__ == "__main__":
    main()
