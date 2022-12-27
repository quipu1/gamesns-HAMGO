/*
User API 예시
 */
import http from '@/util/http-common';

const requestkakaoLogin = (data, callback, errorCallback) => {
  //백앤드와 로그인 통신하는 부분
  http
    .get('/kakaoLogin', { params: { code: data } })
    .then((res) => {
      // alert("로그인 되었습니다.");
      callback(res.data);
    })
    .catch((err) => {
      if (!err.response) {
        errorCallback(true);
      } else {
        errorCallback();
      }
    });
};

const requestExistUser = (data, callback, errorCallback) => {
  http
    .get(`/existUser/${data}`)
    .then((res) => {
      callback(res.data);
    })
    .catch((err) => {
      errorCallback();
    });
};

const requestDupCheck = (data, callback, errorCallback) => {

  http
    .get('/dupcheck', { params: { nickname: data } })
    .then((res) => {
      callback(res);
    })
    .catch(() => {
      errorCallback();
    });
};

const requestSignUp = (data, callback, errorCallback) => {
  http
    .post('/auth/signup', data)
    .then((res) => {
      // alert('회원가입에 성공했습니다.');
      callback(res.data);
    })
    .catch(() => {
      alert('회원가입에 실패했습니다.');
      errorCallback();
    });
};

const logout = (data, callback, errorCallback) => {
  http
    .get('/kakaoLogout', { params: data })
    .then(() => {
      alert('로그아웃!');
      callback();
    })
    .catch((err) => {
      alert('실패!');
      errorCallback();
    });
};

const requestFollowing = (data, callback, errorCallback) => {

  http
    .get('/follow/following', { params: data })
    .then((res) => {
      callback(res.data.object);
    })
    .catch(() => {
      errorCallback();
    });
};

const requestFollower = (data, callback, errorCallback) => {
  http
    .get('/follow/follower', { params: data })
    .then((res) => {
      callback(res.data.object);
    })
    .catch((err) => {
      alert('팔로워 가져오기 실패!');
      errorCallback();
    });
};

const requestFollowUpdate = (data, callback, errorCallback) => {

  let data2 = {
    fromNickname: data.fromNickname,
    toNickname: data.toNickname,
  };

  if (data.type >= 0) {
    http
      .post('/follow/AddOrDeleteFollowing', data2)
      .then((res) => {
        callback(res.data);
      })
      .catch(() => {
        errorCallback();
      });
  }
  if (data.type <= 0) {
    http
      .post('/follow/AddOrDeleteFollower', data2)
      .then((res) => {
        callback(res.data);
      })
      .catch(() => {
        errorCallback();
      });
  }
};

const requestUpdateUser = (formData, callback, errorCallback) => {
  http
    .put('/account/mypageUpdate', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    })
    .then((res) => {
      callback(res);
    })
    .catch(() => {
      errorCallback();
    });
};

const requestGetUser = (data, callback, errorCallback) => {

  http
    .get('/info/' + data)
    .then((res) => {
      callback(res);
    })
    .catch(() => {
      errorCallback();
    });
};

const requestFeedList = (data, callback, errorCallback) => {
  http
    .get('/board',{ params: data })
    .then((res) => {
      callback(res.data.object);
    })
    .catch(() => {
      errorCallback();
    });
};

const requestUserFeedList = (data, callback, errorCallback) => {
  http
    .get('/board/user',{ params: data })
    .then((res) => {
      callback(res.data.object);
    })
    .catch(() => {
      errorCallback();
    });
};

const requestAddReply = (data, callback, errorCallback) => {
  http
    .post(`/reply`, data)
    .then(() => {
      callback();
    })
    .catch(() => {
      errorCallback();
    });
};

const requestReplyList = (data, callback, errorCallback) => {
  http
    .get(`/reply`, { params : data })
    .then(( res ) => {
      callback(res.data.object);
    })
    .catch(() => {
      errorCallback();
    });
};

const requestSearch = (data, callback, errorCallback) => {
  http
    .get(`/search`, { params: data })
    .then(({ data }) => {
      callback(data);
    })
    .catch(() => {
      errorCallback();
    });
}

const requestHashtagBoard = (data, callback, errorCallback) => {
  http
    .get(`/search/hashtag`, { params: data })
    .then(({ data }) => {
      callback(data);
    })
    .catch(() => {
      errorCallback();
    });
}

const requestGames = (data, callback, errorCallback) => {
  http
    .get(`/matching/games`)
    .then(({ data }) => {
      callback(data);
    })
    .catch(() => {
      errorCallback();
    });
}

const requestPeople = (data, callback, errorCallback) => {
  http
    .get(`/matching/people`, { params: data })
    .then(({ data }) => {
      callback(data);
    })
    .catch(() => {
      errorCallback();
    });
}

const requestDiscord = (data, callback, errorCallback) => {
  http
    .get(`/discord/check`, { params: data })
    .then(({ data }) => {
      callback(data);
    })
    .catch(() => {
      errorCallback();
    });
}

const requestDeleteReply = (data, callback, errorCallback) => {
  http
    .delete(`/reply`, { params: data } )
    .then(({ data }) => {
      callback(data);
    })
    .catch(() => {
      errorCallback();
    });
}

const UserApi = {
  requestkakaoLogin: (data, callback, errorCallback) =>
    requestkakaoLogin(data, callback, errorCallback),
  requestSignUp: (data, callback, errorCallback) => requestSignUp(data, callback, errorCallback),
  logout: (data, callback, errorCallback) => logout(data, callback, errorCallback),
  requestFollowing: (data, callback, errorCallback) =>
    requestFollowing(data, callback, errorCallback),
  requestFollower: (data, callback, errorCallback) =>
    requestFollower(data, callback, errorCallback),
  requestDupCheck: (data, callback, errorCallback) =>
    requestDupCheck(data, callback, errorCallback),
  requestUpdateUser: (data, callback, errorCallback) =>
    requestUpdateUser(data, callback, errorCallback),
  requestFollowUpdate: (data, callback, errorCallback) =>
    requestFollowUpdate(data, callback, errorCallback),
  requestExistUser: (data, callback, errorCallback) =>
    requestExistUser(data, callback, errorCallback),
  requestGetUser: (data, callback, errorCallback) => requestGetUser(data, callback, errorCallback),
  requestFeedList: (data, callback, errorCallback) =>
    requestFeedList(data, callback, errorCallback),
  requestUserFeedList: (data, callback, errorCallback) =>
    requestUserFeedList(data, callback, errorCallback),
  requestAddReply: (data, callback, errorCallback) =>
    requestAddReply(data, callback, errorCallback),
  requestReplyList: (data, callback, errorCallback) =>
    requestReplyList(data, callback, errorCallback),
  requestSearch: (data, callback, errorCallback) =>
    requestSearch(data, callback, errorCallback),
  requestHashtagBoard: (data, callback, errorCallback) =>
    requestHashtagBoard(data, callback, errorCallback),
  requestGames: (data, callback, errorCallback) =>
    requestGames(data, callback, errorCallback),
  requestPeople: (data, callback, errorCallback) =>
    requestPeople(data, callback, errorCallback),
  requestDiscord: (data, callback, errorCallback) =>
    requestDiscord(data, callback, errorCallback),
  requestDeleteReply: (data, callback, errorCallback) =>
    requestDeleteReply(data, callback, errorCallback),
};

export default UserApi;
