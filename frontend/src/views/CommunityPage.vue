<template>
  <div class="community-page">
    <div class="container">
      <div class="page-header">
        <h1 class="page-title">社区广场</h1>
        <div class="sort-tabs">
          <el-radio-group v-model="sortType" @change="loadPosts">
            <el-radio-button value="latest">最新发布</el-radio-button>
            <el-radio-button value="hot">最受欢迎</el-radio-button>
          </el-radio-group>
        </div>
      </div>

      <div class="post-grid" v-loading="loading">
        <div
          v-for="post in posts"
          :key="post.id"
          class="post-card card"
          @click="router.push(`/post/${post.id}`)"
        >
          <div class="post-header">
            <el-avatar :size="36" :src="post.author?.avatar">
              {{ post.author?.username?.charAt(0) }}
            </el-avatar>
            <div class="author-info">
              <div class="author-name">{{ post.author?.username }}</div>
              <div class="post-time">{{ formatTime(post.createdAt) }}</div>
            </div>
          </div>

          <h3 class="post-title">{{ post.title }}</h3>
          <p class="post-summary">{{ post.summary }}</p>

          <div class="post-meta">
            <el-tag size="small">{{ post.itinerary?.city }}</el-tag>
            <el-tag size="small" type="success">{{ post.itinerary?.days }}天</el-tag>
            <el-tag size="small" type="info" v-if="post.itinerary?.theme">{{ post.itinerary?.theme }}</el-tag>
          </div>

          <div class="post-stats">
            <span>
              <el-icon><Star /></el-icon>
              {{ post.likeCount }}
            </span>
            <span>
              <el-icon><Collection /></el-icon>
              {{ post.favoriteCount }}
            </span>
          </div>
        </div>
      </div>

      <div class="pagination-container" v-if="total > pageSize">
        <el-pagination
          v-model:current-page="page"
          :page-size="pageSize"
          :total="total"
          layout="prev, pager, next"
          @current-change="loadPosts"
        />
      </div>

      <el-empty v-if="!loading && posts.length === 0" description="暂无帖子" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getPostList } from '@/api/post'

const router = useRouter()

const posts = ref([])
const loading = ref(false)
const sortType = ref('latest')
const page = ref(1)
const pageSize = ref(12)
const total = ref(0)

function formatTime(time) {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleDateString('zh-CN')
}

async function loadPosts() {
  loading.value = true

  try {
    const res = await getPostList({
      sort: sortType.value,
      page: page.value,
      size: pageSize.value
    })
    posts.value = res.data.list
    total.value = res.data.total
  } catch (error) {
    console.error('获取帖子失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadPosts()
})
</script>

<style lang="scss" scoped>
.community-page {
  padding: 30px 0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.post-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  min-height: 300px;
}

.post-card {
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  }
}

.post-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;

  .author-info {
    .author-name {
      font-size: 14px;
      font-weight: 500;
    }

    .post-time {
      font-size: 12px;
      color: #909399;
    }
  }
}

.post-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 10px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.post-summary {
  font-size: 13px;
  color: #666;
  line-height: 1.6;
  margin-bottom: 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.post-meta {
  display: flex;
  gap: 6px;
  margin-bottom: 12px;
}

.post-stats {
  display: flex;
  gap: 20px;
  padding-top: 12px;
  border-top: 1px solid #eee;

  span {
    display: flex;
    align-items: center;
    gap: 4px;
    font-size: 13px;
    color: #909399;
  }
}

.pagination-container {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}

@media (max-width: 1024px) {
  .post-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 640px) {
  .post-grid {
    grid-template-columns: 1fr;
  }
}
</style>
