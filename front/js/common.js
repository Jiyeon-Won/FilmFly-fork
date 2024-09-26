$(function () {
    // 헤더 삽입
    $('#common-header').load('../common/common-header.html', function () {
        // function checkLoginStatusWithoutCookie() {
        //     let status = false;
        //     apiModule.GET("/users/myInfo",
        //         function (response) {
        //             console.log("유저 정보 호출 Success");
        //             $('#loginLink').hide();
        //             $('#signupLink').hide();
        //             $('#logoutLink').show();
        //             $('#myPageLink').show();
        //         }, function () {
        //             console.log("유저 정보 호출 Failed");
        //             $('#loginLink').show();
        //             $('#signupLink').show();
        //             $('#logoutLink').hide();
        //             $('#myPageLink').hide();
        //         });
        //     console.log('status: ' + status);
        // }
        //
        // checkLoginStatusWithoutCookie();

        // 쿠키에서 accessToken 가져오기 함수
        function getCookie(name) {
            const value = `; ${document.cookie}`;
            const parts = value.split(`; ${name}=`);
            if (parts.length === 2) return parts.pop().split(';').shift();
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
                    localStorage.removeItem('userRole');
                    localStorage.removeItem('loginTime');
                }
                return false;
            }

            // 로그인 시간이 설정되어 있고, 현재 시간이 로그인 시간보다 크면 세션 만료
            if (loginTime && currentTime > parseInt(loginTime)) {
                if (isLoggedIn) {
                    localStorage.setItem('isLoggedIn', 'false');
                    localStorage.removeItem('userRole');
                    localStorage.removeItem('loginTime');
                    alert('세션이 만료되었습니다. 다시 로그인해 주세요.');
                    location.href = '../html/login.html';
                }
                return false;
            }

            return isLoggedIn;
        }

        // 로그인 상태에 따라 버튼 표시
        function updateAuthLinks() {
            const userRole = localStorage.getItem('userRole');
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

        // 초기 상태 설정
        updateAuthLinks();

        // 로그아웃 버튼 클릭 이벤트
        $('#logoutBtn').on('click', function (event) {
            event.preventDefault();
            if (confirm('로그아웃 하시겠습니까?')) {
                apiModule.POST('/users/logout', null,
                    function (result) {
                        alert('로그아웃 되었습니다.');
                        localStorage.setItem('isLoggedIn', 'false'); // 로그인 상태 업데이트
                        localStorage.removeItem('userRole'); // 사용자 역할 정보 제거
                        localStorage.removeItem('loginTime'); // 로그인 시간 제거
                        updateAuthLinks();
                        // location.href = '../html/index.html';
                        location.reload();
                    },
                    function (xhr) {
                        alert('로그아웃에 실패했습니다.');
                    });
            }
        });
    });
});