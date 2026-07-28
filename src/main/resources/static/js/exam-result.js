document.addEventListener("DOMContentLoaded", function () {
    document.getElementById("retryBtn").addEventListener("click", function (e) {
        e.preventDefault();
        const url = this.href;
        appConfirm("시험을 다시 풉니다. 이전 결과는 사라져요. 계속할까요?").then(function (ok) {
            if (ok) {
                window.location.href = url;
            }
        });
    });
});
