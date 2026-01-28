<template>
  <div class="generate-page">
    <div class="container">
      <div class="page-header">
        <h1 class="page-title">智能行程规划</h1>
        <p class="page-subtitle">输入您的旅行偏好，AI 为您量身定制大理行程</p>
      </div>

      <div class="generate-form card">
        <el-form :model="form" label-position="top" size="large">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="旅行天数">
                <el-select v-model="form.days" placeholder="选择天数" style="width: 100%">
                  <el-option v-for="n in 7" :key="n" :label="`${n} 天`" :value="n" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="主题偏好">
                <el-select v-model="form.theme" placeholder="选择主题（可选）" clearable style="width: 100%">
                  <el-option label="古镇" value="古镇" />
                  <el-option label="自然风光" value="自然风光" />
                  <el-option label="民族文化" value="民族文化" />
                  <el-option label="摄影" value="摄影" />
                  <el-option label="美食" value="美食" />
                  <el-option label="徒步" value="徒步" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="旅行节奏">
                <el-radio-group v-model="form.pace">
                  <el-radio-button value="relaxed">轻松悠闲</el-radio-button>
                  <el-radio-button value="moderate">适中节奏</el-radio-button>
                  <el-radio-button value="intensive">紧凑充实</el-radio-button>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="出行人群">
                <el-radio-group v-model="form.travelers">
                  <el-radio-button value="solo">独自旅行</el-radio-button>
                  <el-radio-button value="couple">情侣出游</el-radio-button>
                  <el-radio-button value="family">家庭亲子</el-radio-button>
                  <el-radio-button value="friends">朋友结伴</el-radio-button>
                </el-radio-group>
              </el-form-item>
            </el-col>
          </el-row>

          <div class="form-actions">
            <el-button type="primary" size="large" @click="handleGenerate" :loading="loading">
              <el-icon v-if="!loading"><MagicStick /></el-icon>
              {{ loading ? '正在生成行程...' : '一键生成行程' }}
            </el-button>
          </div>
        </el-form>
      </div>

      <div class="features">
        <div class="feature-item">
          <el-icon class="feature-icon"><Location /></el-icon>
          <h3>智能路线</h3>
          <p>根据景点距离自动规划最优路线</p>
        </div>
        <div class="feature-item">
          <el-icon class="feature-icon"><Clock /></el-icon>
          <h3>时间规划</h3>
          <p>合理安排每个景点的游玩时间</p>
        </div>
        <div class="feature-item">
          <el-icon class="feature-icon"><Star /></el-icon>
          <h3>个性推荐</h3>
          <p>基于偏好推荐最适合的景点</p>
        </div>
      </div>
    </div>

    <LoadingOverlay :visible="loading" text="AI 正在为您规划行程，请稍候..." />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { generateItinerary } from '@/api/itinerary'
import LoadingOverlay from '@/components/LoadingOverlay.vue'

const router = useRouter()

const form = ref({
  city: '大理',
  days: 3,
  theme: '',
  pace: 'moderate',
  travelers: 'couple'
})

const loading = ref(false)

async function handleGenerate() {
  loading.value = true

  try {
    const res = await generateItinerary(form.value)
    ElMessage.success('行程生成成功！')
    router.push(`/itinerary/${res.data.itineraryId}`)
  } catch (error) {
    console.error('生成失败:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.generate-page {
  padding: 40px 0;
}

.page-header {
  text-align: center;
  margin-bottom: 40px;

  .page-title {
    font-size: 32px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 10px;
  }

  .page-subtitle {
    font-size: 16px;
    color: #909399;
  }
}

.generate-form {
  max-width: 800px;
  margin: 0 auto 60px;
  padding: 40px;
}

.form-actions {
  text-align: center;
  margin-top: 30px;

  .el-button {
    padding: 12px 40px;
    font-size: 16px;
  }
}

.features {
  display: flex;
  justify-content: center;
  gap: 60px;
  margin-top: 60px;
}

.feature-item {
  text-align: center;
  max-width: 200px;

  .feature-icon {
    font-size: 48px;
    color: #409eff;
    margin-bottom: 16px;
  }

  h3 {
    font-size: 18px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 8px;
  }

  p {
    font-size: 14px;
    color: #909399;
    line-height: 1.6;
  }
}
</style>
