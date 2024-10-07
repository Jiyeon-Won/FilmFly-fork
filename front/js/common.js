$(function () {
    // 헤더 삽입
    $('#common-header').load('../common/common-header.html', function () {
        // checkLoginStatusWithoutCookie();

        // 초기 상태 설정
        updateAuthLinks();

        logout();
    });
});

function logout() {
    $('#logoutLink').on('click', function () {
        apiModule.POST("/users/logout", null, function (result, status, xhr) {
            updateAuthLinks();
            alert("로그아웃 성공");
        }, function (xhr, status, er) {
            alert("로그아웃 실패");
        });
    });
}

// 로그인 상태에 따라 버튼 표시
function updateAuthLinks() {
    if (checkLoginStatus()) {
        $('#loginLink').hide();
        $('#signupLink').hide();
        $('#logoutLink').show();
        $('#myPageLink').show();
    } else {
        $('#loginLink').show();
        $('#signupLink').show();
        $('#logoutLink').hide();
        $('#myPageLink').hide();
    }
}

// 로그인 상태 확인 함수
function checkLoginStatus() {
    const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true';
    const loginTime = localStorage.getItem('loginTime');
    const currentTime = new Date().getTime();
    const accessToken = getCookie('accessToken');
    // accessToken이 없으면 로그아웃 처리
    if (!accessToken) {
        if (isLoggedIn) {
            localStorage.setItem('isLoggedIn', 'false');
            localStorage.removeItem('loginTime');
        }
        return false;
    }

    // 로그인 시간이 설정되어 있고, 현재 시간이 로그인 시간보다 크면 세션 만료
    if (loginTime && currentTime > parseInt(loginTime)) {
        if (isLoggedIn) {
            localStorage.setItem('isLoggedIn', 'false');
            localStorage.removeItem('loginTime');
            alert('세션이 만료되었습니다. 다시 로그인해 주세요.');
            location.href = '../html/login.html';
        }
        return false;
    }

    return isLoggedIn;
}

// 쿠키에서 accessToken 가져오기 함수
function getCookie(name) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop().split(';').shift();
}