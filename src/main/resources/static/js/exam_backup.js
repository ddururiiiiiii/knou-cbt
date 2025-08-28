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

// 남은 문항 수 업데이트 함수
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
            selectSpan.innerText ='● ';

            const spanId = this.id;
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
        });
    });

    spans.forEach((span, index) => {
        span.addEventListener("click", () => {
            updateRemainingQuestions();
            const radio = document.getElementById(span.id);
            radio.checked = true;
            const selectSpan = document.querySelector('span[id="'+ span.id +'"]');
            selectSpan.innerText ='● ';

            const spanId = span.id;
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
        });
    });
});
