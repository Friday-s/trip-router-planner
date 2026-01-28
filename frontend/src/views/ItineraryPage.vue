<template>
  <div class="itinerary-page">
    <div class="itinerary-container">
      <!-- 左侧行程列表 -->
      <div class="itinerary-list">
        <div class="itinerary-header">
          <h1 class="itinerary-title">{{ itinerary?.title }}</h1>
          <p class="itinerary-summary">{{ itinerary?.summary }}</p>

          <div class="itinerary-meta">
            <el-tag>{{ itinerary?.city }}</el-tag>
            <el-tag type="success">{{ itinerary?.days }}天</el-tag>
            <el-tag type="info" v-if="itinerary?.theme">{{ itinerary?.theme }}</el-tag>
          </div>
        </div>

        <div class="day-list">
          <div
            v-for="day in itinerary?.dayList"
            :key="day.dayIndex"
            class="day-item"
          >
            <div class="day-header" @click="toggleDay(day.dayIndex)">
              <div class="day-title">
                <span class="day-badge">Day {{ day.dayIndex }}</span>
                <span class="day-theme">{{ day.theme }}</span>
              </div>
              <el-icon :class="{ rotated: expandedDays.includes(day.dayIndex) }">
                <ArrowDown />
              </el-icon>
            </div>

            <el-collapse-transition>
              <div v-show="expandedDays.includes(day.dayIndex)" class="day-content">
                <p class="day-notes" v-if="day.notes">{{ day.notes }}</p>

                <div class="poi-list">
                  <div
                    v-for="(item, index) in day.items"
                    :key="index"
                    class="poi-item"
                  >
                    <div class="poi-time">{{ item.startTime }}</div>
                    <div class="poi-content">
                      <div class="poi-name">{{ item.poi.name }}</div>
                      <div class="poi-brief">{{ item.poi.brief }}</div>
                      <div class="poi-meta">
                        <span><el-icon><Clock /></el-icon> {{ item.poi.visitDuration }}分钟</span>
                        <span v-if="item.poi.ticketPrice"><el-icon><Ticket /></el-icon> ¥{{ item.poi.ticketPrice }}</span>
                      </div>
                      <div class="poi-tips" v-if="item.tips">
                        <el-icon><InfoFilled /></el-icon>
                        {{ item.tips }}
                      </div>
                    </div>

                    <!-- 路线信息 -->
                    <div class="route-info" v-if="item.routeToNext">
                      <div class="route-line"></div>
                      <div class="route-detail">
                        <el-icon><Van /></el-icon>
                        {{ formatDuration(item.routeToNext) }}
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </el-collapse-transition>
          </div>
        </div>

        <!-- 整体贴士 -->
        <div class="tips-section" v-if="itinerary?.tips?.length">
          <h3>温馨提示</h3>
          <ul>
            <li v-for="(tip, index) in itinerary.tips" :key="index">{{ tip }}</li>
          </ul>
        </div>

        <!-- 操作按钮 -->
        <div class="action-buttons">
          <template v-if="userStore.isLoggedIn">
            <el-button type="primary" @click="handleSave">
              <el-icon><FolderAdd /></el-icon>
              保存行程
            </el-button>
            <el-button @click="handlePublish">
              <el-icon><Share /></el-icon>
              分享到社区
            </el-button>
          </template>
          <template v-else>
            <el-button type="primary" @click="router.push('/login')">
              登录后可保存行程
            </el-button>
          </template>
        </div>
      </div>

      <!-- 右侧地图 -->
      <div class="map-container">
        <div id="amap" class="amap-container"></div>
      </div>
    </div>

    <!-- 发布弹窗 -->
    <el-dialog v-model="publishDialogVisible" title="分享到社区" width="500px">
      <el-form :model="publishForm" label-position="top">
        <el-form-item label="标题">
          <el-input v-model="publishForm.title" placeholder="给你的行程起个标题" />
        </el-form-item>
        <el-form-item label="简介">
          <el-input
            v-model="publishForm.summary"
            type="textarea"
            :rows="3"
            placeholder="分享你的旅行心得..."
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="publishDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmPublish">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getItineraryDetail, saveItinerary } from '@/api/itinerary'
import { createPost } from '@/api/post'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const itinerary = ref(null)
const expandedDays = ref([1])
const map = ref(null)

const publishDialogVisible = ref(false)
const publishForm = ref({
  title: '',
  summary: ''
})

function toggleDay(dayIndex) {
  const index = expandedDays.value.indexOf(dayIndex)
  if (index > -1) {
    expandedDays.value.splice(index, 1)
  } else {
    expandedDays.value.push(dayIndex)
  }
}

function formatDuration(route) {
  const mode = route.recommendMode
  let duration = 0

  if (mode === 'walk') {
    duration = route.walkDurationSec
  } else if (mode === 'drive') {
    duration = route.driveDurationSec
  } else {
    duration = route.transitDurationSec
  }

  const minutes = Math.ceil(duration / 60)
  const modeText = mode === 'walk' ? '步行' : mode === 'drive' ? '驾车' : '公交'

  return `${modeText} ${minutes}分钟`
}

async function handleSave() {
  try {
    await saveItinerary(route.params.id, {})
    ElMessage.success('保存成功！')
  } catch (error) {
    console.error('保存失败:', error)
  }
}

function handlePublish() {
  publishForm.value.title = itinerary.value?.title || ''
  publishDialogVisible.value = true
}

async function confirmPublish() {
  if (!publishForm.value.title) {
    ElMessage.warning('请输入标题')
    return
  }

  try {
    await createPost({
      itineraryId: Number(route.params.id),
      title: publishForm.value.title,
      summary: publishForm.value.summary
    })
    ElMessage.success('发布成功！')
    publishDialogVisible.value = false
  } catch (error) {
    console.error('发布失败:', error)
  }
}

function initMap() {
  if (!window.AMap) {
    console.error('高德地图API未加载')
    return
  }

  if (!itinerary.value?.mapData) {
    console.error('地图数据不存在')
    return
  }

  const mapData = itinerary.value.mapData

  // 转换中心点坐标为数字
  const center = mapData.center ? [
    parseFloat(mapData.center[0]),
    parseFloat(mapData.center[1])
  ] : [100.1653, 25.6969]

  map.value = new window.AMap.Map('amap', {
    zoom: mapData.zoom || 12,
    center: center,
    viewMode: '2D'
  })

  // 添加标记点
  const markers = mapData.markers || []
  const colors = ['#409eff', '#67c23a', '#e6a23c', '#f56c6c', '#909399', '#00c4b6', '#ff7c00']

  markers.forEach(marker => {
    const color = colors[(marker.dayIndex - 1) % colors.length]
    // 转换坐标为数字
    const position = [
      parseFloat(marker.position[0]),
      parseFloat(marker.position[1])
    ]

    new window.AMap.Marker({
      position: position,
      map: map.value,
      label: {
        content: `<div style="background:${color};color:#fff;padding:2px 6px;border-radius:10px;font-size:12px;">D${marker.dayIndex}-${marker.orderIndex}</div>`,
        direction: 'top'
      }
    })
  })

  // 添加路线
  const polylines = mapData.polylines || []
  polylines.forEach(polyline => {
    const color = colors[(polyline.dayIndex - 1) % colors.length]
    // 转换路径坐标为数字
    const path = polyline.path.map(point => [
      parseFloat(point[0]),
      parseFloat(point[1])
    ])

    new window.AMap.Polyline({
      path: path,
      strokeColor: color,
      strokeWeight: 3,
      strokeOpacity: 0.8,
      map: map.value
    })
  })

  // 自适应视野
  if (markers.length > 0) {
    map.value.setFitView()
  }
}

onMounted(async () => {
  try {
    const res = await getItineraryDetail(route.params.id)
    itinerary.value = res.data

    // 等待 DOM 更新后初始化地图
    await nextTick()
    // 延迟确保地图容器已渲染
    setTimeout(initMap, 200)
  } catch (error) {
    console.error('获取行程失败:', error)
    router.push('/')
  }
})

onUnmounted(() => {
  if (map.value) {
    map.value.destroy()
  }
})
</script>

<style lang="scss" scoped>
.itinerary-page {
  height: calc(100vh - 60px);
  background: #f5f7fa;
}

.itinerary-container {
  display: flex;
  height: 100%;
}

.itinerary-list {
  width: 500px;
  background: #fff;
  overflow-y: auto;
  padding: 20px;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
}

.itinerary-header {
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;

  .itinerary-title {
    font-size: 22px;
    font-weight: 600;
    margin-bottom: 10px;
  }

  .itinerary-summary {
    font-size: 14px;
    color: #666;
    margin-bottom: 15px;
    line-height: 1.6;
  }

  .itinerary-meta {
    display: flex;
    gap: 8px;
  }
}

.day-item {
  margin-bottom: 10px;
  border: 1px solid #eee;
  border-radius: 8px;
  overflow: hidden;
}

.day-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #f5f7fa;
  cursor: pointer;

  .day-title {
    display: flex;
    align-items: center;
    gap: 10px;
  }

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

  .el-icon {
    transition: transform 0.3s;

    &.rotated {
      transform: rotate(180deg);
    }
  }
}

.day-content {
  padding: 16px;
}

.day-notes {
  font-size: 13px;
  color: #909399;
  margin-bottom: 16px;
  padding: 10px;
  background: #fef0f0;
  border-radius: 4px;
}

.poi-item {
  position: relative;
  padding-left: 60px;
  margin-bottom: 20px;

  &:last-child {
    margin-bottom: 0;

    .route-info {
      display: none;
    }
  }
}

.poi-time {
  position: absolute;
  left: 0;
  top: 0;
  width: 50px;
  font-size: 13px;
  color: #409eff;
  font-weight: 500;
}

.poi-content {
  .poi-name {
    font-size: 15px;
    font-weight: 600;
    margin-bottom: 6px;
  }

  .poi-brief {
    font-size: 13px;
    color: #666;
    line-height: 1.5;
    margin-bottom: 8px;
  }

  .poi-meta {
    display: flex;
    gap: 16px;
    font-size: 12px;
    color: #909399;

    span {
      display: flex;
      align-items: center;
      gap: 4px;
    }
  }

  .poi-tips {
    margin-top: 8px;
    padding: 8px 12px;
    background: #f0f9eb;
    border-radius: 4px;
    font-size: 12px;
    color: #67c23a;
    display: flex;
    align-items: flex-start;
    gap: 6px;
  }
}

.route-info {
  margin: 12px 0;
  padding-left: 0;

  .route-line {
    position: absolute;
    left: 24px;
    width: 2px;
    height: 20px;
    background: #ddd;
  }

  .route-detail {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 12px;
    color: #909399;
    padding: 4px 0;
  }
}

.tips-section {
  margin-top: 20px;
  padding: 16px;
  background: #fdf6ec;
  border-radius: 8px;

  h3 {
    font-size: 14px;
    font-weight: 600;
    margin-bottom: 10px;
    color: #e6a23c;
  }

  ul {
    list-style: disc;
    padding-left: 20px;

    li {
      font-size: 13px;
      color: #666;
      line-height: 1.8;
    }
  }
}

.action-buttons {
  margin-top: 20px;
  display: flex;
  gap: 10px;
}

.map-container {
  flex: 1;
  height: 100%;
  min-height: 400px;
  background: #e4e4e4;

  .amap-container {
    width: 100%;
    height: 100%;
    min-height: 400px;
  }
}
</style>
