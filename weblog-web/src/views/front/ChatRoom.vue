<template>
  <div class="max-w-6xl mx-auto px-5 py-8">
    <div class="flex items-center justify-between mb-6">
      <h1 class="page-title">公共聊天室</h1>
      <span class="text-sm" style="color: var(--text-muted)">与访客实时交流</span>
    </div>

    <!-- 聊天室卡片 -->
    <div class="chat-card flex" style="height: 600px">
      <!-- 消息区 -->
      <div class="flex-1 flex flex-col min-w-0">
        <div class="chat-header">
          <div class="flex items-center gap-2">
            <el-icon><ChatDotRound /></el-icon>
            <span class="font-semibold" style="color: var(--text-primary)">公共聊天室</span>
          </div>
          <div class="flex items-center gap-3">
            <el-tag size="small" type="success" round>{{ onlineCount }} 人在线</el-tag>
            <el-button v-if="joined" size="small" text type="danger" @click="leave">退出聊天室</el-button>
          </div>
        </div>

        <!-- 消息列表 -->
        <div ref="messageBox" class="flex-1 overflow-y-auto px-4 py-4 space-y-3 chat-body" @scroll="handleScroll">
          <div v-if="loadingHistory" class="text-center text-sm py-4" style="color: var(--text-muted)">加载历史消息中...</div>
          <div v-else-if="!hasMore" class="text-center text-xs py-2" style="color: var(--text-faint)">没有更多消息了</div>

          <div v-for="msg in messages" :key="msg.id" class="flex flex-col">
            <!-- 系统通知 -->
            <div v-if="msg.type === 2" class="text-center text-xs my-1" style="color: var(--text-muted)">{{ msg.content }}</div>
            <!-- 用户消息 -->
            <div v-else class="flex gap-2" :class="{ 'flex-row-reverse': msg.nickname === myNickname }">
              <el-avatar :size="36" :src="msg.avatar || undefined">
                {{ (msg.nickname || '?').charAt(0) }}
              </el-avatar>
              <div class="max-w-[60%]" :class="{ 'items-end': msg.nickname === myNickname }">
                <div class="text-xs mb-1" :class="{ 'text-right': msg.nickname === myNickname }" style="color: var(--text-muted)">
                  {{ msg.nickname }} · {{ msg.createTime }}
                </div>
                <div
                  class="px-3 py-2 rounded-lg text-sm break-words"
                  :class="msg.nickname === myNickname ? 'msg-own' : 'msg-other'"
                >{{ msg.content }}</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 输入区 -->
        <div class="chat-input">
          <div v-if="!joined" class="flex gap-2">
            <el-input v-model="nickname" placeholder="输入昵称加入聊天室" maxlength="20" @keyup.enter="join" />
            <el-button type="primary" :loading="joining" @click="join">加入</el-button>
          </div>
          <div v-else class="flex gap-2">
            <el-input v-model="inputMsg" placeholder="输入消息，回车发送" maxlength="500" @keyup.enter="send" />
            <el-button type="primary" @click="send">发送</el-button>
          </div>
        </div>
      </div>

      <!-- 在线用户区 -->
      <div class="w-48 shrink-0 border-l online-panel hidden md:flex flex-col">
        <div class="px-3 py-3 border-b text-sm font-semibold online-title">在线用户</div>
        <div class="flex-1 overflow-y-auto p-2 space-y-2">
          <div v-for="user in onlineUsers" :key="user" class="flex items-center gap-2 text-sm" style="color: var(--text-secondary)">
            <span class="w-2 h-2 rounded-full bg-green-500"></span>
            <span class="truncate">{{ user }}</span>
          </div>
          <div v-if="onlineUsers.length === 0" class="text-center text-xs py-4" style="color: var(--text-muted)">暂无在线用户</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted, onBeforeUnmount } from 'vue'
import { ElMessage } from 'element-plus'
import { ChatDotRound } from '@element-plus/icons-vue'
import { getChatHistory, getChatOnline } from '@/api/chat'

const joined = ref(false)
const joining = ref(false)
const nickname = ref('')
const myNickname = ref('')
const inputMsg = ref('')
const messages = ref([])
const onlineUsers = ref([])
const onlineCount = ref(0)
const loadingHistory = ref(false)
const hasMore = ref(true)
const messageBox = ref()

let socket = null
let lastMessageId = null
let heartbeatTimer = null

function buildWsUrl() {
  const protocol = location.protocol === 'https:' ? 'wss' : 'ws'
  return `${protocol}://${location.host}/api/chat/ws`
}

async function loadHistory() {
  loadingHistory.value = true
  try {
    const res = await getChatHistory({ lastMessageId, size: 30 })
    const records = res.data?.records || []
    if (records.length > 0) {
      messages.value = [...records, ...messages.value]
      lastMessageId = records[0].id
    }
    if (records.length < 30) {
      hasMore.value = false
    }
  } catch (e) {
  } finally {
    loadingHistory.value = false
  }
}

function join() {
  if (!nickname.value.trim()) {
    ElMessage.warning('请输入昵称')
    return
  }
  myNickname.value = nickname.value.trim()
  joining.value = true
  try {
    socket = new WebSocket(buildWsUrl())
    socket.onopen = () => {
      joining.value = false
      joined.value = true
      socket.send(JSON.stringify({ type: 'join', nickname: myNickname.value, avatar: '' }))
      heartbeatTimer = setInterval(() => {
        if (socket && socket.readyState === WebSocket.OPEN) {
          socket.send(JSON.stringify({ type: 'ping' }))
        }
      }, 30000)
    }
    socket.onmessage = (event) => {
      try {
        const data = JSON.parse(event.data)
        handleMessage(data)
      } catch (e) {}
    }
    socket.onclose = () => {
      if (joined.value) {
        joined.value = false
        ElMessage.info('连接已断开')
        cleanup()
      }
    }
    socket.onerror = () => {
      joining.value = false
      ElMessage.error('连接失败，请稍后重试')
      cleanup()
    }
  } catch (e) {
    joining.value = false
    ElMessage.error('无法建立连接')
  }
}

function handleMessage(data) {
  if (data.type === 'system') {
    messages.value.push({ id: `sys_${Date.now()}_${Math.random()}`, type: 2, content: data.content })
  } else if (data.type === 'message') {
    messages.value.push({
      id: data.id,
      type: 1,
      nickname: data.nickname,
      avatar: data.avatar,
      content: data.content,
      createTime: data.createTime
    })
  } else if (data.type === 'online') {
    onlineUsers.value = data.users || []
    onlineCount.value = onlineUsers.value.length
  }
  scrollToBottom()
}

function send() {
  if (!inputMsg.value.trim()) return
  if (socket && socket.readyState === WebSocket.OPEN) {
    socket.send(JSON.stringify({ type: 'message', content: inputMsg.value.trim() }))
    inputMsg.value = ''
  } else {
    ElMessage.warning('连接已断开，请重新加入')
  }
}

function leave() {
  if (socket) socket.close()
  cleanup()
  joined.value = false
  myNickname.value = ''
}

function cleanup() {
  if (heartbeatTimer) {
    clearInterval(heartbeatTimer)
    heartbeatTimer = null
  }
  socket = null
}

function scrollToBottom() {
  nextTick(() => {
    if (messageBox.value) {
      messageBox.value.scrollTop = messageBox.value.scrollHeight
    }
  })
}

function handleScroll() {
  if (messageBox.value && messageBox.value.scrollTop <= 10 && hasMore.value && !loadingHistory.value) {
    loadHistory()
  }
}

onMounted(async () => {
  await loadHistory()
  try {
    const res = await getChatOnline()
    onlineUsers.value = res.data || []
    onlineCount.value = onlineUsers.value.length
  } catch (e) {}
  scrollToBottom()
})

onBeforeUnmount(() => {
  cleanup()
  if (socket) socket.close()
})
</script>

<style scoped>
.page-title {
  font-size: 26px;
  font-weight: 800;
  color: var(--text-primary);
  margin: 0;
  letter-spacing: -0.02em;
  display: flex;
  align-items: center;
  gap: 10px;
}
.page-title::before {
  content: '';
  width: 5px;
  height: 24px;
  border-radius: 99px;
  background: var(--brand-gradient);
}
.chat-card {
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: 1.25rem;
  overflow: hidden;
  box-shadow: var(--shadow-soft);
}
.chat-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 18px;
  border-bottom: 1px solid var(--border);
}
.chat-body {
  background: var(--surface-muted);
}
.chat-input {
  padding: 14px;
  border-top: 1px solid var(--border);
  background: var(--surface);
}
.msg-own {
  background: var(--brand-gradient);
  color: #fff;
  box-shadow: var(--shadow-float);
}
.msg-other {
  background: var(--surface);
  color: var(--text-primary);
  border: 1px solid var(--border);
}
.online-panel {
  background: var(--surface-muted);
}
.online-title {
  color: var(--text-primary);
}
</style>
