// 로그인 페이지에서 "최근에 이 소셜로 로그인했어요" 배지를 표시해준다.
// 서버 세션이 없는 상태(로그인 전)에서도 봐야 하므로 쿠키(last_login_provider)를 읽어서 처리한다.
(function () {
    function readCookie(name) {
        const match = document.cookie.match(new RegExp('(?:^|; )' + name + '=([^;]*)'));
        return match ? decodeURIComponent(match[1]) : null;
    }

    const lastProvider = readCookie('last_login_provider');
    if (!lastProvider) return;

    const btn = document.querySelector('.social-login-btn[data-provider="' + lastProvider + '"]');
    if (!btn) return;

    const badge = btn.querySelector('.recent-badge');
    if (badge) badge.hidden = false;
})();
