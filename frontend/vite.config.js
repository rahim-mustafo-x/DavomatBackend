import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({

  plugins: [react()],

  server: {
    host: '0.0.0.0',
    port: 3000,

    allowedHosts: [
      'davomat-app.uz',
      'www.davomat-app.uz',
      'localhost',
      '127.0.0.1'
    ],

    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },

      '/auth': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },

      '/ws': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        ws: true
      }
    }
  },

  preview: {
    host: '0.0.0.0',
    port: 4173
  },

  build: {
    outDir: 'dist',
    emptyOutDir: true
  }

})