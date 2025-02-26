const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    port: 8787, // 端口号
    open: true, // 自动打开窗口
    proxy: {
      '/api': {
        // target: 'http://116.62.87.161:7070',
        // target: 'http://10.192.169.195:7070',
        target: 'http://bilimili-backend:7070',
        changeOrigin: true,
        ws: true,
        pathRewrite: {
          "^/api": ""
        }
      },
    },
    client: {
      overlay: false, // 关闭 Uncaught error 的全屏提示，太烦人了
    },
  }
})
