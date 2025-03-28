import vue from '@vitejs/plugin-vue'
import { defineConfig } from 'vite'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
  ],
  resolve: {
    alias: {
      '@': 'src'
    }
  },
  server: {
    port: 5378,
    proxy: {
      // '/api': {
      //   target: 'http://124.243.181.49',
      //   // target: 'http://127.0.0.1:8882/bengo',
      //   // rewrite: (path) => path.replace(/^\/api/, ""),
      //   changeOrigin: true
      // }
      // '/api': {
      //   target: 'http://127.0.0.1:8882/bengo',  // 后端服务地址
      //   rewrite: (path) => path.replace(/^\/api/, ""),  // 将前缀 /api 替换为 /bengo
      //   changeOrigin: true
      // }
      // 处理 /bengo 相关接口
      '/api/bengo': {
        target: 'http://124.243.181.49:8882',
        // target: 'http://127.0.0.1:8882',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, ''), // /api/bengo/chat -> /bengo/chat
      },
      // 处理 /authorize 相关接口
      '/api/authorize': {
        target: 'http://124.243.181.49:8882',
        // target: 'http://127.0.0.1:8882',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, ''), // /api/authorize/permission/list -> /authorize/permission/list
      },
      // 处理根路径或其他接口（可选，用于调试 iframe）
      '/api': {
        target: 'http://124.243.181.49:8882',
        // target: 'http://127.0.0.1:8882',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, ''), // /api/xxx -> /xxx
      }
    }
  },
  rules: [
    {
      test: /\.scss$/,
      use: [
        'style-loader',
        {
          loader: 'css-loader',
          options: { sourceMap: true },
        },
        {
          loader: 'sass-loader',
          options: { sourceMap: true },
        },
      ],
    },
  ],
})
