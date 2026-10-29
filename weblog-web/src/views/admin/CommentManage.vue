<template>
  <el-card shadow="never">
    <template #header>
      <span>评论管理</span>
    </template>

    <el-form :inline="true" class="mb-4">
      <el-form-item label="状态">
        <el-select v-model="query.status" placeholder="全部" clearable style="width: 120px">
          <el-option label="已通过" :value="1" />
          <el-option label="待审核" :value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="tableData" v-loading="loading" border stripe>
      <el-table-column type="index" label="#" width="50" />
      <el-table-column label="评论人" width="160">
        <template #default="{ row }">
          <div class="flex items-center gap-2">
            <el-avatar :size="28" :src="row.avatar" />
            <span>{{ row.nickname }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="content" label="内容" min-width="260" show-overflow-tooltip />
      <el-table-column prop="articleId" label="文章ID" width="180" />
      <el-table-column label="状态" width="90" align="center">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'warning'" size="small">
            {{ row.status === 1 ? '已通过' : '待审核' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="评论时间" width="170" />
      <el-table-column label="操作" width="170" fixed="right">
        <template #default="{ row }">
          <el-button v-if="row.status !== 1" text type="success" @click="handleAudit(row, 1)">通过</el-button>
          <el-button v-if="row.status === 1" text type="warning" @click="handleAudit(row, 0)">驳回</el-button>
          <el-popconfirm title="确定删除？" @confirm="handleDelete(row.id)">
            <template #reference>
              <el-button text type="danger">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="query.current"
      v-model:page-size="query.size"
      :total="total"
      :page-sizes="[10, 20, 50]"
      layout="total, sizes, prev, pager, next"
      class="mt-4 justify-end"
      @size-change="loadData"
      @current-change="loadData"
    />
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { pageComments, deleteComment, auditComment } from '@/api/comment'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const query = reactive({ current: 1, size: 10, status: null })

async function loadData() {
  loading.value = true
  try {
    const res = await pageComments(query)
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (e) {} finally {
    loading.value = false
  }
}

function resetQuery() {
  query.status = null
  query.current = 1
  loadData()
}

async function handleDelete(id) {
  try {
    await deleteComment(id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {}
}

async function handleAudit(row, status) {
  try {
    await auditComment(row.id, status)
    ElMessage.success(status === 1 ? '已通过' : '已驳回')
    loadData()
  } catch (e) {}
}

onMounted(loadData)
</script>
