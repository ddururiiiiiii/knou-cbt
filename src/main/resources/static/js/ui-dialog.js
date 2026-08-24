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

let loadingStallTimer = null;

function showLoading(message) {
    const overlay = document.getElementById('appLoadingOverlay');
    document.getElementById('appLoadingMessage').textContent = message || '처리 중입니다...';
    overlay.classList.remove('d-none');

    if (loadingStallTimer) {
        clearTimeout(loadingStallTimer);
    }
    // 서버 응답이 비정상적으로 오래 걸리는 경우(예: DB 커넥션 풀 대기) 무한 로딩처럼 보이지 않도록 안내 문구로 교체
    loadingStallTimer = setTimeout(function () {
        document.getElementById('appLoadingMessage').textContent =
            '예상보다 시간이 걸리고 있어요. 인터넷 연결을 확인하거나 잠시 후 새로고침해주세요.';
    }, 15000);
}

function hideLoading() {
    document.getElementById('appLoadingOverlay').classList.add('d-none');
    if (loadingStallTimer) {
        clearTimeout(loadingStallTimer);
        loadingStallTimer = null;
    }
}

// 지정 시간 내 응답이 없으면 요청을 중단하는 fetch 래퍼
function fetchWithTimeout(url, options = {}, timeoutMs = 8000) {
    const controller = new AbortController();
    const timer = setTimeout(() => controller.abort(), timeoutMs);
    return fetch(url, {...options, signal: controller.signal}).finally(() => clearTimeout(timer));
}

function handleFetchError(err) {
    if (err && err.name === 'AbortError') {
        appToast('서버 응답이 지연되고 있어요. 잠시 후 다시 시도해주세요.', {type: 'danger'});
    } else {
        appToast('일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요.', {type: 'danger'});
    }
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
