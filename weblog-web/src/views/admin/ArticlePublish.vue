<template>
  <el-card shadow="never">
    <template #header>
      <div class="flex items-center justify-between">
        <span>{{ isEdit ? '编辑文章' : '发布文章' }}</span>
        <el-button @click="$router.back()">返回</el-button>
      </div>
    </template>

    <el-form :model="form" label-width="80px" v-loading="loading">
      <el-form-item label="标题">
        <el-input v-model="form.title" placeholder="请输入文章标题" />
      </el-form-item>
      <el-form-item label="分类">
        <el-select v-model="form.categoryId" placeholder="选择分类" filterable>
          <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="标签">
        <el-select v-model="form.tagIds" multiple placeholder="选择标签" filterable>
          <el-option v-for="t in tags" :key="t.id" :label="t.name" :value="t.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="封面图">
        <el-input v-model="form.cover" placeholder="封面图 URL（可选）" />
      </el-form-item>
      <el-form-item label="置顶">
        <el-switch v-model="form.isTop" :active-value="1" :inactive-value="0" active-text="是" />
      </el-form-item>
      <el-form-item label="正文">
        <MdEditor v-model="form.content" :style="{ height: '400px' }" />
      </el-form-item>
      <el-form-item label="状态">
        <el-radio-group v-model="form.status">
          <el-radio :label="1">发布</el-radio>
          <el-radio :label="0">草稿</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="摘要">
        <el-input v-model="form.summary" type="textarea" :rows="2" placeholder="不填则自动生成" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSubmit">{{ isEdit ? '更新' : '发布' }}</el-button>
        <el-button @click="$router.back()">取消</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { MdEditor } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'
import { publishArticle, getArticleDetail } from '@/api/article'
import { pageCategories } from '@/api/category'
import { pageTags } from '@/api/tag'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const isEdit = computed(() => !!route.query.id)

const form = reactive({
  id: null,
  title: '',
  categoryId: null,
  tagIds: [],
  cover: '',
  content: '',
  summary: '',
  status: 1,
  isTop: 0
})

const categories = ref([])
const tags = ref([])

async function loadOptions() {
  try {
    const [catRes, tagRes] = await Promise.all([
      pageCategories({ current: 1, size: 100 }),
      pageTags({ current: 1, size: 100 })
    ])
    categories.value = catRes.data.records || []
    tags.value = tagRes.data.records || []
  } catch (e) {}
}

async function loadArticle() {
  if (!route.query.id) return
  loading.value = true
  try {
    const res = await getArticleDetail(route.query.id)
    const d = res.data
    form.id = d.id
    form.title = d.title
    form.categoryId = d.categoryId
    form.tagIds = d.tagIds || []
    form.cover = d.cover || ''
    form.content = d.content || ''
    form.summary = d.summary || ''
    form.status = d.status == null ? 1 : d.status
    form.isTop = d.isTop || 0
  } catch (e) {} finally {
    loading.value = false
  }
}

async function handleSubmit() {
  if (!form.title.trim()) { ElMessage.warning('请输入标题'); return }
  if (!form.content.trim()) { ElMessage.warning('请输入正文'); return }
  loading.value = true
  try {
    await publishArticle(form)
    ElMessage.success(isEdit.value ? '更新成功' : '发布成功')
    router.push('/admin/article')
  } catch (e) {} finally {
    loading.value = false
  }
}

onMounted(async () => {
  await loadOptions()
  await loadArticle()
})
</script>