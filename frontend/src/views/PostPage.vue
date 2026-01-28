<template>
  <div class="post-page">
    <div class="container">
      <div class="post-detail" v-if="post">
        <div class="post-header card">
          <h1 class="post-title">{{ post.title }}</h1>

          <div class="post-info">
            <div class="author">
              <el-avatar :size="40" :src="post.author?.avatar">
                {{ post.author?.username?.charAt(0) }}
              </el-avatar>
              <div class="author-detail">
                <div class="author-name">{{ post.author?.username }}</div>
                <div class="post-time">{{ formatTime(post.createdAt) }}</div>
              </div>
            </div>

            <div class="post-actions">
              <el-button :type="post.liked ? 'primary' : 'default'" @click="toggleLike">
                <el-icon><Star /></el-icon>
                {{ post.likeCount }}
              </el-button>
              <el-button :type="post.favorited ? 'warning' : 'default'" @click="toggleFavorite">
                <el-icon><Collection /></el-icon>
                {{ post.favoriteCount }}
              </el-button>
            </div>
          </div>

          <p class="post-summary" v-if="post.summary">{{ post.summary }}</p>

          <div class="post-tags">
            <el-tag>{{ post.itinerary?.city }}</el-tag>
            <el-tag type="success">{{ post.itinerary?.days }}天</el-tag>
            <el-tag type="info" v-if="post.itinerary?.theme">{{ post.itinerary?.theme }}</el-tag>
          </div>
        </div>

        <div class="itinerary-preview card" v-if="itinerary">
          <h2>行程详情</h2>

          <div class="day-list">
            <div v-for="day in itinerary.dayList" :key="day.dayIndex" class="day-item">
              <div class="day-header">
                <span class="day-badge">Day {{ day.dayIndex }}</span>
                <span class="day-theme">{{ day.theme }}</span>
              </div>

              <div class="poi-list">
                <div v-for="item in day.items" :key="item.orderIndex" class="poi-item">
                  <div class="poi-time">{{ item.startTime }}</div>
                  <div class="poi-name">{{ item.poi.name }}</div>
                </div>
              </div>
            </div>
          </div>

          <el-button type="primary" @click="router.push(`/itinerary/${post.itinerary?.id}`)">
            查看完整行程
          </el-button>
        </div>
      </div>

      <el-skeleton :rows="10" animated v-else />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getPostDetail, likePost, unlikePost, favoritePost, unfavoritePost } from '@/api/post'
import { getItineraryDetail } from '@/api/itinerary'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const post = ref(null)
const itinerary = ref(null)

function formatTime(time) {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleString('zh-CN')
}

async function toggleLike() {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }

  try {
    if (post.value.liked) {
      const res = await unlikePost(post.value.id)
      post.value.liked = res.data.liked
      post.value.likeCount = res.data.likeCount
    } else {
      const res = await likePost(post.value.id)
      post.value.liked = res.data.liked
      post.value.likeCount = res.data.likeCount
    }
  } catch (error) {
    console.error('操作失败:', error)
  }
}

async function toggleFavorite() {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }

  try {
    if (post.value.favorited) {
      const res = await unfavoritePost(post.value.id)
      post.value.favorited = res.data.favorited
      post.value.favoriteCount = res.data.favoriteCount
    } else {
      const res = await favoritePost(post.value.id)
      post.value.favorited = res.data.favorited
      post.value.favoriteCount = res.data.favoriteCount
    }
  } catch (error) {
    console.error('操作失败:', error)
  }
}

onMounted(async () => {
  try {
    const res = await getPostDetail(route.params.id)
    post.value = res.data

    if (res.data.itinerary?.id) {
      const itineraryRes = await getItineraryDetail(res.data.itinerary.id)
      itinerary.value = itineraryRes.data
    }
  } catch (error) {
    console.error('获取帖子失败:', error)
    router.push('/community')
  }
})
</script>

<style lang="scss" scoped>
.post-page {
  padding: 30px 0;
}

.post-detail {
  max-width: 800px;
  margin: 0 auto;
}

.post-header {
  margin-bottom: 20px;
}

.post-title {
  font-size: 26px;
  font-weight: 600;
  margin-bottom: 20px;
  line-height: 1.4;
}

.post-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;

  .author {
    display: flex;
    align-items: center;
    gap: 12px;

    .author-name {
      font-weight: 500;
    }

    .post-time {
      font-size: 13px;
      color: #909399;
    }
  }

  .post-actions {
    display: flex;
    gap: 10px;
  }
}

.post-summary {
  font-size: 15px;
  color: #666;
  line-height: 1.8;
  margin-bottom: 20px;
}

.post-tags {
  display: flex;
  gap: 8px;
}

.itinerary-preview {
  h2 {
    font-size: 18px;
    margin-bottom: 20px;
  }
}

.day-item {
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;

  &:last-child {
    border-bottom: none;
  }
}

.day-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;

  .day-badge {
    background: #409eff;
    color: #fff;
    padding: 2px 10px;
    border-radius: 12px;
    font-size: 13px;
  }

  .day-theme {
    font-weight: 500;
  }
}

.poi-list {
  padding-left: 20px;
}

.poi-item {
  display: flex;
  gap: 12px;
  padding: 8px 0;

  .poi-time {
    color: #409eff;
    font-size: 13px;
    min-width: 50px;
  }

  .poi-name {
    font-size: 14px;
  }
}
</style>
