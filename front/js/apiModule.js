const serverUrl = window.location.hostname === 'localhost'
    ? 'http://localhost:8080'
    : 'https://api.filmfly.shop';

// const imageUrl = 'https://image.tmdb.org/t/p/w600_and_h900_bestv2';
const imageUrl = 'https://image.tmdb.org/t/p/w220_and_h330_face';
let apiModule = (function() {
  // 기본 설정
  let settings = {
    baseUrl: serverUrl,
  };

  // 내부 함수: Ajax 요청을 보내는 함수
  function ajaxRequest(method, url, data, successCallback, errorCallback, options = {}) {
    let fullUrl = settings.baseUrl + url;
    let ajaxOptions = {
      url: fullUrl,
      type: method,
      headers: settings.headers,
      xhrFields: {
        withCredentials: true
      },
      success: function(result, status, xhr) {
        console.log(`요청 성공: ${method} ${fullUrl}\n`, result);
        if (successCallback) successCallback(result);
      },
      error: function(xhr, status, error) {
        console.error(`요청 실패:  ${method} ${fullUrl}\n`
            + `상태 코드 : ${xhr.status}\n`
            + `응답 데이터 : ${JSON.stringify(xhr, null , 2)}\n`
        );
        if (xhr.responseJSON.code === 401) {
          alert(`${xhr.responseJSON.message}\n로그인을 해주세요.`);
          location.href = '../html/login.html';
          return;
        }
        if (xhr.responseJSON.code === 403) {
          alert(`${xhr.responseJSON.message}\n로그인을 해주세요.`);
          location.href = '../html/login.html';
          return;
        }
        if (errorCallback) {}errorCallback(xhr, status, error);
      }
    };

    if (options.processData === false) {
      ajaxOptions.data = data;
      ajaxOptions.contentType = options.contentType;
      ajaxOptions.processData = options.processData;
    } else {
      ajaxOptions.data = data ? JSON.stringify(data) : null;
      ajaxOptions.contentType = 'application/json';
      ajaxOptions.processData = true;
    }

    $.ajax(ajaxOptions);
  }

  // GET 요청
  function GET(url, successCallback, errorCallback) {
    ajaxRequest('GET', url, null, successCallback, errorCallback);
  }

  // POST 요청
  function POST(url, data, successCallback, errorCallback, options) {
    ajaxRequest('POST', url, data, successCallback, errorCallback, options);
  }

  // PATCH 요청
  function PATCH(url, data, successCallback, errorCallback, options) {
    ajaxRequest('PATCH', url, data, successCallback, errorCallback, options);
  }

  // PUT 요청
  function PUT(url, data, successCallback, errorCallback, options) {
    ajaxRequest('PUT', url, data, successCallback, errorCallback, options);
  }

  // DELETE 요청
  function DELETE(url, data = null, successCallback, errorCallback, options) {
    ajaxRequest('DELETE', url, data, successCallback, errorCallback, options);
  }

  // 외부에 공개할 API
  return {
    GET: GET,
    POST: POST,
    PATCH: PATCH,
    PUT: PUT,
    DELETE: DELETE,
    settings: settings // 설정을 외부에서 수정 가능
  };
})();