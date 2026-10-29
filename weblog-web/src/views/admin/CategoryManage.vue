<template>
  <el-card shadow="never">
    <template #header>
      <div class="flex items-center justify-between">
        <span>分类管理</span>
        <el-button type="primary" @click="showDialog = true">+ 新增分类</el-button>
      </div>
    </template>

    <el-table :data="tableData" v-loading="loading" border stripe>
      <el-table-column prop="name" label="名称" width="200" />
      <el-table-column prop="articlesTotal" label="文章数" width="100" align="center" />
      <el-table-column prop="createTime" label="创建时间" />
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
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
      :page-sizes="[5, 10, 20]"
      layout="total, sizes, prev, pager, next"
      class="mt-4 justify-end"
      @size-change="loadData"
      @current-change="loadData"
    />

    <!-- 新增对话框 -->
    <el-dialog v-model="showDialog" title="新增分类" width="400px">
      <el-form :model="addForm" label-width="60px" @keyup.enter="handleAdd">
        <el-form-item label="名称">
          <el-input v-model="addForm.name" placeholder="分类名称" maxlength="30" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" :loading="adding" @click="handleAdd">确定</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { pageCategories, addCategory, deleteCategory } from '@/api/category'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const query = reactive({ current: 1, size: 10 })
const showDialog = ref(false)
const adding = ref(false)
const addForm = reactive({ name: '' })

async function loadData() {
  loading.value = true
  try {
    const res = await pageCategories(query)
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (e) {} finally {
    loading.value = false
  }
}

async function handleAdd() {
  if (!addForm.name.trim()) { ElMessage.warning('请输入分类名称'); return }
  adding.value = true
  try {
    await addCategory(addForm)
    ElMessage.success('添加成功')
    showDialog.value = false
    addForm.name = ''
    loadData()
  } catch (e) {} finally {
    adding.value = false
  }
}

async function handleDelete(id) {
  try {
    await deleteCategory(id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {}
}

onMounted(loadData)
</script>