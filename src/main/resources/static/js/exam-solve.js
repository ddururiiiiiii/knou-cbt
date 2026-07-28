document.addEventListener("DOMContentLoaded", function () {
    const totalQuestions = parseInt(document.getElementById("totalQ").textContent, 10);
    const totalSeconds = totalQuestions * 60;
    let timeLeft = totalSeconds;

    const totalTimeEl = document.getElementById("totalTime");
    const timeLeftEl = document.getElementById("timeLeft");
    const progressEl = document.getElementById("progress");
    const remainingEl = document.getElementById("remaining");
    const progressPercentEl = document.getElementById("progressPercent");
    const form = document.getElementById("solveForm");

    totalTimeEl.textContent = formatTime(totalSeconds);
    updateProgress();

    const timer = setInterval(() => {
        timeLeft--;
        timeLeftEl.textContent = formatTime(timeLeft);
        updateTimeColor();

        if (timeLeft < 0) {
            clearInterval(timer);
            appAlert("시간이 종료되었습니다. 자동으로 제출됩니다.").then(function () {
                recordElapsedAndSubmit();
            });
        }
    }, 1000);

    function recordElapsedAndSubmit() {
        const elapsedInput = document.getElementById("elapsedSeconds");
        if (elapsedInput) {
            elapsedInput.value = totalSeconds - Math.max(timeLeft, 0);
        }
        form.submit();
    }

    function formatTime(sec) {
        const minutes = String(Math.floor(sec / 60)).padStart(2, "0");
        const seconds = String(sec % 60).padStart(2, "0");
        return `${minutes}:${seconds}`;
    }

    function updateTimeColor() {
        timeLeftEl.classList.remove("text-danger", "text-warning", "blink", "fw-bold");
        if (timeLeft <= 60) {
            timeLeftEl.classList.add("text-danger", "fw-bold", "blink");
        } else if (timeLeft <= totalSeconds / 2) {
            timeLeftEl.classList.add("text-warning", "fw-bold");
        }
    }

    function markAnswered(index, input) {
        const selected = parseInt(input.value);

        const ansDiv = document.getElementById(`ans-${index}`);
        const choiceSpans = ansDiv.querySelectorAll(".choice");
        choiceSpans.forEach((span, i) => {
            span.classList.toggle("selected", i + 1 === selected);
        });

        const card = input.closest(".card");
        card.classList.add("answered");
        const optionDivs = card.querySelectorAll(".option");
        optionDivs.forEach((div, i) => {
            div.classList.toggle("selected", i + 1 === selected);
        });

        updateProgress();
    }

    function selectAnswer(index, choice) {
        const radios = document.getElementsByName(`answers[${index}]`);
        if (!radios || radios.length === 0) return;
        const target = [...radios].find(r => r.value == choice);
        if (target) {
            target.checked = true;
            target.dispatchEvent(new Event("change", {bubbles: true}));
        }
    }

    function updateProgress() {
        let answeredCount = 0;
        for (let i = 0; i < totalQuestions; i++) {
            const radios = document.getElementsByName(`answers[${i}]`);
            if ([...radios].some(r => r.checked)) {
                answeredCount++;
            }
        }
        progressEl.textContent = answeredCount;
        remainingEl.textContent = totalQuestions - answeredCount;
        const percent = Math.round((answeredCount / totalQuestions) * 100);
        const progressBar = document.getElementById("progressBar");
        progressBar.style.width = percent + "%";
        progressBar.setAttribute("aria-valuenow", percent);
        progressPercentEl.textContent = percent;
    }

    function confirmSubmit() {
        const unanswered = [];
        for (let i = 0; i < totalQuestions; i++) {
            const radios = document.getElementsByName(`answers[${i}]`);
            if (![...radios].some(r => r.checked)) {
                unanswered.push(i + 1);
            }
        }

        if (unanswered.length > 0) {
            appConfirm(
                `아직 풀지 않은 문제가 ${unanswered.length}개 있어요 (${unanswered.join(", ")}번). 그래도 제출할까요?`,
                {confirmText: "제출하기", cancelText: "안 푼 문제로 이동"}
            ).then(function (ok) {
                if (ok) {
                    clearInterval(timer);
                    recordElapsedAndSubmit();
                } else {
                    document.getElementById(`question-${unanswered[0] - 1}`)
                        .scrollIntoView({behavior: "smooth", block: "center"});
                }
            });
            return;
        }

        appConfirm("정말 시험을 종료하고 제출하시겠습니까?").then(function (ok) {
            if (ok) {
                clearInterval(timer);
                recordElapsedAndSubmit();
            }
        });
    }

    // 정답 선택 클릭 (문제 영역 보기 + 우측 답안지 동그라미 공용 처리)
    document.addEventListener("click", function (e) {
        const el = e.target.closest("[data-index][data-choice]");
        if (el) {
            selectAnswer(parseInt(el.dataset.index, 10), parseInt(el.dataset.choice, 10));
        }
    });

    // 라디오 버튼이 실제로 체크될 때 답안지/보기 갱신
    form.addEventListener("change", function (e) {
        if (e.target.matches("input[type=radio][data-index]")) {
            markAnswered(parseInt(e.target.dataset.index, 10), e.target);
        }
    });

    document.getElementById("submitExamBtn").addEventListener("click", confirmSubmit);

    const backBtn = document.getElementById("backBtn");
    if (backBtn) {
        backBtn.addEventListener("click", function (e) {
            e.preventDefault();
            const url = this.href;
            appConfirm("지금까지 푼 답은 기록되지 않아요. 정말 목록으로 돌아갈까요?").then(function (ok) {
                if (ok) {
                    window.location.href = url;
                }
            });
        });
    }
});
