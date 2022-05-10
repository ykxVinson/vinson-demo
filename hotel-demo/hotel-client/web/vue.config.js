const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  configureWebpack: {
    externals: {
      'AMap': 'AMap' // 高德地图配置
    }
  }
})
