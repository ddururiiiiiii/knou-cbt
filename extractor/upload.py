#!/usr/bin/env python3
"""
추출된 JSON을 DB에 업로드하는 스크립트

사용법:
  python upload.py 자료구조_2023_기말.json

DB URL 설정 방법:
  .env 파일에 DATABASE_URL=postgresql://... 추가
  또는 --db-url 옵션 사용
"""

import argparse
import json
import os
import sys

import psycopg2
from dotenv import load_dotenv

load_dotenv()


def get_or_create_department(cur, name: str) -> int:
    cur.execute("SELECT id FROM department WHERE department_name = %s", (name,))
    row = cur.fetchone()
    if row:
        return row[0]
    cur.execute(
        "INSERT INTO department (department_name) VALUES (%s) RETURNING id", (name,)
    )
    return cur.fetchone()[0]


def get_or_create_subject(cur, name: str, category: str, dept_id: int, grade: int) -> int:
    cur.execute("SELECT id FROM subject WHERE subject_name = %s", (name,))
    row = cur.fetchone()
    if row:
        return row[0]
    cur.execute(
        "INSERT INTO subject (subject_name, subject_category, department_id, grade) VALUES (%s, %s, %s, %s) RETURNING id",
        (name, category, dept_id, grade),
    )
    return cur.fetchone()[0]


def get_or_create_exam(cur, subject_id: int, exam_type: str, year: int) -> int:
    cur.execute(
        "SELECT id FROM exam WHERE subject_id = %s AND exam_type = %s AND year = %s",
        (subject_id, exam_type, year),
    )
    row = cur.fetchone()
    if row:
        exam_id = row[0]
        print(f"  이미 존재하는 시험 (id={exam_id}) → 기존 문제 삭제 후 재삽입")
        cur.execute("DELETE FROM exam_question WHERE exam_id = %s", (exam_id,))
        return exam_id
    cur.execute(
        "INSERT INTO exam (subject_id, exam_type, year) VALUES (%s, %s, %s) RETURNING id",
        (subject_id, exam_type, year),
    )
    return cur.fetchone()[0]


def upload(json_path: str, db_url: str):
    with open(json_path, "r", encoding="utf-8") as f:
        data = json.load(f)

    required = ["department", "subject", "year", "exam_type", "questions"]
    for key in required:
        if key not in data:
            print(f"오류: JSON에 '{key}' 필드가 없습니다.")
            sys.exit(1)

    conn = psycopg2.connect(db_url)
    try:
        with conn:
            with conn.cursor() as cur:
                dept_id = get_or_create_department(cur, data["department"])
                print(f"학과: {data['department']} (id={dept_id})")

                subj_id = get_or_create_subject(
                    cur,
                    data["subject"],
                    data.get("subject_category", "전공선택"),
                    dept_id,
                    data.get("grade", 2),
                )
                print(f"과목: {data['subject']} (id={subj_id})")

                exam_id = get_or_create_exam(cur, subj_id, data["exam_type"], data["year"])
                print(f"시험: {data['year']}년 {data['exam_type']} (id={exam_id})")

                inserted = 0
                skipped = 0

                for q in data["questions"]:
                    if not q.get("answer"):
                        print(f"  문제 {q.get('question_no', '?')}: 정답 없음 → 건너뜀")
                        skipped += 1
                        continue

                    cur.execute(
                        """INSERT INTO exam_question
                               (exam_id, question_no, question_text, option1, option2, option3, option4)
                           VALUES (%s, %s, %s, %s, %s, %s, %s)
                           RETURNING id""",
                        (
                            exam_id,
                            q["question_no"],
                            q["question_text"],
                            q["option1"],
                            q["option2"],
                            q["option3"],
                            q["option4"],
                        ),
                    )
                    question_id = cur.fetchone()[0]

                    cur.execute(
                        "INSERT INTO exam_question_answer (question_id, option_no) VALUES (%s, %s)",
                        (question_id, q["answer"]),
                    )
                    inserted += 1

                print(f"\n완료: {inserted}개 삽입, {skipped}개 건너뜀")
    finally:
        conn.close()


def main():
    parser = argparse.ArgumentParser(description="기출문제 JSON → DB 업로드")
    parser.add_argument("file", help="업로드할 JSON 파일 경로")
    parser.add_argument("--db-url", help="DB 연결 URL (없으면 DATABASE_URL 환경변수 사용)")
    args = parser.parse_args()

    db_url = args.db_url or os.environ.get("DATABASE_URL")
    if not db_url:
        print("오류: DB URL이 필요합니다.")
        print("  방법 1: .env 파일에 DATABASE_URL=postgresql://... 추가")
        print("  방법 2: --db-url 옵션 사용")
        sys.exit(1)

    print(f"업로드 시작: {args.file}\n")
    upload(args.file, db_url)


if __name__ == "__main__":
    main()
