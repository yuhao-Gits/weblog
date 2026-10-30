<template>
  <el-card shadow="never">
    <template #header>
      <div class="flex items-center justify-between">
        <span>文章管理</span>
        <el-button type="primary" @click="goPublish">+ 发布文章</el-button>
      </div>
    </template>

    <!-- 搜索栏 -->
    <el-form :inline="true" :model="query" class="mb-4">
      <el-form-item label="标题">
        <el-input v-model="query.title" placeholder="搜索标题" clearable @clear="loadData" @keyup.enter="loadData" />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="query.status" placeholder="全部" clearable style="width: 100px">
          <el-option label="已发布" :value="1" />
          <el-option label="草稿" :value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 表格 -->
    <el-table v-loading="loading" :data="tableData" border stripe>
      <el-table-column type="index" label="#" width="50" />
      <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
      <el-table-column prop="categoryName" label="分类" width="120" />
      <el-table-column prop="tagNames" label="标签" width="180" show-overflow-tooltip />
      <el-table-column prop="readCount" label="阅读量" width="90" align="center" />
      <el-table-column label="置顶" width="70" align="center">
        <template #default="{ row }">
          <el-switch
            :model-value="row.isTop === 1"
            @change="handleToggleTop(row, $event)"
          />
        </template>
      </el-table-column>
      <el-table-column label="状态" width="80" align="center">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
            {{ row.status === 1 ? '已发布' : '草稿' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="170" />
      <el-table-column label="操作" width="150" fixed="right">        <template #default="{ row }">
          <el-button text type="primary" @click="goEdit(row.id)">编辑</el-button>
          <el-popconfirm title="确定删除？" @confirm="handleDelete(row.id)">
            <template #reference>
              <el-button text type="danger">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-model:current-page="query.current"
      v-model:page-size="query.size"
      :total="total"
      :page-sizes="[5, 10, 20]"
      layout="total, sizes, prev, pager, next"
      class="mt-4 justify-end"
      @size-change="loadData"
      @current-change="loadData"
    />
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { pageArticles, deleteArticle, updateArticleIsTop } from '@/api/article'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const tableData = ref([])
const total = ref(0)

const query = reactive({ current: 1, size: 10, title: '', status: null })

async function loadData() {
  loading.value = true
  try {
    const res = await pageArticles(query)
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (e) {} finally {
    loading.value = false
  }
}

function resetQuery() {
  query.title = ''
  query.status = null
  query.current = 1
  loadData()
}

function goPublish() { router.push('/admin/article/publish') }
function goEdit(id) { router.push(`/admin/article/publish?id=${id}`) }

async function handleDelete(id) {
  try {
    await deleteArticle(id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {}
}

async function handleToggleTop(row, checked) {
  try {
    await updateArticleIsTop(row.id, checked ? 1 : 0)
    ElMessage.success(checked ? '已置顶' : '已取消置顶')
    loadData()
  } catch (e) {}
}

onMounted(loadData)
</script>