import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig(({ mode }) => {

  const isDev = mode === 'development'

  return {

    plugins: [react()],

    server: {
      host: true,        // 0.0.0.0
      port: 3000,

      proxy: {
        '/api': {
          target: 'http://localhost:8080',
          changeOrigin: true,
          secure: false
        },

        '/auth': {
          target: 'http://localhost:8080',
          changeOrigin: true,
          secure: false
        },

        '/ws': {
          target: 'http://localhost:8080',
          changeOrigin: true,
          ws: true
        }
      }
    },

    preview: {
      host: true,
      port: 4173
    },

    build: {
      outDir: 'dist',
      emptyOutDir: true,
      sourcemap: isDev,
      chunkSizeWarningLimit: 1000
    },

    define: {
      __APP_ENV__: JSON.stringify(mode)
    }

  }
})