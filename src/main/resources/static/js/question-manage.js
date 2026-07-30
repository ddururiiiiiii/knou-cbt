window.addEventListener("DOMContentLoaded", () => {
    updateQuestionCount();

    document.getElementById("addRowBtn").addEventListener("click", addRow);
    document.getElementById("addRowBtnBottom").addEventListener("click", addRow);
    document.getElementById("deleteSelectedBtn").addEventListener("click", deleteSelectedRows);
    document.getElementById("deleteSelectedBtnBottom").addEventListener("click", deleteSelectedRows);
    document.getElementById("sortRowsBtn").addEventListener("click", sortRows);
    document.getElementById("sortRowsBtnBottom").addEventListener("click", sortRows);
    document.getElementById("selectAll").addEventListener("click", function () {
        toggleAll(this);
    });
    document.getElementById("selectAllBottom").addEventListener("click", function () {
        toggleAll(this);
    });
    document.getElementById("uploadExcelBtn").addEventListener("click", confirmAndUpload);

    ["previewBtn", "previewBtnBottom"].forEach(function (id) {
        const previewBtn = document.getElementById(id);
        if (previewBtn) {
            previewBtn.addEventListener("click", function () {
                openPreview(previewBtn.dataset.examId);
            });
        }
    });

    // 동적으로 추가되는 행(input/button)까지 한번에 처리하는 이벤트 위임
    const table = document.getElementById("questionTable");
    table.addEventListener("input", function (e) {
        if (e.target.matches("input[name*='.questionNo']")) {
            e.target.value = e.target.value.replace(/[^0-9]/g, '');
        } else if (e.target.matches("input[name*='.answers']")) {
            e.target.value = e.target.value.replace(/[^0-9,]/g, '');
        }
        if (e.target.matches("input[type='text']")) {
            e.target.title = e.target.value;
        }
    });
    table.addEventListener("click", function (e) {
        const delBtn = e.target.closest(".btn-delete-row");
        if (delBtn) {
            deleteRow(delBtn);
        }
    });
    table.addEventListener("change", function (e) {
        if (e.target.matches(".option-type-select")) {
            applyOptionTypeToRow(e.target);
        }
        if (e.target.matches("select")) {
            e.target.title = e.target.options[e.target.selectedIndex].text;
        }
        if (e.target.matches("input[type='file']")) {
            e.target.title = e.target.files.length > 0 ? e.target.files[0].name : "첨부파일 없음";
        }
    });

    document.getElementById("questionForm").addEventListener("submit", function (e) {
        if (!validateBeforeSave()) {
            e.preventDefault();
            appAlert("입력값을 다시 확인해주세요.");
        }
    });

    document.getElementById("excelFile").addEventListener("change", function () {
        this.form.submit();
    });
});

function applyOptionTypeToRow(select) {
    const row = select.closest("tr");
    const layoutSelect = row.querySelector(".image-layout-select");
    const isImage = select.value === "IMAGE";
    row.classList.toggle("row-option-image", isImage);
    if (layoutSelect) {
        layoutSelect.disabled = !isImage;
    }
}

function updateQuestionCount() {
    const tbody = document.getElementById('questionTable');
    const count = tbody.rows.length;
    document.getElementById('countValue').textContent = count;
    document.getElementById('countValueBottom').textContent = count;
}

function addRow() {
    const tbody = document.getElementById('questionTable');
    const rowCount = tbody.rows.length;

    if (rowCount >= 35) {
        appAlert("더 이상 행을 추가할 수 없습니다. (최대 35문제)");
        return;
    }

    const row = tbody.insertRow();

    let cell = row.insertCell();
    cell.innerHTML = `<input type="checkbox" class="row-check"/>`;

    cell = row.insertCell();
    cell.innerHTML = `
        <input type="text" class="form-control" name="questions[${rowCount}].questionNo" value="${rowCount + 1}" title="${rowCount + 1}"/>
        <div class="invalid-feedback">숫자만 입력</div>
    `;

    cell = row.insertCell();
    cell.innerHTML = `<input type="text" class="form-control" name="questions[${rowCount}].questionText"/>`;

    cell = row.insertCell();
    cell.innerHTML = `
        <select class="form-select form-select-sm option-type-select" name="questions[${rowCount}].optionType" title="텍스트">
            <option value="TEXT" selected>텍스트</option>
            <option value="IMAGE">이미지</option>
        </select>`;

    cell = row.insertCell();
    cell.innerHTML = `
        <select class="form-select form-select-sm image-layout-select" name="questions[${rowCount}].imageLayout" disabled title="2단(2x2)">
            <option value="GRID_2X2" selected>2단(2x2)</option>
            <option value="STACK_1X4">1단(세로4줄)</option>
        </select>`;

    for (let n = 1; n <= 4; n++) {
        cell = row.insertCell();
        cell.innerHTML = `
            <input type="text" class="form-control form-control-sm option-text-input mb-1" name="questions[${rowCount}].option${n}"/>
            <input type="file" class="form-control form-control-sm option-file-input" accept="image/*" name="questions[${rowCount}].option${n}File" title="첨부파일 없음"/>`;
    }

    cell = row.insertCell();
    cell.innerHTML = `
        <input type="text" class="form-control" name="questions[${rowCount}].answers"/>
        <div class="invalid-feedback">숫자(쉼표 구분)만 입력</div>
    `;

    cell = row.insertCell();
    cell.innerHTML = `<span class="text-muted">-</span>`;

    cell = row.insertCell();
    cell.innerHTML = `<input type="file" class="form-control" name="questions[${rowCount}].imageFile" accept="image/*" title="첨부파일 없음"/>`;

    cell = row.insertCell();
    cell.innerHTML = `<button type="button" class="btn btn-danger btn-sm btn-delete-row">삭제</button>`;
    updateQuestionCount();
}

function deleteRow(btn) {
    appConfirm("이 문제를 삭제하시겠습니까?").then(function (ok) {
        if (!ok) {
            return;
        }
        const row = btn.closest("tr");
        row.remove();
        updateQuestionCount();
    });
}

function deleteSelectedRows() {
    const checks = document.querySelectorAll('.row-check:checked');
    if (checks.length === 0) {
        appAlert("삭제할 문제를 선택해주세요.");
        return;
    }
    appConfirm(`선택한 ${checks.length}개 문제를 삭제하시겠습니까?`).then(function (ok) {
        if (!ok) {
            return;
        }
        checks.forEach(chk => chk.closest('tr').remove());
        updateQuestionCount();
    });
}

function sortRows() {
    const tbody = document.getElementById('questionTable');
    const rows = Array.from(tbody.rows);

    rows.sort((a, b) => {
        const aVal = parseInt(a.querySelector("input[name*='.questionNo']").value) || 0;
        const bVal = parseInt(b.querySelector("input[name*='.questionNo']").value) || 0;
        return aVal - bVal;
    });

    tbody.innerHTML = "";
    rows.forEach((row, index) => {
        row.querySelector("input[name*='.questionNo']").value = index + 1;
        tbody.appendChild(row);
    });
    updateQuestionCount();
}

function toggleAll(master) {
    const checks = document.querySelectorAll('.row-check');
    checks.forEach(chk => chk.checked = master.checked);
    ["selectAll", "selectAllBottom"].forEach(function (id) {
        const el = document.getElementById(id);
        if (el && el !== master) {
            el.checked = master.checked;
        }
    });
}

function confirmAndUpload() {
    appConfirm("엑셀 파일을 업로드하면 화면의 기존 데이터는 지워지고 업로드한 데이터로 대체됩니다. 진행하시겠습니까?")
        .then(function (ok) {
            if (ok) {
                document.getElementById('excelFile').click();
            }
        });
}

function validateBeforeSave() {
    let valid = true;
    const rows = document.querySelectorAll("#questionTable tr");

    rows.forEach((row) => {
        const noInput = row.querySelector("input[name*='.questionNo']");
        const answerInput = row.querySelector("input[name*='.answers']");

        if (noInput) {
            if (!/^\d+$/.test(noInput.value.trim())) {
                noInput.classList.add("is-invalid");
                valid = false;
            } else {
                noInput.classList.remove("is-invalid");
            }
        }

        if (answerInput) {
            if (!/^\d+(,\d+)*$/.test(answerInput.value.trim())) {
                answerInput.classList.add("is-invalid");
                valid = false;
            } else {
                answerInput.classList.remove("is-invalid");
            }
        }
    });

    return valid;
}

function openPreview(examId) {
    const url = `/admin/exams/${examId}/preview`;
    const options = "width=1000,height=800,scrollbars=yes,resizable=yes";
    window.open(url, "examPreview", options);
}
