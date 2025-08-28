function onChange() {
    const selectId = this.id;
    let departmentId = document.getElementById("departmentId").value;
    let subjectId = document.getElementById("subjectId").value;
    let year = document.getElementById("year").value;
    let semester = document.getElementById("semester").value;
    let category = document.getElementById("category").value;

    if (selectId === 'departmentId'){
        //과목
        if (this.value === ""){
            const subjectSelect = document.getElementById("subjectId");
            while (subjectSelect.firstChild) {
                subjectSelect.removeChild(subjectSelect.firstChild);
            }
            const option0 = document.createElement("option");
            option0.value = "";
            option0.text = "== 과목 ==";
            subjectSelect.appendChild(option0);
            document.getElementById("subjectId").disabled = true;
        } else {
            searchSelectChange("subjectId", this.value);
            document.getElementById("subjectId").disabled = false;
        }
        //년도
        const yearSelect = document.getElementById("year");
        while (yearSelect.firstChild) {
            yearSelect.removeChild(yearSelect.firstChild);
        }
        const option1 = document.createElement("option");
        option1.value = "";
        option1.text = "== 년도 ==";
        yearSelect.appendChild(option1);
        document.getElementById("year").disabled = true;

        //학기
        const semesterSelect = document.getElementById("semester");
        while (semesterSelect.firstChild) {
            semesterSelect.removeChild(semesterSelect.firstChild);
        }
        const option2 = document.createElement("option");
        option2.value = "";
        option2.text = "== 학기 ==";
        semesterSelect.appendChild(option2);
        document.getElementById("semester").disabled = true;

        //구분
        const categorySelect = document.getElementById("category");
        while (categorySelect.firstChild) {
            categorySelect.removeChild(categorySelect.firstChild);
        }
        const option3 = document.createElement("option");
        option3.value = "";
        option3.text = "== 구분 ==";
        document.getElementById("category").disabled = true;
        categorySelect.appendChild(option3);

        let url = "";
        if (this.value !== ""){
            url = "/exam/api?" + "departmentId=" + this.value
        } else {
            url = "/exam/api";
        }

        //조건 검색
        fetch(url, {
            method: "GET"
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error("HTTP 요청이 실패했습니다.");
                }
                return response.json();

            })
            .then(data => {
                updateTable(data.exams, data.exams.length);
                createPagination(data.totalPages, data.currentPage);
            })
            .catch(error => {
                console.error("Error : ", error);
            });

    } else if (selectId === 'subjectId'){

        //년도
        if (this.value === ""){
            const yearSelect = document.getElementById("year");
            while (yearSelect.firstChild) {
                yearSelect.removeChild(yearSelect.firstChild);
            }
            const option1 = document.createElement("option");
            option1.value = "";
            option1.text = "== 년도 ==";
            yearSelect.appendChild(option1);
            document.getElementById("year").disabled = true;
        } else {
            searchSelectChange("year", this.value);
            document.getElementById("year").disabled = false;
        }

        //학기
        const semesterSelect = document.getElementById("semester");
        while (semesterSelect.firstChild) {
            semesterSelect.removeChild(semesterSelect.firstChild);
        }
        const option2 = document.createElement("option");
        option2.value = "";
        option2.text = "== 학기 ==";
        semesterSelect.appendChild(option2);
        document.getElementById("semester").disabled = true;

        //구분
        const categorySelect = document.getElementById("category");
        while (categorySelect.firstChild) {
            categorySelect.removeChild(categorySelect.firstChild);
        }
        const option3 = document.createElement("option");
        option3.value = "";
        option3.text = "== 구분 ==";
        document.getElementById("category").disabled = true;
        categorySelect.appendChild(option3);

        let url = "";
        if (this.value !== ""){
            url = "/exam/api?" + "departmentId=" + departmentId + "&subjectId="+ subjectId
        } else {
            url = "/exam/api?" + "departmentId=" + departmentId;
        }


        //조건 검색
        fetch(url, {
            method: "GET"
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error("HTTP 요청이 실패했습니다.");
                }
                return response.json();

            })
            .then(data => {
                updateTable(data.exams, data.exams.length);
                createPagination(data.totalPages, data.currentPage);
            })
            .catch(error => {
                console.error("Error : ", error);
            });

    } else if (selectId === 'year'){
        //학기
        if (this.value === ""){
            const semesterSelect = document.getElementById("semester");
            while (semesterSelect.firstChild) {
                semesterSelect.removeChild(semesterSelect.firstChild);
            }
            const option2 = document.createElement("option");
            option2.value = "";
            option2.text = "== 학기 ==";
            semesterSelect.appendChild(option2);
            document.getElementById("semester").disabled = true;
        } else {
            searchSelectChange("semester", subjectId+year);
            document.getElementById("semester").disabled = false;
        }

        //구분
        const categorySelect = document.getElementById("category");
        while (categorySelect.firstChild) {
            categorySelect.removeChild(categorySelect.firstChild);
        }
        const option3 = document.createElement("option");
        option3.value = "";
        option3.text = "== 구분 ==";
        document.getElementById("category").disabled = true;
        categorySelect.appendChild(option3);

        let url = "";
        if (this.value !== ""){
            url = "/exam?" + "departmentId=" + departmentId
                + "&subjectId="+ subjectId
                + "&year=" + year
        } else {
            url = "/exam?" + "departmentId=" + departmentId + "&subjectId="+ subjectId;
        }
        //조건 검색
        fetch(url, {
            method: "GET"
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error("HTTP 요청이 실패했습니다.");
                }
                return response.json();

            })
            .then(data => {
                updateTable(data.exams, data.exams.length);
                createPagination(data.totalPages, data.currentPage);
            })
            .catch(error => {
                console.error("Error : ", error);
            });


    } else if (selectId === 'semester'){
        //구분
        if (this.value === ""){
            const categorySelect = document.getElementById("category");
            while (categorySelect.firstChild) {
                categorySelect.removeChild(categorySelect.firstChild);
            }
            const option3 = document.createElement("option");
            option3.value = "";
            option3.text = "== 구분 ==";
            document.getElementById("category").disabled = true;
            categorySelect.appendChild(option3);
        } else {
            searchSelectChange("category", subjectId+year+semester);
            document.getElementById("category").disabled = false;
        }

        let url = "";
        if (this.value !== ""){
            url = "/exam?" + "departmentId=" + departmentId
                + "&subjectId="+ subjectId
                + "&year=" + year
                + "&semester=" + semester
        } else {
            url = "/exam?" + "departmentId=" + departmentId
                + "&subjectId="+ subjectId
                + "&year=" + year;
        }

        //조건 검색
        fetch(url, {
            method: "GET"
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error("HTTP 요청이 실패했습니다.");
                }
                return response.json();

            })
            .then(data => {
                updateTable(data.exams, data.exams.length);
                createPagination(data.totalPages, data.currentPage);
            })
            .catch(error => {
                console.error("Error : ", error);
            });
    } else if (selectId === 'category'){

        let url = "";
        if (this.value !== ""){
            url = "/exam?" + "departmentId=" + departmentId
                + "&subjectId="+ subjectId
                + "&year=" + year
                + "&semester=" + semester
                + "&category=" + category
        } else {
            url = "/exam?" + "departmentId=" + departmentId
                + "&subjectId="+ subjectId
                + "&year=" + year
                + "&semester=" + semester;
        }

        //조건 검색
        fetch(url, {
            method: "GET"
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error("HTTP 요청이 실패했습니다.");
                }
                return response.json();

            })
            .then(data => {
                updateTable(data.exams, data.exams.length);
                createPagination(data.totalPages, data.currentPage);
            })
            .catch(error => {
                console.error("Error : ", error);
            });
    }
}


function searchSelectChange(searchKey, searchValue){

    fetch("/search/api?" + "searchKey=" + searchKey +"&" + "searchValue=" + searchValue, {
        method: "GET"
    })
        .then(response => {
            if (!response.ok) {
                throw new Error("HTTP 요청이 실패했습니다.");
            }
            return response.json();
        })
        .then(datas => {
            console.log(datas);
            const select = document.getElementById(searchKey);
            while (select.firstChild) {
                select.removeChild(select.firstChild);
            }

            const option = document.createElement("option");
            option.value = "";

            if (searchKey === "departmentId"){
                option.text = "== 학과 ==";
            } else if (searchKey === "subjectId"){
                option.text = "== 과목 ==";
            } else if (searchKey === "year"){
                option.text = "== 년도 ==";
            } else if (searchKey === "semester"){
                option.text = "== 학기 ==";
            } else if (searchKey === "category"){
                option.text = "== 구분 ==";
            }
            select.appendChild(option);

            datas.forEach(data => {
                const opt = document.createElement("option");
                opt.value = data.searchKey;
                opt.text = data.searchValue;
                select.appendChild(opt);
            });

        })
        .catch(error => {
            console.error("Error : ", error);
        });

}

function updateTable(exams, length) {
    let tbody = document.querySelector(".table-group-divider");
    tbody.innerHTML = "";
    exams.forEach(exam => {
        let row = tbody.insertRow();
        row.insertCell(0).innerText = exam.departmentName;
        row.insertCell(1).innerText = exam.subjectName;
        row.insertCell(2).innerText = `${exam.grade} 학년`;
        row.insertCell(3).innerText = `${exam.examYear} 년도`;
        row.insertCell(4).innerText = `${exam.semester} 학기`;
        row.insertCell(5).innerText = getCategoryText(exam.examCategory);
        let btnCell = row.insertCell(6);
        let btn = document.createElement("button");
        btn.className = "btn btn-primary";
        btn.innerText = "풀기";
        btnCell.appendChild(btn);
    });
    document.getElementById("count").textContent = `총 ${length} 건`;
}

function getCategoryText(category) {
    switch (category) {
        case 1: return "기말";
        case 2: return "계절학기(동계)";
        case 3: return "계절학기(하계)";
        default: return "";
    }
}

function createPagination(totalPages, currentPage) {
    let pagination = document.getElementById("pagination");
    pagination.innerHTML = "";

    let ul = document.createElement("ul");
    ul.className = "pagination";
    ul.style.display = "flex";
    ul.style.justifyContent = "center";

    // 첫 페이지 버튼 생성
    let firstPageLi = document.createElement("li");
    firstPageLi.className = "page-item";
    let firstPageA = document.createElement("a");
    firstPageA.className = "page-link";
    firstPageA.innerText = "First";
    firstPageA.id = "firstBtn";

    let selectArr = ["departmentId", "subjectId", "year", "semester", "category"];
    let paramArr = {};
    for (let i = 0; i < selectArr.length; i++){
        if (document.getElementById(selectArr[i]).value !== ""){
            paramArr[selectArr[i]] = document.getElementById(selectArr[i]).value;
        }
    }
    let paramString = "";
    for (let key in paramArr ) {
        paramString += "&" + key + "=" + paramArr[key];
    }

    //firstPageA.href = `/exam?page=1${paramString}`;
    firstPageLi.appendChild(firstPageA);
    if (currentPage === 1) {
        firstPageLi.className += " disabled";
    }
    ul.appendChild(firstPageLi);

    // 이전 페이지 버튼 생성
    let prevPageLi = document.createElement("li");
    prevPageLi.className = "page-item";
    let prevPageA = document.createElement("a");
    prevPageA.className = "page-link";
    prevPageA.innerText = "Previous";
    prevPageA.id = "preBtn";
    //prevPageA.href = `/exam?page=${currentPage - 1}${paramString}`;
    prevPageLi.appendChild(prevPageA);
    if (currentPage === 1) {
        prevPageLi.className += " disabled";
    }
    ul.appendChild(prevPageLi);

    // 페이지 번호 생성
    for (let i = 1; i <= totalPages; i++) {
        let pageLi = document.createElement("li");
        pageLi.className = "page-item";
        if (i == currentPage) {
            pageLi.className += " active";
        }
        let pageA = document.createElement("a");
        pageA.className = "page-link";
        //pageA.href = `/exam?page=${i}${paramString}`;
        pageA.id = `page_${i}`;
        pageA.innerText = i;
        pageLi.appendChild(pageA);
        ul.appendChild(pageLi);
    }

    // 다음 페이지 버튼 생성
    let nextPageLi = document.createElement("li");
    nextPageLi.className = "page-item";

    let nextPageA = document.createElement("a");
    nextPageA.className = "page-link";
    nextPageA.innerText = "Next";
    //nextPageA.href = `/exam?page=${currentPage + 1}${paramString}`;
    nextPageA.id = "nextBtn";
    nextPageLi.appendChild(nextPageA);
    if (currentPage === totalPages) {
        nextPageLi.className += " disabled";
    }

    ul.appendChild(nextPageLi);

    // 마지막 페이지 버튼 생성
    let lastPageLi = document.createElement("li");
    lastPageLi.className = "page-item";
    let lastPageA = document.createElement("a");
    lastPageA.className = "page-link";
    lastPageA.innerText = "Last";
    //lastPageA.href = `/exam?page=${totalPages}${paramString}`;
    lastPageA.id = "lastBtn";
    lastPageLi.appendChild(lastPageA);
    if (currentPage === totalPages) {
        lastPageLi.className += " disabled";
    }
    ul.appendChild(lastPageLi);

    pagination.appendChild(ul);
    let container = document.getElementById("content");
    container.appendChild(pagination);

    document.getElementById("firstBtn").addEventListener("click", function() {
        fetch(`/exam?page=1${paramString}`)
            .then(data => console.log(data))
            .catch(error => console.error(error));
    });

    document.getElementById("preBtn").addEventListener("click", function() {
        fetch(`/exam?page=${currentPage - 1}${paramString}`)
            .then(data => console.log(data))
            .catch(error => console.error(error));
    });

    for (let i = 1; i <= totalPages; i++) {
        document.getElementById(`page_${i}`).addEventListener("click", function() {
            fetch(`/exam?page=${i}${paramString}`)
                .then(data => console.log(data))
                .catch(error => console.error(error));
        });
    }

    document.getElementById("nextBtn").addEventListener("click", function() {
        fetch(`/exam?page=${currentPage + 1}${paramString}`)
            .then(data => console.log(data))
            .catch(error => console.error(error));
    });

    document.getElementById("lastBtn").addEventListener("click", function() {
        fetch(`/exam?page=${totalPages}${paramString}`)
            .then(data => console.log(data))
            .catch(error => console.error(error));
    });

}

document.addEventListener('DOMContentLoaded', function() {

    document.querySelectorAll('select').forEach(function(select) {
        select.addEventListener('change', onChange);
    });
    searchSelectChange("departmentId", null);
})