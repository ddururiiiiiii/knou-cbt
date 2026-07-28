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

                const response = await fetch("/api/notices/image", {
                    method: "POST",
                    body: formData
                });
                const result = await response.json();
                if (result.success) {
                    callback(result.url, blob.name);
                } else {
                    appAlert("이미지 업로드에 실패했습니다.");
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
