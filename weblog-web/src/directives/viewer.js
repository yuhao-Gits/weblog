import Viewer from 'viewerjs'
import 'viewerjs/dist/viewer.css'

/**
 * v-viewer 指令：容器内图片点击放大预览
 */
export const viewerDirective = {
  mounted(el) {
    if (!el) return
    el.__viewer__ = new Viewer(el, {
      // 只在图片被点击时触发
      trigger: 'img',
      navbar: false,
      toolbar: { zoomIn: true, zoomOut: true, reset: true, rotateLeft: true, rotateRight: true }
    })
  },
  updated(el) {
    if (el && el.__viewer__) {
      el.__viewer__.update()
    }
  },
  unmounted(el) {
    if (el && el.__viewer__) {
      el.__viewer__.destroy()
      el.__viewer__ = null
    }
  }
}

export default viewerDirective
