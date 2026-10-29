import hljs from 'highlight.js/lib/common'
import 'highlight.js/styles/github.css'

/**
 * v-highlight 指令：对容器内 <pre><code> 做语法高亮
 * 用法：<div class="article-content" v-highlight v-html="html" />
 */
export const highlightDirective = {
  mounted(el) {
    apply(el)
  },
  updated(el) {
    apply(el)
  }
}

function apply(el) {
  if (!el) return
  const blocks = el.querySelectorAll('pre code')
  blocks.forEach(block => {
    // 已高亮的块跳过，避免重复包裹
    if (block.dataset.highlighted === 'true') return
    try {
      hljs.highlightElement(block)
      block.dataset.highlighted = 'true'
    } catch (e) {
      // 单块高亮失败不影响其余内容
    }
  })
}

export default highlightDirective
