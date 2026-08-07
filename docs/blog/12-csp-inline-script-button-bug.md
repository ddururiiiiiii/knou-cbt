# CSP 도입 후 버튼이 안 눌리던 문제 해결기

## 문제

XSS 방어를 위해 Content Security Policy(CSP)를 붙였다. 그런데 배포 후 여기저기서 "버튼을 눌러도 아무 반응이 없다"는 증상이 나타났다. 에러 화면이 뜨는 것도 아니고, 그냥 클릭했는데 아무 일도 안 일어나는 상태라 처음엔 원인을 짐작하기 어려웠다. 확인창(confirm)이 떠야 할 삭제 버튼도 조용히 무반응이었다.

## 원인

CSP의 `script-src` 지시문을 이렇게 설정해뒀다.

```
script-src 'self' uicdn.toast.com cdn.jsdelivr.net www.googletagmanager.com;
```

`'unsafe-inline'`이 빠져 있다. 같은 설정에서 `style-src`에는 `'unsafe-inline'`이 들어있어서 인라인 스타일은 멀쩡했지만, 스크립트 쪽은 처음부터 인라인 실행을 막고 있었던 것이다.

```
style-src 'self' cdn.jsdelivr.net uicdn.toast.com 'unsafe-inline';
```

이 프로젝트의 화면 곳곳에는 `onclick="deleteItem(1)"`처럼 HTML 태그 속성에 직접 스크립트를 박아넣은 인라인 이벤트 핸들러가 많았다. CSP가 `'unsafe-inline'`을 허용하지 않으면 브라우저는 이런 인라인 스크립트/이벤트 핸들러를 **조용히 차단**한다. 에러 화면을 띄우는 게 아니라, 그냥 실행을 안 시켜버린다. 그래서 사용자 입장에서는 "버튼이 그냥 안 눌린다"로만 보였다. 실제로는 브라우저 개발자 도구 콘솔에 CSP 위반 로그가 남지만, 일반 사용자가 콘솔을 열어볼 리는 없으니 증상만 보면 원인을 짐작하기 어려웠다.

## 해결 — `'unsafe-inline'`을 허용하는 대신, 인라인 스크립트를 전부 외부 파일로 뺐다

CSP를 느슨하게 풀어서 `'unsafe-inline'`을 script-src에 추가하는 것도 방법이었지만, 그건 CSP를 도입한 원래 목적(XSS 방어)을 스스로 무력화하는 선택이었다. 그래서 반대로 갔다 — 인라인 스크립트를 전부 외부 `.js` 파일로 분리했다.

```
exam-list.js, exam-solve.js, question-manage.js,
subject-list.js, ui-dialog.js, confirm-delete.js,
notice-form.js, login.js  ...
```

화면마다 별도 JS 파일을 만들고, `onclick="..."` 속성 대신 `addEventListener`로 이벤트를 바인딩하는 방식으로 바꿨다. 문제 등록 화면(`questionManage.html`)처럼 인라인 스크립트가 많이 몰려있던 화면은 diff가 300줄 넘게 나올 정도로 큰 리팩터링이었다.

```html
<!-- Before -->
<button onclick="deleteQuestion(1)">삭제</button>

<!-- After -->
<button class="js-delete-question" data-id="1">삭제</button>
```

```js
// question-manage.js
document.querySelectorAll('.js-delete-question').forEach(btn => {
    btn.addEventListener('click', () => deleteQuestion(btn.dataset.id));
});
```

## 교훈

- CSP는 위반해도 에러를 던지지 않고 "조용히 차단"한다는 게 제일 무서운 특성이다. YAML 들여쓰기 오류가 조용히 무시되는 것과 비슷하게, 실패가 소리 없이 일어나는 종류의 버그는 발견까지 시간이 오래 걸린다.
- 새 보안 헤더(CSP, CORS 등)를 도입할 때는 반드시 실제 화면에서 버튼 클릭, 폼 제출, 확인창 노출 같은 상호작용을 전부 눌러보는 스모크 테스트가 필요하다. 정책을 "설정했다"와 "실제로 기능이 안 깨졌다"는 별개다.
- `'unsafe-inline'`을 허용해서 문제를 우회하는 건 제일 쉬운 길이지만, CSP를 도입한 목적 자체를 무력화한다. 조금 번거롭더라도 인라인 스크립트를 걷어내는 쪽이 장기적으로 맞는 방향이었다.
