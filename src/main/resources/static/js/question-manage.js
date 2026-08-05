const SCROLL_TO_BOTTOM_KEY = "kcbt_questionManage_scrollToBottom";

window.addEventListener("DOMContentLoaded", () => {
    updateQuestionCount();
    restoreScrollPosition();

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
            updateTooltipContent(e.target, e.target.value);
        }
    });
    table.addEventListener("click", function (e) {
        const delBtn = e.target.closest(".btn-delete-row");
        if (delBtn) {
            deleteRow(delBtn);
        }
        const delOptionImageBtn = e.target.closest(".btn-delete-option-image");
        if (delOptionImageBtn) {
            clearOptionImageCell(delOptionImageBtn.closest("td"));
        }
        const delQuestionImageBtn = e.target.closest(".btn-delete-question-image");
        if (delQuestionImageBtn) {
            clearQuestionImageCell(delQuestionImageBtn.closest("td"));
        }
    });
    table.addEventListener("change", function (e) {
        if (e.target.matches(".option-type-select")) {
            applyOptionTypeToRow(e.target);
        }
        if (e.target.matches("select")) {
            updateTooltipContent(e.target, e.target.options[e.target.selectedIndex].text);
        }
        if (e.target.matches("input[type='file']")) {
            updateTooltipContent(e.target, e.target.files.length > 0 ? e.target.files[0].name : "첨부파일 없음");
        }
        if (e.target.matches(".option-file-input, input[name*='.imageFile']")) {
            updateLocalImagePreview(e.target);
        }
    });

    document.getElementById("questionForm").addEventListener("submit", function (e) {
        if (!validateBeforeSave()) {
            e.preventDefault();
            appAlert("입력값을 다시 확인해주세요.");
            return;
        }
        if (e.submitter && e.submitter.id === "saveBtnBottom") {
            sessionStorage.setItem(SCROLL_TO_BOTTOM_KEY, "1");
        } else {
            sessionStorage.removeItem(SCROLL_TO_BOTTOM_KEY);
        }
        showLoading("문제를 저장하고 있습니다...");
    });

    document.getElementById("excelFile").addEventListener("change", function () {
        showLoading("엑셀 파일을 업로드하고 있습니다...");
        this.form.submit();
    });
});

function initTooltips(root) {
    (root || document).querySelectorAll('[data-bs-toggle="tooltip"]').forEach(function (el) {
        const existing = bootstrap.Tooltip.getInstance(el);
        if (existing) {
            existing.dispose();
        }
        new bootstrap.Tooltip(el);
    });
}

function updateTooltipContent(el, text) {
    el.setAttribute("title", text);
    const instance = bootstrap.Tooltip.getInstance(el);
    if (!text) {
        // Bootstrap은 title이 비어있어도 최초 생성 시점 설정값으로 표시 여부를 판단하기 때문에,
        // setContent로 내용만 비우면 빈 말풍선(테두리+화살표)만 뜨는 버그가 생긴다.
        // 내용이 없을 땐 아예 인스턴스를 없애서 호버해도 아무것도 안 뜨게 한다.
        instance?.dispose();
        return;
    }
    if (instance) {
        instance.setContent({".tooltip-inner": text});
    } else if (el.matches('[data-bs-toggle="tooltip"]')) {
        new bootstrap.Tooltip(el);
    }
}

const IMAGE_ICON_SVG = '<svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" fill="currentColor" class="bi bi-image" viewBox="0 0 16 16">'
    + '<path d="M6.002 5.5a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0"/>'
    + '<path d="M2.002 1a2 2 0 0 0-2 2v10a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V3a2 2 0 0 0-2-2zm12 1a1 1 0 0 1 1 1v6.5l-3.777-1.947a.5.5 0 0 0-.577.093l-3.71 3.71-2.66-1.772a.5.5 0 0 0-.63.062L1.002 12V3a1 1 0 0 1 1-1z"/>'
    + '</svg>';

function updateLocalImagePreview(fileInput) {
    let preview = fileInput.nextElementSibling;
    if (!preview || !preview.classList.contains("local-image-preview")) {
        preview = null;
    }

    const file = fileInput.files[0];
    if (!file) {
        if (preview) {
            URL.revokeObjectURL(preview.dataset.objectUrl);
            bootstrap.Tooltip.getInstance(preview)?.dispose();
            preview.remove();
        }
        return;
    }

    const objectUrl = URL.createObjectURL(file);
    if (!preview) {
        preview = document.createElement("span");
        preview.className = "text-success local-image-preview ms-1";
        preview.style.cursor = "pointer";
        preview.setAttribute("data-bs-toggle", "tooltip");
        preview.setAttribute("data-bs-html", "true");
        preview.innerHTML = IMAGE_ICON_SVG;
        fileInput.insertAdjacentElement("afterend", preview);
    } else {
        URL.revokeObjectURL(preview.dataset.objectUrl);
        bootstrap.Tooltip.getInstance(preview)?.dispose();
    }

    preview.dataset.objectUrl = objectUrl;
    preview.setAttribute("title", `<img src="${objectUrl}" class="tooltip-img-preview" alt="미리보기"/>`);
    new bootstrap.Tooltip(preview);
}

function restoreScrollPosition() {
    if (sessionStorage.getItem(SCROLL_TO_BOTTOM_KEY) !== "1") {
        return;
    }
    sessionStorage.removeItem(SCROLL_TO_BOTTOM_KEY);
    const bottomBar = document.getElementById("saveBtnBottom");
    if (bottomBar) {
        bottomBar.scrollIntoView({block: "center"});
    }
}

function applyOptionTypeToRow(select) {
    const row = select.closest("tr");
    const layoutSelect = row.querySelector(".image-layout-select");
    const isImage = select.value === "IMAGE";
    row.classList.toggle("row-option-image", isImage);
    if (layoutSelect) {
        layoutSelect.disabled = !isImage;
    }
    // 보기 형식(텍스트/이미지) 전환 시 반대 형식의 값이 그대로 남아있으면
    // (텍스트로는 URL 문자열이, 이미지로는 일반 텍스트가 저장되어) 저장 시 깨지므로 초기화한다.
    row.querySelectorAll(".option-text-input").forEach(function (textInput) {
        clearOptionImageCell(textInput.closest("td"));
    });
}

function clearOptionImageCell(td) {
    if (!td) {
        return;
    }
    const textInput = td.querySelector(".option-text-input");
    if (textInput) {
        textInput.value = "";
        updateTooltipContent(textInput, "");
    }
    const fileInput = td.querySelector(".option-file-input");
    if (fileInput) {
        fileInput.value = "";
        updateTooltipContent(fileInput, "첨부파일 없음");
    }
    const previewWrap = td.querySelector(".option-image-preview-wrap");
    if (previewWrap) {
        previewWrap.classList.add("d-none");
        const icon = previewWrap.querySelector("[data-bs-toggle='tooltip']");
        if (icon) {
            bootstrap.Tooltip.getInstance(icon)?.dispose();
        }
    }
    const localPreview = td.querySelector(".local-image-preview");
    if (localPreview) {
        URL.revokeObjectURL(localPreview.dataset.objectUrl);
        bootstrap.Tooltip.getInstance(localPreview)?.dispose();
        localPreview.remove();
    }
}

function clearQuestionImageCell(td) {
    if (!td) {
        return;
    }
    const urlInput = td.querySelector(".question-image-url-input");
    if (urlInput) {
        urlInput.value = "";
    }
    const fileInput = td.querySelector("input[type='file']");
    if (fileInput) {
        fileInput.value = "";
        updateTooltipContent(fileInput, "첨부파일 없음");
    }
    const previewWrap = td.querySelector(".question-image-preview-wrap");
    if (previewWrap) {
        previewWrap.classList.add("d-none");
        const icon = previewWrap.querySelector("[data-bs-toggle='tooltip']");
        if (icon) {
            bootstrap.Tooltip.getInstance(icon)?.dispose();
        }
    }
    const localPreview = td.querySelector(".local-image-preview");
    if (localPreview) {
        URL.revokeObjectURL(localPreview.dataset.objectUrl);
        bootstrap.Tooltip.getInstance(localPreview)?.dispose();
        localPreview.remove();
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
        <input type="text" class="form-control" name="questions[${rowCount}].questionNo" value="${rowCount + 1}" title="${rowCount + 1}" data-bs-toggle="tooltip" data-bs-placement="top"/>
        <div class="invalid-feedback">숫자만 입력</div>
    `;

    cell = row.insertCell();
    cell.innerHTML = `<input type="text" class="form-control" name="questions[${rowCount}].questionText" data-bs-toggle="tooltip" data-bs-placement="top"/>`;

    cell = row.insertCell();
    cell.innerHTML = `
        <select class="form-select form-select-sm option-type-select" name="questions[${rowCount}].optionType" title="텍스트" data-bs-toggle="tooltip" data-bs-placement="top">
            <option value="TEXT" selected>텍스트</option>
            <option value="IMAGE">이미지</option>
        </select>`;

    cell = row.insertCell();
    cell.innerHTML = `
        <select class="form-select form-select-sm image-layout-select" name="questions[${rowCount}].imageLayout" disabled title="2단(2x2)" data-bs-toggle="tooltip" data-bs-placement="top">
            <option value="GRID_2X2" selected>2단(2x2)</option>
            <option value="STACK_1X4">1단(세로4줄)</option>
        </select>`;

    for (let n = 1; n <= 4; n++) {
        cell = row.insertCell();
        cell.innerHTML = `
            <input type="text" class="form-control form-control-sm option-text-input mb-1" name="questions[${rowCount}].option${n}" data-bs-toggle="tooltip" data-bs-placement="top"/>
            <input type="file" class="form-control form-control-sm option-file-input" accept="image/*" name="questions[${rowCount}].option${n}File" title="첨부파일 없음" data-bs-toggle="tooltip" data-bs-placement="top"/>`;
    }

    cell = row.insertCell();
    cell.innerHTML = `
        <input type="text" class="form-control" name="questions[${rowCount}].answers" data-bs-toggle="tooltip" data-bs-placement="top"/>
        <div class="invalid-feedback">숫자(쉼표 구분)만 입력</div>
    `;

    cell = row.insertCell();
    cell.innerHTML = `
        <input type="hidden" name="questions[${rowCount}].imageUrl" value="" class="question-image-url-input"/>
        <input type="file" class="form-control form-control-sm" name="questions[${rowCount}].imageFile" accept="image/*" title="첨부파일 없음" data-bs-toggle="tooltip" data-bs-placement="top"/>
        <span class="question-image-preview-wrap d-none"></span>`;

    cell = row.insertCell();
    cell.innerHTML = `<button type="button" class="btn btn-link btn-delete-row p-0" title="문제 삭제"><i class="bi bi-x-circle-fill text-danger fs-4"></i></button>`;
    initTooltips(row);
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
    const tbody = document.getElementById('questionTable');
    if (tbody.rows.length === 0) {
        document.getElementById('excelFile').click();
        return;
    }
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
    const noValueCounts = {};

    rows.forEach((row) => {
        const noInput = row.querySelector("input[name*='.questionNo']");
        const answerInput = row.querySelector("input[name*='.answers']");

        if (noInput) {
            const trimmed = noInput.value.trim();
            if (/^\d+$/.test(trimmed)) {
                noValueCounts[trimmed] = (noValueCounts[trimmed] || 0) + 1;
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

    rows.forEach((row) => {
        const noInput = row.querySelector("input[name*='.questionNo']");
        if (!noInput) {
            return;
        }
        const feedback = noInput.parentElement.querySelector(".invalid-feedback");
        const trimmed = noInput.value.trim();

        if (!/^\d+$/.test(trimmed)) {
            noInput.classList.add("is-invalid");
            if (feedback) {
                feedback.textContent = "숫자만 입력";
            }
            valid = false;
        } else if (noValueCounts[trimmed] > 1) {
            noInput.classList.add("is-invalid");
            if (feedback) {
                feedback.textContent = "문제번호가 중복되었습니다";
            }
            valid = false;
        } else {
            noInput.classList.remove("is-invalid");
        }
    });

    return valid;
}

function openPreview(examId) {
    const url = `/admin/exams/${examId}/preview`;
    const options = "width=1000,height=800,scrollbars=yes,resizable=yes";
    window.open(url, "examPreview", options);
}
