import axios from 'axios';
import { router } from '@/main';

/**
 * axios 인스턴스를 생성
 * 생성할때 사용하는 옵션들 (baseURL, timeout, headers 등)은 다음 URL에서 확인
    https://github.com/axios/axios 의 Request Config 챕터 확인
 */
const instance = axios.create({
  baseURL: 'http://localhost:8080',
  withCredentials: true,
  headers: {
    'Content-type': 'application/json',
  },
});

/*
    요청 인터셉터를 작성
    2개의 콜백 함수를 받는다

    1) 요청 바로 직전 - 인자값: axios config
    2) 요청 에러 - 인자값: error
*/
instance.interceptors.request.use(
  async function(config) {
    // 요청 바로 직전
    // axios 설정값에 대해 작성
    return config;
  },
  function(error) {
    // 요청 에러 처리를 작성
    return Promise.reject(error);
  }
);

let isTokenRefreshing = false;
let refreshSubscribers = [];

// 새로운 토큰이 발행되면 refreshSubscribers에 쌓인 요청들을 처리
const onTokenRefreshed = () => {
  refreshSubscribers.map((callback) => callback());
};

// 새로운 토큰이 발행되기전에 refreshSubscribers에 요청을 쌓는다.
const addRefreshSubscriber = (callback) => {
  refreshSubscribers.push(callback);
};

/*
    응답 인터셉터를 작성
    2개의 콜백 함수를 받는다

    1) 응답 정성 - 인자값: http response
    2) 응답 에러 - 인자값: http error
*/
instance.interceptors.response.use(
  function(response) {
    /*
        http status가 200인 경우
        응답 바로 직전에 대해 작성. 
        .then() 으로 이어진다.
    */
    return response;
  },

  async function(error) {
    /*
        http status가 200이 아닌 경우
        응답 에러 처리를 작성.
        .catch() 으로 이어진다.    
    */

    console.log('에러일 경우', error.config);
    const errorApi = error.config;

    if (error.response.data.status === 401) {
      if (!isTokenRefreshing) {
        errorApi.retry = true;
        console.log('토큰의 유효성이 잘못될 경우');

        // 재발급 요청
        const { data } = await instance.post('/auth/reissue');

        // 재발급 요청에 성공할 경우
        if (data.status) {
          console.log('토큰 재발급 성공');
          return axios(errorApi);
        } else {
          // 재발급 요청에 실패했을 경우
          alert('다시 로그인해주세요.');
          router.push('/');
        }

        isTokenRefreshing = false;
        // 새로운 토큰으로 지연된 요청 진행
        onTokenRefreshed();
      }
      // 토큰이 재발급 되는 동안의 요청은 refreshSubscribers에 저장
      const retryOriginalRequest = new Promise((resolve) => {
        addRefreshSubscriber(() => {
          resolve(axios(errorApi));
        });
      });

      return retryOriginalRequest;
    }

    return Promise.reject(error);
  }
);

export default instance;

// export default axios.create({
//   baseURL: 'http://localhost:8080',
//   withCredentials: true,
//   headers: {
//     'Content-type': 'application/json',
//   },
// });
