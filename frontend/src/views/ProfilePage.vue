<template>
  <div class="profile-page">
    <div class="container">
      <div class="profile-header card">
        <el-avatar :size="80" :src="userStore.user?.avatar">
          {{ userStore.user?.username?.charAt(0) }}
        </el-avatar>
        <div class="user-info">
          <h2 class="username">{{ userStore.user?.username }}</h2>
          <p class="email">{{ userStore.user?.email }}</p>
        </div>
      </div>

      <el-tabs v-model="activeTab" class="profile-tabs">
        <el-tab-pane label="我的行程" name="itineraries">
          <div class="list-container" v-loading="loading.itineraries">
            <div
              v-for="item in itineraries"
              :key="item.itineraryId"
              class="list-item card"
              @click="router.push(`/itinerary/${item.itineraryId}`)"
            >
              <div class="item-info">
                <h3>{{ item.title || `${item.city}${item.days}日游` }}</h3>
                <div class="item-meta">
                  <el-tag size="small">{{ item.city }}</el-tag>
                  <el-tag size="small" type="success">{{ item.days }}天</el-tag>
                  <el-tag size="small" type="info" v-if="item.theme">{{ item.theme }}</el-tag>
                </div>
              </div>
              <el-button type="danger" text @click.stop="handleDeleteItinerary(item.itineraryId)">
                删除
              </el-button>
            </div>

            <el-empty v-if="!loading.itineraries && itineraries.length === 0" description="暂无行程" />
          </div>
        </el-tab-pane>

        <el-tab-pane label="我的发布" name="posts">
          <div class="list-container" v-loading="loading.posts">
            <div
              v-for="post in myPosts"
              :key="post.id"
              class="list-item card"
              @click="router.push(`/post/${post.id}`)"
            >
              <div class="item-info">
                <h3>{{ post.title }}</h3>
                <div class="item-stats">
                  <span><el-icon><Star /></el-icon> {{ post.likeCount }}</span>
                  <span><el-icon><Collection /></el-icon> {{ post.favoriteCount }}</span>
                </div>
              </div>
            </div>

            <el-empty v-if="!loading.posts && myPosts.length === 0" description="暂无发布" />
          </div>
        </el-tab-pane>

        <el-tab-pane label="我的收藏" name="favorites">
          <div class="list-container" v-loading="loading.favorites">
            <div
              v-for="post in favorites"
              :key="post.id"
              class="list-item card"
              @click="router.push(`/post/${post.id}`)"
            >
              <div class="item-info">
                <h3>{{ post.title }}</h3>
                <div class="item-meta">
                  <span>{{ post.author?.username }}</span>
                </div>
              </div>
            </div>

            <el-empty v-if="!loading.favorites && favorites.length === 0" description="暂无收藏" />
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { getMyItineraries, deleteItinerary } from '@/api/itinerary'
import { getMyPosts, getMyFavorites } from '@/api/post'

const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('itineraries')
const itineraries = ref([])
const myPosts = ref([])
const favorites = ref([])

const loading = ref({
  itineraries: false,
  posts: false,
  favorites: false
})

async function loadItineraries() {
  loading.value.itineraries = true
  try {
    const res = await getMyItineraries({ page: 1, size: 50 })
    itineraries.value = res.data.list
  } catch (error) {
    console.error('获取行程失败:', error)
  } finally {
    loading.value.itineraries = false
  }
}

async function loadMyPosts() {
  loading.value.posts = true
  try {
    const res = await getMyPosts({ page: 1, size: 50 })
    myPosts.value = res.data.list
  } catch (error) {
    console.error('获取帖子失败:', error)
  } finally {
    loading.value.posts = false
  }
}

async function loadFavorites() {
  loading.value.favorites = true
  try {
    const res = await getMyFavorites({ page: 1, size: 50 })
    favorites.value = res.data.list
  } catch (error) {
    console.error('获取收藏失败:', error)
  } finally {
    loading.value.favorites = false
  }
}

async function handleDeleteItinerary(id) {
  try {
    await ElMessageBox.confirm('确定要删除这个行程吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await deleteItinerary(id)
    ElMessage.success('删除成功')
    loadItineraries()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

watch(activeTab, (tab) => {
  if (tab === 'itineraries' && itineraries.value.length === 0) {
    loadItineraries()
  } else if (tab === 'posts' && myPosts.value.length === 0) {
    loadMyPosts()
  } else if (tab === 'favorites' && favorites.value.length === 0) {
    loadFavorites()
  }
})

onMounted(() => {
  loadItineraries()
})
</script>

<style lang="scss" scoped>
.profile-page {
  padding: 30px 0;
}

.profile-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 30px;

  .user-info {
    .username {
      font-size: 24px;
      font-weight: 600;
      margin-bottom: 5px;
    }

    .email {
      color: #909399;
    }
  }
}

.profile-tabs {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.list-container {
  min-height: 200px;
}

.list-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  cursor: pointer;
  transition: box-shadow 0.3s;

  &:hover {
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  }

  .item-info {
    h3 {
      font-size: 16px;
      font-weight: 500;
      margin-bottom: 8px;
    }

    .item-meta {
      display: flex;
      gap: 8px;
    }

    .item-stats {
      display: flex;
      gap: 15px;
      font-size: 13px;
      color: #909399;

      span {
        display: flex;
        align-items: center;
        gap: 4px;
      }
    }
  }
}
</style>
