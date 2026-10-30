<template>
  <el-card shadow="never">
    <template #header>
      <div class="flex items-center justify-between">
        <span>知识库管理</span>
        <div class="flex gap-2">
          <el-button type="primary" @click="openDirDialog()">新增目录</el-button>
          <el-button type="success" @click="openArticleDialog()">新增文章</el-button>
        </div>
      </div>
    </template>

    <div class="flex gap-4">
      <!-- 左侧目录树 -->
      <div class="w-64 shrink-0">
        <div class="text-sm text-gray-500 mb-2">知识库目录</div>
        <el-tree
          :data="directoryTree"
          :props="{ label: 'title', children: 'children' }"
          node-key="id"
          highlight-current
          :expand-on-click-node="false"
          @node-click="handleDirClick"
        >
          <template #default="{ data }">
            <div class="flex items-center justify-between w-full pr-2">
              <span>{{ data.title }}</span>
              <span class="text-xs text-gray-400">{{ data.articleCount }}</span>
            </div>
          </template>
        </el-tree>
        <div class="mt-3 flex gap-1">
          <el-button size="small" text type="primary" @click="openDirDialog()">添加</el-button>
          <el-button size="small" text type="warning" @click="editCurrentDir()">重命名</el-button>
          <el-button size="small" text type="danger" @click="deleteCurrentDir()">删除</el-button>
        </div>
      </div>

      <!-- 右侧文章列表 -->
      <div class="flex-1">
        <el-form :inline="true" class="mb-3">
          <el-form-item label="标题">
            <el-input v-model="query.title" placeholder="标题关键词" clearable style="width: 160px" @keyup.enter="loadArticles" />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="query.status" placeholder="全部" clearable style="width: 110px">
              <el-option label="已发布" :value="1" />
              <el-option label="未发布" :value="0" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="loadArticles">查询</el-button>
          </el-form-item>
        </el-form>

        <el-table v-loading="loading" :data="articleList" border stripe>
          <el-table-column prop="title" label="标题" min-width="220" show-overflow-tooltip />
          <el-table-column prop="summary" label="摘要" min-width="200" show-overflow-tooltip />
          <el-table-column label="置顶" width="70" align="center">
            <template #default="{ row }">
              <el-tag v-if="row.isTop === 1" type="danger" size="small">置顶</el-tag>
              <span v-else class="text-gray-300">-</span>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="90" align="center">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
                {{ row.status === 1 ? '已发布' : '未发布' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="170" />
          <el-table-column label="操作" width="220" fixed="right">
            <template #default="{ row }">
              <el-button text type="primary" @click="openArticleDialog(row)">编辑</el-button>
              <el-button text :type="row.isTop === 1 ? 'warning' : 'info'" @click="toggleTop(row)">
                {{ row.isTop === 1 ? '取消置顶' : '置顶' }}
              </el-button>
              <el-button text :type="row.status === 1 ? 'warning' : 'success'" @click="toggleStatus(row)">
                {{ row.status === 1 ? '下架' : '发布' }}
              </el-button>
              <el-popconfirm title="确定删除？" @confirm="handleDeleteArticle(row.id)">
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
          @size-change="loadArticles"
          @current-change="loadArticles"
        />
      </div>
    </div>

    <!-- 目录对话框 -->
    <el-dialog v-model="dirDialogVisible" :title="dirForm.id ? '编辑目录' : '新增目录'" width="420px">
      <el-form :model="dirForm" label-width="80px">
        <el-form-item label="目录标题">
          <el-input v-model="dirForm.title" placeholder="请输入目录标题" />
        </el-form-item>
        <el-form-item label="上级目录">
          <el-tree-select
            v-model="dirForm.parentId"
            :data="parentOptions"
            :props="{ label: 'title', children: 'children' }"
            node-key="id"
            check-strictly
            clearable
            placeholder="不选则为一级目录"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dirDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSaveDir">确定</el-button>
      </template>
    </el-dialog>

    <!-- 文章对话框 -->
    <el-dialog v-model="articleDialogVisible" :title="articleForm.id ? '编辑文章' : '新增文章'" width="720px" top="5vh">
      <el-form :model="articleForm" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="articleForm.title" placeholder="请输入文章标题" />
        </el-form-item>
        <el-form-item label="所属目录">
          <el-tree-select
            v-model="articleForm.directoryId"
            :data="directoryTree"
            :props="{ label: 'title', children: 'children' }"
            node-key="id"
            check-strictly
            placeholder="请选择目录"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="摘要">
          <el-input v-model="articleForm.summary" type="textarea" :rows="2" placeholder="可选，留空自动生成" />
        </el-form-item>
        <el-form-item label="正文">
          <el-input v-model="articleForm.content" type="textarea" :rows="12" placeholder="支持 Markdown 语法" />
        </el-form-item>
        <el-form-item label="置顶">
          <el-switch v-model="articleForm.isTop" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="发布">
          <el-switch v-model="articleForm.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="articleDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSaveArticle">确定</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import {
  listDirectory, saveDirectory, deleteDirectory,
  pageArticle, saveArticle, deleteArticle,
  updateArticleTop, updateArticleStatus
} from '@/api/knowledge'
import { ElMessage } from 'element-plus'

const directoryTree = ref([])
const articleList = ref([])
const total = ref(0)
const loading = ref(false)
const saving = ref(false)
const currentDirId = ref(null)

const query = reactive({ current: 1, size: 10, title: '', status: null, directoryId: null })

const dirDialogVisible = ref(false)
const dirForm = reactive({ id: null, title: '', parentId: null })

const articleDialogVisible = ref(false)
const articleForm = reactive({ id: null, title: '', directoryId: null, summary: '', content: '', isTop: 0, status: 0 })

// 上级目录选择：包含"一级目录"虚拟节点
const parentOptions = computed(() => {
  const root = { id: 0, title: '（一级目录）', children: directoryTree.value }
  return [root]
})

async function loadDirectories() {
  const res = await listDirectory()
  directoryTree.value = res.data || []
}

async function loadArticles() {
  loading.value = true
  try {
    query.directoryId = currentDirId.value
    const res = await pageArticle(query)
    articleList.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (e) {} finally {
    loading.value = false
  }
}

function handleDirClick(data) {
  currentDirId.value = Number(data.id)
  query.current = 1
  loadArticles()
}

// ===== 目录操作 =====
function openDirDialog() {
  dirForm.id = null
  dirForm.title = ''
  dirForm.parentId = currentDirId.value || null
  dirDialogVisible.value = true
}

function editCurrentDir() {
  const node = findDir(currentDirId.value)
  if (!node) {
    ElMessage.warning('请先选择要编辑的目录')
    return
  }
  dirForm.id = node.id
  dirForm.title = node.title
  dirForm.parentId = node.parentId === '0' ? null : Number(node.parentId)
  dirDialogVisible.value = true
}

async function deleteCurrentDir() {
  if (!currentDirId.value) {
    ElMessage.warning('请先选择要删除的目录')
    return
  }
  try {
    await deleteDirectory(currentDirId.value)
    ElMessage.success('删除成功')
    currentDirId.value = null
    query.directoryId = null
    await loadDirectories()
    await loadArticles()
  } catch (e) {}
}

async function handleSaveDir() {
  if (!dirForm.title.trim()) {
    ElMessage.warning('请输入目录标题')
    return
  }
  saving.value = true
  try {
    await saveDirectory({
      id: dirForm.id || undefined,
      title: dirForm.title,
      parentId: dirForm.parentId || 0
    })
    ElMessage.success('保存成功')
    dirDialogVisible.value = false
    await loadDirectories()
  } catch (e) {} finally {
    saving.value = false
  }
}

// ===== 文章操作 =====
function openArticleDialog(row) {
  if (row) {
    articleForm.id = row.id
    articleForm.title = row.title
    articleForm.directoryId = row.directoryId ? Number(row.directoryId) : null
    articleForm.summary = row.summary || ''
    articleForm.isTop = row.isTop
    articleForm.status = row.status
    articleForm.content = row.content || ''
    articleDialogVisible.value = true
  } else {
    articleForm.id = null
    articleForm.title = ''
    articleForm.directoryId = currentDirId.value || null
    articleForm.summary = ''
    articleForm.content = ''
    articleForm.isTop = 0
    articleForm.status = 0
    articleDialogVisible.value = true
  }
}

async function handleSaveArticle() {
  if (!articleForm.title.trim()) {
    ElMessage.warning('请输入文章标题')
    return
  }
  if (!articleForm.directoryId) {
    ElMessage.warning('请选择所属目录')
    return
  }
  saving.value = true
  try {
    await saveArticle({
      id: articleForm.id || undefined,
      title: articleForm.title,
      directoryId: Number(articleForm.directoryId),
      summary: articleForm.summary,
      content: articleForm.content,
      isTop: articleForm.isTop,
      status: articleForm.status
    })
    ElMessage.success('保存成功')
    articleDialogVisible.value = false
    await loadArticles()
    await loadDirectories()
  } catch (e) {} finally {
    saving.value = false
  }
}

async function toggleTop(row) {
  try {
    await updateArticleTop(row.id, row.isTop === 1 ? 0 : 1)
    ElMessage.success('操作成功')
    loadArticles()
  } catch (e) {}
}

async function toggleStatus(row) {
  try {
    await updateArticleStatus(row.id, row.status === 1 ? 0 : 1)
    ElMessage.success('操作成功')
    loadArticles()
  } catch (e) {}
}

async function handleDeleteArticle(id) {
  try {
    await deleteArticle(id)
    ElMessage.success('删除成功')
    loadArticles()
    loadDirectories()
  } catch (e) {}
}

// 递归查找目录
function findDir(id, list = directoryTree.value) {
  for (const node of list) {
    if (node.id === String(id)) return node
    if (node.children && node.children.length) {
      const found = findDir(id, node.children)
      if (found) return found
    }
  }
  return null
}

onMounted(() => {
  loadDirectories()
  loadArticles()
})
</script>
