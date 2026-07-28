document.addEventListener("DOMContentLoaded", function () {
    const CATEGORY_LABELS = {MAJOR: "전공", LIBERAL: "교양", ELECTIVE: "일반선택"};

    const deptSelect = document.getElementById("departmentSelect");
    const gradeSelect = document.getElementById("gradeSelect");
    const categorySelect = document.getElementById("categorySelect");
    const form = document.getElementById("searchForm");
    const resetBtn = document.getElementById("resetBtn");

    const params = new URLSearchParams(window.location.search);
    const selectedDeptId = params.get("departmentId");
    const selectedGrade = params.get("grade");
    const selectedCategory = params.get("subjectCategory");

    function fillGrades(deptId, selected) {
        return fetch(`/api/departments/${deptId}/grades`)
            .then(res => res.json())
            .then(grades => {
                let options = '<option value="">학년 선택</option>';
                grades.forEach(g => {
                    const isSelected = String(g) === String(selected) ? "selected" : "";
                    options += `<option value="${g}" ${isSelected}>${g}학년</option>`;
                });
                gradeSelect.innerHTML = options;
                gradeSelect.disabled = false;
            });
    }

    function fillCategories(deptId, grade, selected) {
        return fetch(`/api/departments/${deptId}/grades/${grade}/categories`)
            .then(res => res.json())
            .then(categories => {
                let options = '<option value="">과목구분 선택</option>';
                categories.forEach(c => {
                    const isSelected = c === selected ? "selected" : "";
                    options += `<option value="${c}" ${isSelected}>${CATEGORY_LABELS[c] || c}</option>`;
                });
                categorySelect.innerHTML = options;
                categorySelect.disabled = false;
            });
    }

    // 최초 로딩 시 이전 선택값 복원
    if (selectedDeptId) {
        fillGrades(selectedDeptId, selectedGrade).then(() => {
            if (selectedGrade) {
                fillCategories(selectedDeptId, selectedGrade, selectedCategory);
            }
        });
    }

    deptSelect.addEventListener("change", function () {
        const deptId = this.value;
        gradeSelect.innerHTML = '<option value="">학년 선택</option>';
        categorySelect.innerHTML = '<option value="">과목구분 선택</option>';
        categorySelect.disabled = true;

        if (!deptId) {
            gradeSelect.disabled = true;
            form.submit();
            return;
        }
        fillGrades(deptId, null).then(() => form.submit());
    });

    gradeSelect.addEventListener("change", function () {
        const deptId = deptSelect.value;
        const grade = this.value;
        categorySelect.innerHTML = '<option value="">과목구분 선택</option>';

        if (!grade) {
            categorySelect.disabled = true;
            form.submit();
            return;
        }
        fillCategories(deptId, grade, null).then(() => form.submit());
    });

    categorySelect.addEventListener("change", function () {
        form.submit();
    });

    resetBtn.addEventListener("click", function () {
        deptSelect.value = "";
        gradeSelect.innerHTML = '<option value="">학년 선택</option>';
        gradeSelect.disabled = true;
        categorySelect.innerHTML = '<option value="">과목구분 선택</option>';
        categorySelect.disabled = true;
        form.querySelector("input[name='keyword']").value = "";
        form.submit();
    });
});
