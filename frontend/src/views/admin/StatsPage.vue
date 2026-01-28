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
        <h2>数据统计</h2>
        <div class="admin-user">
          <span>{{ userStore.user?.username }}</span>
          <el-button text @click="handleLogout">退出</el-button>
        </div>
      </div>

      <div class="admin-main">
        <div class="stats-grid">
          <div class="stat-card card">
            <div class="stat-icon" style="background: #409eff;">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.userCount }}</div>
              <div class="stat-label">注册用户</div>
            </div>
          </div>

          <div class="stat-card card">
            <div class="stat-icon" style="background: #67c23a;">
              <el-icon><Location /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.poiCount }}</div>
              <div class="stat-label">景点数量</div>
            </div>
          </div>

          <div class="stat-card card">
            <div class="stat-icon" style="background: #e6a23c;">
              <el-icon><Map /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.itineraryCount }}</div>
              <div class="stat-label">生成行程</div>
            </div>
          </div>

          <div class="stat-card card">
            <div class="stat-icon" style="background: #f56c6c;">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.postCount }}</div>
              <div class="stat-label">社区帖子</div>
            </div>
          </div>
        </div>

        <div class="today-stats card">
          <h3>今日数据</h3>
          <div class="today-grid">
            <div class="today-item">
              <div class="today-value">{{ stats.todayItineraryCount }}</div>
              <div class="today-label">今日生成行程</div>
            </div>
            <div class="today-item">
              <div class="today-value">{{ stats.todayPostCount }}</div>
              <div class="today-label">今日发布帖子</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const stats = ref({
  userCount: 0,
  poiCount: 0,
  itineraryCount: 0,
  postCount: 0,
  todayItineraryCount: 0,
  todayPostCount: 0
})

function handleLogout() {
  userStore.logout()
  router.push('/login')
}

onMounted(() => {
  // TODO: 调用统计API
  stats.value = {
    userCount: 128,
    poiCount: 50,
    itineraryCount: 456,
    postCount: 89,
    todayItineraryCount: 12,
    todayPostCount: 3
  }
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

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 20px;

  .stat-icon {
    width: 60px;
    height: 60px;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;

    .el-icon {
      font-size: 28px;
      color: #fff;
    }
  }

  .stat-info {
    .stat-value {
      font-size: 28px;
      font-weight: 600;
      color: #303133;
    }

    .stat-label {
      font-size: 14px;
      color: #909399;
    }
  }
}

.today-stats {
  h3 {
    font-size: 16px;
    margin-bottom: 20px;
  }
}

.today-grid {
  display: flex;
  gap: 60px;
}

.today-item {
  .today-value {
    font-size: 36px;
    font-weight: 600;
    color: #409eff;
  }

  .today-label {
    font-size: 14px;
    color: #909399;
    margin-top: 5px;
  }
}
</style>
