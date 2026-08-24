document.addEventListener("DOMContentLoaded", () => {
    const content = document.querySelector('#content')?.value || '';

    const editor = new toastui.Editor({
        el: document.querySelector('#editor'),
        height: '400px',
        initialEditType: 'wysiwyg',
        previewStyle: 'vertical',
        initialValue: content,
        hooks: {
            addImageBlobHook: async (blob, callback) => {
                const formData = new FormData();
                formData.append("file", blob);

                try {
                    const response = await fetchWithTimeout("/api/notices/image", {
                        method: "POST",
                        body: formData
                    }, 15000);
                    const result = await response.json();
                    if (result.success) {
                        callback(result.url, blob.name);
                    } else {
                        appAlert("이미지 업로드에 실패했습니다.");
                    }
                } catch (err) {
                    appAlert("이미지 업로드 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
                }
            }
        }
    });

    const form = document.getElementById("noticeForm");
    form.addEventListener("submit", (e) => {
        e.preventDefault();
        document.getElementById("content").value = editor.getHTML();
        form.submit();
    });
});
