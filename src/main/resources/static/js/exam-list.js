document.addEventListener("DOMContentLoaded", function () {
    const deptSelect = document.getElementById("departmentSelect");
    const subjectSelect = document.getElementById("subjectSelect");
    const examTypeSelect = document.getElementById("examTypeSelect");
    const yearSelect = document.getElementById("yearSelect");
    const form = document.getElementById("searchForm");
    const resetBtn = document.getElementById("resetBtn");
    const useYnCheck = document.getElementById("useYnCheck");

    const params = new URLSearchParams(window.location.search);
    const selectedDeptId = params.get("departmentId");
    const selectedSubjectId = params.get("subjectId");
    const selectedExamType = params.get("examType");
    const selectedYear = params.get("year");

    function fillSubjects(deptId, selected) {
        return fetch(`/api/departments/${deptId}/subjects`)
            .then(res => res.json())
            .then(data => {
                let options = '<option value="">과목 선택</option>';
                data.forEach(subj => {
                    const isSelected = String(subj.id) === String(selected) ? "selected" : "";
                    options += `<option value="${subj.id}" ${isSelected}>${subj.subjectName} (${subj.gradeDisplay}, ${subj.semesterDescription})</option>`;
                });
                subjectSelect.innerHTML = options;
                subjectSelect.disabled = false;
            });
    }

    function fillExamTypes(subjectId, selected) {
        const descriptions = {ATTENDANCE: "출석대체시험", FINAL: "기말시험", SEASONAL: "계절학기시험"};
        return fetch(`/api/subjects/${subjectId}/exam-types`)
            .then(res => res.json())
            .then(types => {
                let options = '<option value="">구분 선택</option>';
                types.forEach(t => {
                    const isSelected = t === selected ? "selected" : "";
                    options += `<option value="${t}" ${isSelected}>${descriptions[t] || t}</option>`;
                });
                examTypeSelect.innerHTML = options;
                examTypeSelect.disabled = false;
            });
    }

    function fillYears(subjectId, examType, selected) {
        return fetch(`/api/subjects/${subjectId}/exam-types/${examType}/years`)
            .then(res => res.json())
            .then(years => {
                let options = '<option value="">년도 선택</option>';
                years.forEach(y => {
                    const isSelected = String(y) === String(selected) ? "selected" : "";
                    options += `<option value="${y}" ${isSelected}>${y}</option>`;
                });
                yearSelect.innerHTML = options;
                yearSelect.disabled = false;
            });
    }

    resetBtn.addEventListener("click", function () {
        deptSelect.value = "";
        subjectSelect.innerHTML = '<option value="">과목 선택</option>';
        subjectSelect.disabled = true;
        examTypeSelect.innerHTML = '<option value="">구분 선택</option>';
        examTypeSelect.disabled = true;
        yearSelect.innerHTML = '<option value="">년도 선택</option>';
        yearSelect.disabled = true;
        if (useYnCheck) {
            useYnCheck.checked = false;
        }
        form.submit();
    });

    if (useYnCheck) {
        useYnCheck.addEventListener("change", function () {
            form.submit();
        });
    }

    // 최초 로딩 시 이전 선택값 복원
    if (selectedDeptId) {
        fillSubjects(selectedDeptId, selectedSubjectId).then(() => {
            if (selectedSubjectId) {
                fillExamTypes(selectedSubjectId, selectedExamType).then(() => {
                    if (selectedExamType) {
                        fillYears(selectedSubjectId, selectedExamType, selectedYear);
                    }
                });
            }
        });
    }

    deptSelect.addEventListener("change", function () {
        const deptId = this.value;
        subjectSelect.innerHTML = '<option value="">과목 선택</option>';
        examTypeSelect.innerHTML = '<option value="">구분 선택</option>';
        examTypeSelect.disabled = true;
        yearSelect.innerHTML = '<option value="">년도 선택</option>';
        yearSelect.disabled = true;

        if (!deptId) {
            subjectSelect.disabled = true;
            form.submit();
            return;
        }
        fillSubjects(deptId, null).then(() => form.submit());
    });

    subjectSelect.addEventListener("change", function () {
        const subjectId = this.value;
        examTypeSelect.innerHTML = '<option value="">구분 선택</option>';
        yearSelect.innerHTML = '<option value="">년도 선택</option>';
        yearSelect.disabled = true;

        if (!subjectId) {
            examTypeSelect.disabled = true;
            form.submit();
            return;
        }
        fillExamTypes(subjectId, null).then(() => form.submit());
    });

    examTypeSelect.addEventListener("change", function () {
        const subjectId = subjectSelect.value;
        const examType = this.value;
        yearSelect.innerHTML = '<option value="">년도 선택</option>';

        if (!examType) {
            yearSelect.disabled = true;
            form.submit();
            return;
        }
        fillYears(subjectId, examType, null).then(() => form.submit());
    });

    yearSelect.addEventListener("change", function () {
        form.submit();
    });

    document.querySelectorAll("[data-solve-url]").forEach(function (btn) {
        btn.addEventListener("click", function () {
            if (btn.dataset.hasQuestions === "false") {
                appToast("등록된 문제가 없습니다.", {type: "warning"});
                return;
            }
            appConfirm(`${btn.dataset.solveInfo} 시험을 시작합니다.`).then(function (ok) {
                if (ok) {
                    window.location.href = btn.dataset.solveUrl;
                }
            });
        });
    });

    document.querySelectorAll("[data-questions-url]").forEach(function (btn) {
        btn.addEventListener("click", function () {
            window.location.href = btn.dataset.questionsUrl;
        });
    });
});
