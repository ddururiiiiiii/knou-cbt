function appConfirm(message, options = {}) {
    return new Promise((resolve) => {
        const modalEl = document.getElementById('appConfirmModal');
        document.getElementById('appConfirmModalTitle').textContent = options.title || '확인';
        document.getElementById('appConfirmModalBody').textContent = message;
        const okBtn = document.getElementById('appConfirmModalOk');
        okBtn.textContent = options.confirmText || '확인';
        document.getElementById('appConfirmModalCancel').textContent = options.cancelText || '취소';

        const modal = bootstrap.Modal.getOrCreateInstance(modalEl);
        let decided = false;

        function cleanup() {
            okBtn.removeEventListener('click', onOk);
            modalEl.removeEventListener('hidden.bs.modal', onHidden);
        }

        function onOk() {
            decided = true;
            cleanup();
            modal.hide();
            resolve(true);
        }

        function onHidden() {
            cleanup();
            if (!decided) {
                resolve(false);
            }
        }

        okBtn.addEventListener('click', onOk);
        modalEl.addEventListener('hidden.bs.modal', onHidden);
        modal.show();
    });
}

function showLoading(message) {
    const overlay = document.getElementById('appLoadingOverlay');
    document.getElementById('appLoadingMessage').textContent = message || '처리 중입니다...';
    overlay.classList.remove('d-none');
}

function hideLoading() {
    document.getElementById('appLoadingOverlay').classList.add('d-none');
}

let appToastTimer = null;

function appToast(message, options = {}) {
    const toast = document.getElementById('appToast');
    const icon = document.getElementById('appToastIcon');
    const closeBtn = document.getElementById('appToastClose');
    const type = options.type || 'info';
    const duration = options.duration || 3000;

    document.getElementById('appToastMessage').textContent = message;

    toast.classList.remove('app-toast-info', 'app-toast-warning', 'app-toast-danger', 'app-toast-success');
    toast.classList.add(`app-toast-${type}`);

    const icons = {
        info: 'bi-info-circle-fill',
        warning: 'bi-exclamation-triangle-fill',
        danger: 'bi-x-circle-fill',
        success: 'bi-check-circle-fill',
    };
    icon.className = `bi app-toast-icon ${icons[type] || icons.info}`;

    function hide() {
        toast.classList.remove('app-toast-show');
        if (appToastTimer) {
            clearTimeout(appToastTimer);
            appToastTimer = null;
        }
    }

    closeBtn.onclick = hide;

    if (appToastTimer) {
        clearTimeout(appToastTimer);
    }
    toast.classList.add('app-toast-show');
    appToastTimer = setTimeout(hide, duration);
}

function appAlert(message, options = {}) {
    return new Promise((resolve) => {
        const modalEl = document.getElementById('appAlertModal');
        document.getElementById('appAlertModalTitle').textContent = options.title || '알림';
        document.getElementById('appAlertModalBody').textContent = message;

        const modal = bootstrap.Modal.getOrCreateInstance(modalEl);

        function onHidden() {
            modalEl.removeEventListener('hidden.bs.modal', onHidden);
            resolve();
        }

        modalEl.addEventListener('hidden.bs.modal', onHidden);
        modal.show();
    });
}
