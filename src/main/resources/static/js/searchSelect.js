document.addEventListener('DOMContentLoaded', function() {
    document.querySelectorAll('select').forEach(function(select) {
        select.addEventListener('change', onChange);
    });
    searchSelectChange("departmentId", null);
});

function onChange() {
    const selectId = this.id;
    const selects = {
        departmentId: document.getElementById("departmentId").value,
        subjectId: document.getElementById("subjectId").value,
        year: document.getElementById("year").value,
        semester: document.getElementById("semester").value,
        category: document.getElementById("category").value
    };

    const resetSelect = (id, defaultText) => {
        const select = document.getElementById(id);
        select.innerHTML = `<option value="">== ${defaultText} ==</option>`;
        select.disabled = true;
    };

    const updateSelectOptions = (searchKey, searchValue, defaultText) => {
        searchSelectChange(searchKey, searchValue);
        document.getElementById(searchKey).disabled = false;
    };

    const handleFetch = (url) => {
        fetch(url, { method: "GET" })
            .then(response => {
                if (!response.ok) throw new Error("HTTP request failed");
                return response.json();
            })
            .then(data => {
                updateTable(data.exams, data.exams.length);
                createPagination(data.totalPages, data.currentPage);
            })
            .catch(error => console.error("Error: ", error));
    };

    switch (selectId) {
        case 'departmentId':
            resetSelect('subjectId', '과목');
            resetSelect('year', '년도');
            resetSelect('semester', '학기');
            resetSelect('category', '구분');
            if (this.value) {
                updateSelectOptions("subjectId", this.value, "과목");
                document.getElementById("subjectId").disabled = false;
                handleFetch(`/exam/api?departmentId=${this.value || ''}`);
            }  else {
                handleFetch(`/exam/api`)
            }
            break;

        case 'subjectId':
            resetSelect('year', '년도');
            resetSelect('semester', '학기');
            resetSelect('category', '구분');
            if (this.value) {
                updateSelectOptions("year", this.value, "년도");
                handleFetch(`/exam/api?departmentId=${selects.departmentId}&subjectId=${this.value || ''}`);
            } else {
                handleFetch(`/exam/api?departmentId=${selects.departmentId}`)
            }
            break;


        case 'year':
            resetSelect('semester', '학기');
            resetSelect('category', '구분');
            if (this.value) {
                updateSelectOptions("semester", selects.subjectId + this.value, "학기");
                handleFetch(`/exam?departmentId=${selects.departmentId}&subjectId=${selects.subjectId}&year=${this.value || ''}`);
            } else {
                handleFetch(`/exam?departmentId=${selects.departmentId}&subjectId=${selects.subjectId}`)
            }
            break;

        case 'semester':
            resetSelect('category', '구분');
            if (this.value) {
                updateSelectOptions("category", selects.subjectId + selects.year + this.value, "구분");
                handleFetch(`/exam?departmentId=${selects.departmentId}&subjectId=${selects.subjectId}&year=${selects.year}&semester=${this.value || ''}`);
            } else {
                handleFetch(`/exam?departmentId=${selects.departmentId}&subjectId=${selects.subjectId}&year=${selects.year}`)
            }
            break;

        case 'category':
            if (this.value) {
                handleFetch(`/exam?departmentId=${selects.departmentId}&subjectId=${selects.subjectId}&year=${selects.year}&semester=${selects.semester}&category=${this.value || ''}`);
            } else {
                handleFetch(`/exam?departmentId=${selects.departmentId}&subjectId=${selects.subjectId}&year=${selects.year}&semester=${selects.semester}`);
            }

            break;
    }
}

function searchSelectChange(searchKey, searchValue) {
    fetch(`/search/api?searchKey=${searchKey}&searchValue=${searchValue || ''}`, { method: "GET" })
        .then(response => {
            if (!response.ok) throw new Error("HTTP request failed");
            return response.json();
        })
        .then(datas => {
            const select = document.getElementById(searchKey);
            select.innerHTML = `<option value="">== ${getDefaultText(searchKey)} ==</option>`;
            datas.forEach(data => {
                const opt = document.createElement("option");
                opt.value = data.searchKey;
                opt.text = data.searchValue;
                select.appendChild(opt);
            });
        })
        .catch(error => console.error("Error: ", error));
}

function getDefaultText(key) {
    const texts = {
        departmentId: "학과",
        subjectId: "과목",
        year: "년도",
        semester: "학기",
        category: "구분"
    };
    return texts[key] || '';
}



function updateTable(exams, length) {
    const tbody = document.querySelector(".table-group-divider");
    tbody.innerHTML = exams.map(exam => `
        <tr>
            <td>${exam.departmentName}</td>
            <td>${exam.subjectName}</td>
            <td>${getSubjectCategoryText(exam.subjectCategory)} </td>
            <td>${exam.grade} 학년</td>
            <td>${exam.examYear} 년도</td>
            <td>${exam.semester} 학기</td>
            <td>${getCategoryText(exam.examCategory)}</td>
            <td><button class="btn btn-primary" 
            onclick="location.href='/exam/${exam.examId}'">풀기</button></td>
        </tr>`).join('');
    document.getElementById("count").textContent = `총 ${length} 건`;
}


function getSubjectCategoryText(subjectCategory) {
    const subjectCategories = {
        1: "전공",
        2: "교양",
        3: "일반선택"
    };
    return subjectCategories[subjectCategory] || '';
}
function getCategoryText(category) {
    const categories = {
        1: "기말",
        2: "계절학기(동계)",
        3: "계절학기(하계)"
    };
    return categories[category] || '';
}

function createPagination(totalPages, currentPage) {
    const pagination = document.getElementById("pagination");
    const paramArr = Array.from(document.querySelectorAll('select')).reduce((params, select) => {
        if (select.value) params[select.id] = select.value;
        return params;
    }, {});
    const paramString = Object.entries(paramArr).map(([key, value]) => `&${key}=${value}`).join('');

    const createPageItem = (text, page, isDisabled, isActive) => `
        <li class="page-item ${isDisabled ? 'disabled' : ''} ${isActive ? 'active' : ''}">
            <a class="page-link" href="#" data-page="${page}">${text}</a>
        </li>
    `;

    const pageItems = [
        createPageItem("First", 1, currentPage === 1, false),
        createPageItem("Previous", currentPage - 1, currentPage === 1, false),
        ...Array.from({ length: totalPages }, (_, i) => createPageItem(i + 1, i + 1, false, i + 1 === currentPage)).join(''),
        createPageItem("Next", currentPage + 1, currentPage === totalPages, false),
        createPageItem("Last", totalPages, currentPage === totalPages, false)
    ].join('');

    pagination.innerHTML = `<ul class="pagination" style="display: flex; justify-content: center;">${pageItems}</ul>`;

    pagination.querySelectorAll('a[data-page]').forEach(link => {
        link.addEventListener('click', function (event) {
            event.preventDefault();
            const page = this.getAttribute('data-page');
            fetch(`/exam?page=${page}${paramString}`)
                .then
                (response => response.json())
                .then(data => {
                    updateTable(data.exams, data.exams.length);
                    createPagination(data.totalPages, data.currentPage);
                })
                .catch(error => console.error("Error: ", error));
        });
    });
}
