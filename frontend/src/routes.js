import Login from './views/user/Login.vue';
import Loading from '@/views/user/UserLoading.vue';
import Join from './views/user/Join.vue';
import Main from './views/feed/Main.vue';
import NotFound from './views/error/PageNotFound.vue';
import ErrorP from './views/error/Error.vue';
import MyEdit from './views/user/MyEdit.vue';
import MyPage from './views/user/MyPage.vue';
import Following from './views/user/Following.vue';
import Follower from './views/user/Follower.vue';
import Search from './components/home/search/Search.vue';
import Hashtagsearch from './components/home/search/Hashtagsearch.vue';
import MatchingBefore from './components/home/matching/MatchingBefore.vue';
import Matching from './components/home/matching/Matching.vue';
import MatchingStart from './components/home/matching/MatchingStart.vue';
import MatchingResult from './components/home/matching/MatchingResult.vue';
import Writing from './components/home/writing/Writing.vue';
import Alarm from './views/alarm/Alarm.vue';
import UserPage from './views/user/UserPage.vue';
import InChatting from './components/home/chatting/InChatting.vue';
import Chatting from './components/home/chatting/Chatting.vue';

export default [
  {
    path: '/',
    name: 'Login',
    component: Login,
  },
  {
    path: '/user/loading',
    name: 'Loading',
    component: Loading,
  },
  {
    path: '/user/join',
    name: 'Join',
    component: Join,
  },
  {
    path: '/main',
    name: 'Main',
    component: Main,
  },
  {
    path: '/userpage',
    name: 'UserPage',
    component: UserPage,
  },
  {
    path: '/hashtagsearch/:hashtag',
    name: 'Hashtagsearch',
    component: Hashtagsearch,
  },
  {
    path: '/mypage/edit',
    name: 'MyEdit',
    component: MyEdit,
  },
  {
    path: '/following',
    name: 'Following',
    component: Following,
  },
  {
    path: '/follower',
    name: 'Follower',
    component: Follower,
  },
  {
    path: '/error',
    name: 'error',
    component: ErrorP,
  },
  {
    path: '/404',
    name: 'notFound',
    component: NotFound,
  },
  {
    path: '/MyPage',
    name: 'MyPage',
    component: MyPage,
  },
  {
    path: '/search',
    name: 'Search',
    component: Search,
  },
  {
    path: '/writing',
    name: 'Writing',
    component: Writing,
  },
  {
    path: '/inChatting',
    name: 'InChatting',
    component: InChatting,
  },
  {
    path: '/chatting',
    name: 'Chatting',
    component: Chatting,
  },
  {
    path: '/matchingBefore',
    name: 'MatchingBefore',
    component: MatchingBefore,
  },
  {
    path: '/matching',
    name: 'Matching',
    component: Matching,
  },
  {
    path: '/matchingStart',
    name: 'MatchingStart',
    component: MatchingStart,
  },
  {
    path: '/matchingResult',
    name: 'MatchingResult',
    component: MatchingResult,
  },
  {
    path: '/alarm',
    name: 'Alarm',
    component: Alarm,
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/404',
  },
];
