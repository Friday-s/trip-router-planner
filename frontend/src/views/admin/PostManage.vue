<template>
  <div class="admin-page">
    <div class="admin-sidebar">
      <div class="admin-logo">管理后台</div>
      <el-menu :default-active="$route.path" router>
        <el-menu-item index="/admin/pois">
          <el-icon><Location /></el-icon>
          <span>POI管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/posts">
          <el-icon><Document /></el-icon>
          <span>帖子管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/stats">
          <el-icon><DataAnalysis /></el-icon>
          <span>数据统计</span>
        </el-menu-item>
        <el-menu-item index="/" class="back-item">
          <el-icon><Back /></el-icon>
          <span>返回前台</span>
        </el-menu-item>
      </el-menu>
    </div>

    <div class="admin-content">
      <div class="admin-header">
        <h2>帖子管理</h2>
        <div class="admin-user">
          <span>{{ userStore.user?.username }}</span>
          <el-button text @click="handleLogout">退出</el-button>
        </div>
      </div>

      <div class="admin-main">
        <div class="page-container">
          <div class="page-header">
            <div class="filter-box">
              <el-select v-model="filterStatus" placeholder="状态筛选" clearable @change="loadPosts">
                <el-option label="正常" :value="1" />
                <el-option label="下架" :value="0" />
              </el-select>
            </div>

            <div class="search-box">
              <el-input
                v-model="searchKeyword"
                placeholder="搜索标题"
                clearable
                @clear="loadPosts"
                @keyup.enter="loadPosts"
              >
                <template #append>
                  <el-button @click="loadPosts">
                    <el-icon><Search /></el-icon>
                  </el-button>
                </template>
              </el-input>
            </div>
          </div>

          <el-table :data="posts" v-loading="loading" stripe>
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="title" label="标题" min-width="200" />
            <el-table-column label="作者" width="120">
              <template #default="{ row }">{{ row.author?.username }}</template>
            </el-table-column>
            <el-table-column prop="likeCount" label="点赞" width="80" />
            <el-table-column prop="favoriteCount" label="收藏" width="80" />
            <el-table-column prop="status" label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'danger'">
                  {{ row.status === 1 ? '正常' : '下架' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="发布时间" width="180">
              <template #default="{ row }">{{ formatTime(row.createdAt) }}</template>
            </el-table-column>
            <el-table-column label="操作" width="150">
              <template #default="{ row }">
                <el-button text type="primary" @click="router.push(`/post/${row.id}`)">
                  查看
                </el-button>
                <el-button
                  text
                  :type="row.status === 1 ? 'warning' : 'success'"
                  @click="toggleStatus(row)"
                >
                  {{ row.status === 1 ? '下架' : '上架' }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination-container">
            <el-pagination
              v-model:current-page="page"
              :page-size="pageSize"
              :total="total"
              layout="total, prev, pager, next"
              @current-change="loadPosts"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { getPostList } from '@/api/post'

const router = useRouter()
const userStore = useUserStore()

const posts = ref([])
const loading = ref(false)
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchKeyword = ref('')
const filterStatus = ref(null)

function handleLogout() {
  userStore.logout()
  router.push('/login')
}

function formatTime(time) {
  if (!time) return ''
  return new Date(time).toLocaleString('zh-CN')
}

async function loadPosts() {
  loading.value = true
  try {
    const res = await getPostList({
      page: page.value,
      size: pageSize.value,
      keyword: searchKeyword.value
    })
    posts.value = res.data.list
    total.value = res.data.total
  } catch (error) {
    console.error('获取帖子失败:', error)
  } finally {
    loading.value = false
  }
}

async function toggleStatus(row) {
  // TODO: 实现状态切换API调用
  ElMessage.success('操作成功')
  loadPosts()
}

onMounted(() => {
  loadPosts()
})
</script>

<style lang="scss" scoped>
.admin-page {
  display: flex;
  min-height: 100vh;
}

.admin-sidebar {
  width: 200px;
  background: #304156;

  .admin-logo {
    height: 60px;
    line-height: 60px;
    text-align: center;
    color: #fff;
    font-size: 18px;
    font-weight: 600;
  }

  .el-menu {
    border-right: none;
    background: transparent;

    .el-menu-item {
      color: #bfcbd9;

      &:hover,
      &.is-active {
        background: #263445;
        color: #409eff;
      }
    }

    .back-item {
      margin-top: 20px;
    }
  }
}

.admin-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.admin-header {
  height: 60px;
  background: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);

  h2 {
    font-size: 18px;
  }

  .admin-user {
    display: flex;
    align-items: center;
    gap: 10px;
    color: #606266;
  }
}

.admin-main {
  flex: 1;
  padding: 20px;
  background: #f5f7fa;
}

.page-container {
  background: #fff;
  padding: 20px;
  border-radius: 4px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;

  .filter-box {
    width: 150px;
  }

  .search-box {
    width: 300px;
  }
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
