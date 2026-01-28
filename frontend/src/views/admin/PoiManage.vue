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
        <h2>{{ pageTitle }}</h2>
        <div class="admin-user">
          <span>{{ userStore.user?.username }}</span>
          <el-button text @click="handleLogout">退出</el-button>
        </div>
      </div>

      <div class="admin-main">
        <div class="page-container">
          <div class="page-header">
            <el-button type="primary" @click="showAddDialog">
              <el-icon><Plus /></el-icon>
              新增POI
            </el-button>

            <div class="search-box">
              <el-input
                v-model="searchKeyword"
                placeholder="搜索景点名称"
                clearable
                @clear="loadPois"
                @keyup.enter="loadPois"
              >
                <template #append>
                  <el-button @click="loadPois">
                    <el-icon><Search /></el-icon>
                  </el-button>
                </template>
              </el-input>
            </div>
          </div>

          <el-table :data="pois" v-loading="loading" stripe>
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="name" label="名称" min-width="150" />
            <el-table-column prop="area" label="区域" width="100" />
            <el-table-column label="标签" min-width="150">
              <template #default="{ row }">
                <el-tag v-for="tag in row.tags" :key="tag" size="small" style="margin-right: 4px">
                  {{ tag }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="visitDuration" label="游玩时长" width="100">
              <template #default="{ row }">{{ row.visitDuration }}分钟</template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'danger'">
                  {{ row.status === 1 ? '上架' : '下架' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150">
              <template #default="{ row }">
                <el-button text type="primary" @click="showEditDialog(row)">编辑</el-button>
                <el-button text type="danger" @click="handleDelete(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination-container">
            <el-pagination
              v-model:current-page="page"
              :page-size="pageSize"
              :total="total"
              layout="total, prev, pager, next"
              @current-change="loadPois"
            />
          </div>
        </div>
      </div>
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑POI' : '新增POI'" width="600px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="经度">
              <el-input-number v-model="form.lng" :precision="7" :controls="false" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="纬度">
              <el-input-number v-model="form.lat" :precision="7" :controls="false" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="地址">
          <el-input v-model="form.address" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="城市">
              <el-input v-model="form.city" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="区域">
              <el-input v-model="form.area" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="标签">
          <el-select v-model="form.tags" multiple placeholder="选择标签" style="width: 100%">
            <el-option value="古镇" />
            <el-option value="自然风光" />
            <el-option value="民族文化" />
            <el-option value="摄影" />
            <el-option value="美食" />
            <el-option value="徒步" />
            <el-option value="亲子" />
            <el-option value="小众" />
          </el-select>
        </el-form-item>
        <el-form-item label="简介">
          <el-input v-model="form.brief" type="textarea" :rows="3" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="游玩时长">
              <el-input-number v-model="form.visitDuration" :min="30" :step="30" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-radio-group v-model="form.status">
                <el-radio :value="1">上架</el-radio>
                <el-radio :value="0">下架</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { getPoiList } from '@/api/poi'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const pageTitle = computed(() => 'POI管理')

const pois = ref([])
const loading = ref(false)
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchKeyword = ref('')

const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref({
  name: '',
  lng: 100.0,
  lat: 25.0,
  address: '',
  city: '大理',
  area: '',
  tags: [],
  brief: '',
  visitDuration: 60,
  status: 1
})

function handleLogout() {
  userStore.logout()
  router.push('/login')
}

async function loadPois() {
  loading.value = true
  try {
    const res = await getPoiList({
      page: page.value,
      size: pageSize.value,
      keyword: searchKeyword.value
    })
    pois.value = res.data.list
    total.value = res.data.total
  } catch (error) {
    console.error('获取POI失败:', error)
  } finally {
    loading.value = false
  }
}

function showAddDialog() {
  isEdit.value = false
  form.value = {
    name: '',
    lng: 100.0,
    lat: 25.0,
    address: '',
    city: '大理',
    area: '',
    tags: [],
    brief: '',
    visitDuration: 60,
    status: 1
  }
  dialogVisible.value = true
}

function showEditDialog(row) {
  isEdit.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

async function handleSubmit() {
  // TODO: 实现新增/编辑API调用
  ElMessage.success(isEdit.value ? '编辑成功' : '新增成功')
  dialogVisible.value = false
  loadPois()
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm('确定要删除这个POI吗？', '提示', {
      type: 'warning'
    })
    // TODO: 实现删除API调用
    ElMessage.success('删除成功')
    loadPois()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

onMounted(() => {
  loadPois()
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
