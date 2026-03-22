import vue from '@vitejs/plugin-vue'
import { defineConfig } from 'vitest/config'

export default defineConfig({
  plugins: [vue()],
  test: {
    environment: 'node',
    environmentMatchGlobs: [['test/components/**/*.test.ts', 'jsdom']],
    include: ['test/**/*.test.ts']
  }
})
