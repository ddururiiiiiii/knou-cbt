# 프레임워크 없이 커스텀 Alert/Confirm/Toast 만들기

## 문제

브라우저 네이티브 `alert()`/`confirm()`은 못생겼다는 이유만으로 문제인 게 아니다. 진짜 문제는 **막는다(blocking)**는 점이다. `confirm()`이 뜨는 동안 자바스크립트 실행이 통째로 멈추고, 브라우저마다 스타일도 제각각이라 서비스 전체의 디자인 톤과 완전히 따로 논다. 삭제 확인, 저장 완료 알림처럼 화면 곳곳에서 이런 상호작용이 필요한데, 매번 네이티브 다이얼로그를 쓰면 사용자 경험이 서비스 디자인과 계속 어긋났다.

그렇다고 무거운 UI 프레임워크(React, Vue 같은)를 새로 끌어들이는 것도 배보다 배꼽이 더 큰 선택이었다. 이 프로젝트는 Thymeleaf 서버사이드 렌더링 + 바닐라 JS 조합인데, 다이얼로그 하나 예쁘게 만들자고 프론트엔드 프레임워크를 통째로 얹을 이유는 없었다.

## 접근 — Bootstrap은 이미 쓰고 있으니, 그 위에 Promise 래퍼만 얹는다

이 프로젝트는 이미 Bootstrap을 쓰고 있었다. Bootstrap의 `Modal` 컴포넌트 자체는 이미 스타일도 통일돼 있고 접근성도 어느 정도 갖춰져 있어서, 굳이 처음부터 새로 만들 이유가 없었다. 문제는 Bootstrap Modal의 기본 사용법이 콜백 기반(`show.bs.modal`, `hidden.bs.modal` 이벤트 리스너)이라, 코드에서 "사용자가 확인을 눌렀는지"를 물어보고 그 결과를 기다렸다가 다음 동작을 하는 흐름을 짜려면 콜백이 계속 중첩되기 쉬웠다.

그래서 Bootstrap Modal 위에 **Promise 기반 async/await 래퍼**를 씌웠다.

```js
function appConfirm(message, options = {}) {
    return new Promise((resolve) => {
        const modalEl = document.getElementById('appConfirmModal');
        document.getElementById('appConfirmModalTitle').textContent = options.title || '확인';
        document.getElementById('appConfirmModalBody').textContent = message;
        const okBtn = document.getElementById('appConfirmModalOk');

        const modal = bootstrap.Modal.getOrCreateInstance(modalEl);
        let decided = false;

        function onOk() {
            decided = true;
            cleanup();
            modal.hide();
            resolve(true);
        }
        function onHidden() {
            cleanup();
            if (!decided) resolve(false); // 확인 없이 닫으면(esc, 바깥 클릭) false
        }
        function cleanup() {
            okBtn.removeEventListener('click', onOk);
            modalEl.removeEventListener('hidden.bs.modal', onHidden);
        }

        okBtn.addEventListener('click', onOk);
        modalEl.addEventListener('hidden.bs.modal', onHidden);
        modal.show();
    });
}
```

이렇게 만들어두면 호출부는 네이티브 `confirm()`을 쓰던 것과 거의 똑같은 모양으로 쓸 수 있다.

```js
if (await appConfirm('정말 삭제하시겠습니까?')) {
    // 삭제 진행
}
```

콜백 지옥 없이, 그러면서도 네이티브 `confirm()`처럼 "다음 줄로 넘어가기 전에 사용자의 선택을 기다린다"는 직관적인 흐름을 그대로 유지할 수 있었다.

`appAlert()`도 같은 패턴으로 Bootstrap Modal을 감쌌고, 로딩 오버레이(`showLoading`/`hideLoading`)도 같이 만들어서 저장 버튼을 눌렀을 때 "처리 중입니다..." 같은 표시가 나오게 했다.

## 토스트는 다르게 — Bootstrap Toast 대신 완전히 직접 구현

확인/알림 모달과 달리, 짧게 떴다 사라지는 토스트 알림(`appToast`)은 Bootstrap의 Toast 컴포넌트를 쓰지 않고 CSS 클래스 기반으로 완전히 새로 만들었다.

```js
function appToast(message, options = {}) {
    const toast = document.getElementById('appToast');
    const type = options.type || 'info';
    const duration = options.duration || 3000;

    document.getElementById('appToastMessage').textContent = message;
    toast.classList.remove('app-toast-info', 'app-toast-warning', 'app-toast-danger', 'app-toast-success');
    toast.classList.add(`app-toast-${type}`);

    toast.classList.add('app-toast-show');
    setTimeout(() => toast.classList.remove('app-toast-show'), duration);
}
```

`info`/`warning`/`danger`/`success` 네 가지 타입에 맞춰 아이콘과 색을 바꿔주는 정도만 있으면 충분했고, Bootstrap Toast가 제공하는 기능(스택 쌓기, 위치 옵션 등)까지는 필요 없었다. 그래서 이 부분만큼은 프레임워크 컴포넌트를 안 쓰고 직접 만드는 게 오히려 더 가볍고 이 프로젝트에 맞았다.

## 정리 — "프레임워크 없이"의 정확한 의미

이 글의 제목에 낚일 수 있어서 짚어둔다. "프레임워크 없이"는 React/Vue 같은 새 UI 프레임워크를 끌어들이지 않았다는 뜻이지, 이미 쓰고 있던 Bootstrap 자체를 걷어냈다는 뜻은 아니다. `appConfirm`/`appAlert`는 Bootstrap Modal을 그대로 쓰되 네이티브 `alert`/`confirm`처럼 쓸 수 있게 Promise로 감쌌고, `appToast`만 Bootstrap 컴포넌트 없이 순수 CSS/JS로 새로 만들었다. 지금은 코드베이스 전체에서 네이티브 `alert()`/`confirm()` 사용처는 하나도 남아있지 않다.

## 교훈

- 이미 쓰고 있는 라이브러리(Bootstrap)의 저수준 컴포넌트(Modal)를, 필요한 만큼만 얇게 감싸는 것으로도 네이티브 다이얼로그의 사용성 문제(블로킹, 디자인 불일치)를 대부분 해결할 수 있었다. 새 프레임워크를 도입하는 것만이 답은 아니다.
- 콜백 기반 API를 Promise로 감싸면 `async/await`으로 호출부를 깔끔하게 쓸 수 있다는 건 잘 알려진 패턴이지만, 실제로 "확인 버튼을 눌렀을 때"와 "esc나 바깥 클릭으로 그냥 닫았을 때"를 모두 `resolve`로 처리해야 한다는 디테일(`decided` 플래그)은 막상 만들어보지 않으면 놓치기 쉬웠다.
- 모든 UI 요소를 같은 방식으로 만들 필요는 없다. 모달 두 개는 기존 컴포넌트를 감쌌고, 토스트 하나는 새로 만들었다 — 각 컴포넌트가 필요로 하는 기능의 크기에 맞춰 구현 방식을 다르게 가져간 게 오히려 전체 코드량을 줄였다.
