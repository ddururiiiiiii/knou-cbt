# 세션 진행 상황 트래커

> 여러 Claude Code 세션(웹/로컬)이 병렬로 작업할 때 서로 컨텍스트를 공유하기 위한 문서입니다.
> 각 세션은 메모리를 공유하지 않으므로, 설계안·진행 상황·다음 할 일을 여기에 기록해두면
> 다른 세션(다른 브랜치, 로컬 터미널 포함)에서 이어서 작업할 수 있습니다.

## 사용법

1. 작업을 시작하거나 끝낼 때 아래 표에 자신의 브랜치 행을 추가/갱신하세요.
2. 설계안처럼 내용이 긴 것은 이 파일에 직접 쓰지 말고 `docs/design/<주제>.md` 파일로 따로 만든 뒤,
   이 표의 "설계 문서" 칸에 경로를 링크하세요.
3. 이 파일은 **main 브랜치에 직접** 커밋합니다 (기능 브랜치에 두면 다른 세션이 못 봄).
   - `git checkout main && git pull origin main`
   - 표 수정 후 `git add docs/progress.md && git commit -m "docs: progress 업데이트" && git push origin main`
   - 작업하던 브랜치로 다시 돌아가는 것 잊지 마세요: `git checkout <내 브랜치>`
4. 로컬 터미널에서 이어서 작업할 때는 `git fetch origin main && git show origin/main:docs/progress.md`
   또는 `git pull origin main` 후 이 파일을 열어서 현재 상황을 파악하세요.

## 세션 목록

| 브랜치 | 목적 | 상태 | 설계 문서 | 다음 할 일 | 마지막 업데이트 |
|---|---|---|---|---|---|
| `claude/memory-cli-workflow-8847cj` | 세션 간 작업 공유 워크플로우 정리 (이 파일 자체) | 완료 | - | - | 2026-08-06 |
| `claude/beta-notice-message-update-xglihe` | 메인화면 Beta 안내 문구에 오탈자/버그 양해 문구 추가 및 줄바꿈 정리 (`index.html`) | 완료 | - | - | 2026-08-06 |
| `claude/notice-detail-top-buttons-v0klmh` | 공지사항 상세 화면(`noticeDetail.html`)에 하단에만 있던 수정/삭제/목록 버튼을 상단에도 동일하게 추가 | 완료 | - | - | 2026-08-06 |

<!-- 새 세션에서 작업을 시작하면 위 표에 행을 추가하세요. 상태 예시: 설계 중 / 구현 중 / 리뷰 대기 / 완료 -->
