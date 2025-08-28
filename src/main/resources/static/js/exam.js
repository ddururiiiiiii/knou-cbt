window.onload = function() {
    const timer = setInterval(() => {
        const currentTime = new Date().getTime();
        const elapsedTime = currentTime - startTime;

        const hours = Math.floor(elapsedTime / (1000 * 60 * 60));
        const minutes = Math.floor((elapsedTime % (1000 * 60 * 60)) / (1000 * 60));
        const seconds = Math.floor((elapsedTime % (1000 * 60)) / 1000);

        const timeElement = document.getElementById('loadTime');
        timeElement.textContent = `${hours < 10 ? '0' + hours : hours}:${minutes < 10 ? '0' + minutes : minutes}:${seconds < 10 ? '0' + seconds : seconds}`;
    }, 1000);

    const startTime = new Date().getTime();
};

function createSpanNumber() {

    for (let i = 1; i <= 35; i++) {
        const number = document.createElement("p");
        number.innerText = `${i}. `;

        const span = document.createElement("span");
        span.innerText = `① `;
        span.id = `choice_${i}_1`;
        number.appendChild(span);

        const span2 = document.createElement("span");
        span2.innerText = `② `;
        span2.id = `choice_${i}_2`;
        number.appendChild(span2);

        const span3 = document.createElement("span");
        span3.innerText = `③ `;
        span3.id = `choice_${i}_3`;
        number.appendChild(span3);

        const span4 = document.createElement("span");
        span4.innerText = `④`;
        span4.id = `choice_${i}_4`;
        number.appendChild(span4);

        document.getElementById("sideChoice").appendChild(number);
        if (i % 5 === 0){
            const hr = document.createElement("hr");
            hr.className = 'my-4';
            document.getElementById("sideChoice").appendChild(hr);
        }
    }
}

function updateRemainingQuestions() {
    // 총 문제 수 설정
    const totalQuestions = 35;
    const remainingQuestionsElement = document.getElementById("remainingQuestions");
    const radioButtons = document.querySelectorAll("input[type=radio]");

    const answeredQuestions = new Set();
    radioButtons.forEach(button => {
        if (button.checked) {
            const questionName = button.name;
            answeredQuestions.add(questionName);
        }
    });
    const remainingQuestions = totalQuestions - answeredQuestions.size;
    remainingQuestionsElement.textContent = `남은 문항수 : ${remainingQuestions} / ${totalQuestions}`;
}

function updateSpan( category, radio, selectSpan, spanId) {
    selectSpan.innerText ='● ';
    if (category === 'span'){
        radio.checked = true;
    }
    let arr = spanId.split("_");
    for (let i = 1; i <= 4; i++){
        if (i !== Number(spanId.substring(spanId.length - 1))){
            let leftSpan = document.querySelector('span[id="choice_'+ arr[1] +'_' + i +'"]');
            if (i === 1)leftSpan.innerText = '① ';
            if (i === 2)leftSpan.innerText = '② ';
            if (i === 3)leftSpan.innerText = '③ ';
            if (i === 4)leftSpan.innerText = '④';
        }
    }
}

function finishBtn(radios){
    let result = confirm("시험을 종료하시겠습니까?");
    if (result) {
        let arr = [];
        for (let i = 1; i <= 3; i++){
            if (!document.querySelector('[name="choice_' + i +'"]').checked){
                arr.push("choice_" + i);
            }
        }
        if (arr.length !== 0) {
            let result = confirm("풀지 않은 문제가 있습니다. 풀지 않은 문제로 이동하시겠습니까?");
            if (result) {
                document.querySelectorAll('[name="'+ arr[0]+'"]')[0].focus();
                document.querySelectorAll('[name="'+ arr[0]+'"]')[0].scrollIntoView({ behavior: 'smooth', block: 'center' });
            } else{
                alert("시험이 종료되었습니다. 수고하셨습니다.");
            }
        } else {
            alert("시험이 종료되었습니다. 수고하셨습니다.");
        }
    }

}

function save(){

    let param1 = {    "examId" : document.getElementById("examId").innerText
                         , "studentId" : "admin"
                         , "takenTime" : document.getElementById("loadTime").innerText};

    let param2 = {}; //문제번호 : 입력한답
    document.querySelectorAll('[id*="choice_"]').forEach(radio => {
        if (radio.checked) param2[radio.id.slice(7,8)] = radio.id.slice(-1);
    })

    let param = {"param1" : param1, "param2" : param2};

    fetch('/exam/saveExamResult', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(param)
    })
        .then(response)
        .catch((error) => {
            console.error('Error:', error);
        });
}
document.addEventListener('DOMContentLoaded', function() {

    updateRemainingQuestions();
    createSpanNumber();

    const spans = document.querySelectorAll('span[id*="choice"]');
    const radios = document.querySelectorAll("input[type=radio]");

    radios.forEach(radio => {
        radio.addEventListener('change', function() {
            updateRemainingQuestions();
            const radio = document.getElementById(this.id);
            const selectSpan = document.querySelector('span[id="'+ radio.id +'"]');
            const spanId = this.id;
            updateSpan('radio', radio, selectSpan, spanId);
        });
    });

    spans.forEach((span, index) => {
        span.addEventListener("click", () => {
            updateRemainingQuestions();
            const radio = document.getElementById(span.id);
            const selectSpan = document.querySelector('span[id="'+ span.id +'"]');
            const spanId = span.id;
            updateSpan('span', radio, selectSpan, spanId);
        });
    });
    document.getElementById("finishBtn").addEventListener("click", function() {
        finishBtn(radios);
    });
});
