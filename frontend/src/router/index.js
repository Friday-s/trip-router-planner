import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/',
    redirect: '/generate'
  },
  {
    path: '/generate',
    name: 'Generate',
    component: () => import('@/views/GeneratePage.vue'),
    meta: { title: '生成行程' }
  },
  {
    path: '/itinerary/:id',
    name: 'Itinerary',
    component: () => import('@/views/ItineraryPage.vue'),
    meta: { title: '行程详情' }
  },
  {
    path: '/community',
    name: 'Community',
    component: () => import('@/views/CommunityPage.vue'),
    meta: { title: '社区广场' }
  },
  {
    path: '/post/:id',
    name: 'Post',
    component: () => import('@/views/PostPage.vue'),
    meta: { title: '帖子详情' }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/LoginPage.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/RegisterPage.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/ProfilePage.vue'),
    meta: { title: '个人中心', requiresAuth: true }
  },
  {
    path: '/admin',
    redirect: '/admin/pois'
  },
  {
    path: '/admin/pois',
    name: 'AdminPois',
    component: () => import('@/views/admin/PoiManage.vue'),
    meta: { title: 'POI管理', requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/posts',
    name: 'AdminPosts',
    component: () => import('@/views/admin/PostManage.vue'),
    meta: { title: '帖子管理', requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/stats',
    name: 'AdminStats',
    component: () => import('@/views/admin/StatsPage.vue'),
    meta: { title: '数据统计', requiresAuth: true, requiresAdmin: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 大理旅游规划` : '大理旅游规划'

  const userStore = useUserStore()

  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next({ name: 'Login', query: { redirect: to.fullPath } })
    return
  }

  if (to.meta.requiresAdmin && userStore.user?.role !== 'admin') {
    next({ name: 'Generate' })
    return
  }

  next()
})

export default router
