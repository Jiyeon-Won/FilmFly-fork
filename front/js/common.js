$(function () {
    // 헤더 삽입
    $('#common-header').load('../common/common-header.html', function () {
        // checkLoginStatusWithoutCookie();

        // 초기 상태 설정
        updateAuthLinks();
    });
});

function checkLoginStatusWithoutCookie() {
    let status = false;
    apiModule.GET("/users/myInfo",
        function (response) {
            $('#loginLink').hide();
            $('#signupLink').hide();
            $('#logoutLink').show();
            $('#myPageLink').show();
        }, function () {
            $('#loginLink').show();
            $('#signupLink').show();
            $('#logoutLink').hide();
            $('#myPageLink').hide();
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
    console.log("isLoggedIn: " + isLoggedIn);
    console.log("loginTime: " + loginTime);
    console.log("currentTime: " + currentTime);
    console.log("accessToken: " + accessToken);
    // accessToken이 없으면 로그아웃 처리
    if (!accessToken) {
        console.log("1번");
        if (isLoggedIn) {
            console.log("2번");
            localStorage.setItem('isLoggedIn', 'false');
            localStorage.removeItem('loginTime');
        }
        return false;
    }

    // 로그인 시간이 설정되어 있고, 현재 시간이 로그인 시간보다 크면 세션 만료
    if (loginTime && currentTime > parseInt(loginTime)) {
        console.log("3번");
        if (isLoggedIn) {
            console.log("4번");
            localStorage.setItem('isLoggedIn', 'false');
            localStorage.removeItem('loginTime');
            alert('세션이 만료되었습니다. 다시 로그인해 주세요.');
            location.href = '../html/login.html';
        }
        return false;
    }

    console.log("로그인 되어 있음");
    return isLoggedIn;
}

// 쿠키에서 accessToken 가져오기 함수
function getCookie(name) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop().split(';').shift();
}