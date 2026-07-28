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
