import { ref, watch } from 'vue'

// 主题切换：模块级单例，切换时同步 html.dark class + localStorage
const isDark = ref(localStorage.getItem('weblog_theme') === 'dark')

function applyTheme() {
  if (isDark.value) {
    document.documentElement.classList.add('dark')
  } else {
    document.documentElement.classList.remove('dark')
  }
  localStorage.setItem('weblog_theme', isDark.value ? 'dark' : 'light')
}

// 初始应用
applyTheme()

export function useTheme() {
  function toggle() {
    isDark.value = !isDark.value
    applyTheme()
  }

  function setTheme(dark) {
    isDark.value = dark
    applyTheme()
  }

  watch(isDark, applyTheme)

  return { isDark, toggle, setTheme }
}
