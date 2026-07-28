document.addEventListener("submit", function (e) {
    const form = e.target.closest("form[data-confirm]");
    if (!form) {
        return;
    }
    e.preventDefault();
    appConfirm(form.dataset.confirm).then(function (ok) {
        if (ok) {
            form.submit();
        }
    });
});

document.addEventListener("click", function (e) {
    const btn = e.target.closest("[data-alert]");
    if (btn) {
        appAlert(btn.dataset.alert);
    }
});
