/** @type {import('tailwindcss').Config} */
export default {
  darkMode: 'class',
  content: ['./index.html', './src/**/*.{vue,js,ts,jsx,tsx}'],
  theme: {
    extend: {
      colors: {
        // 品牌色体系（靛蓝 -> 紫罗兰 渐变）
        brand: {
          50: '#eef2ff',
          100: '#e0e7ff',
          200: '#c7d2fe',
          300: '#a5b4fc',
          400: '#818cf8',
          500: '#6366f1',
          600: '#4f46e5',
          700: '#4338ca',
          800: '#3730a3',
          900: '#312e81'
        },
        // 强调色（紫罗兰）
        accent: {
          DEFAULT: '#8b5cf6',
          500: '#8b5cf6',
          600: '#7c3aed'
        }
      },
      fontFamily: {
        // 标题衬线字体，正文无衬线字体
        serif: ['"Playfair Display"', 'Georgia', '"Songti SC"', '"Noto Serif SC"', 'serif'],
        sans: ['-apple-system', 'BlinkMacSystemFont', '"Segoe UI"', 'Roboto', '"PingFang SC"', '"Microsoft YaHei"', 'sans-serif']
      },
      borderRadius: {
        xl: '0.875rem',
        '2xl': '1.25rem',
        '3xl': '1.75rem'
      },
      boxShadow: {
        soft: '0 1px 2px rgba(16, 24, 40, 0.04), 0 4px 16px rgba(16, 24, 40, 0.06)',
        card: '0 2px 4px rgba(16, 24, 40, 0.04), 0 12px 32px -8px rgba(16, 24, 40, 0.12)',
        float: '0 8px 24px -6px rgba(79, 70, 229, 0.25)'
      },
      backgroundImage: {
        'brand-gradient': 'linear-gradient(135deg, #6366f1 0%, #8b5cf6 50%, #d946ef 100%)',
        'brand-soft': 'linear-gradient(135deg, rgba(99,102,241,0.08) 0%, rgba(139,92,246,0.08) 100%)'
      }
    }
  },
  plugins: []
}
