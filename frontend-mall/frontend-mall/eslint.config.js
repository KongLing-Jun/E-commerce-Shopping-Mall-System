import { defineConfig, globalIgnores } from 'eslint/config'
import globals from 'globals'
import js from '@eslint/js'
import pluginVue from 'eslint-plugin-vue'
import skipFormatting from 'eslint-config-prettier/flat'

export default defineConfig([
  {
    name: 'app/files-to-lint',
    files: ['**/*.{vue,js,mjs,jsx}'],
  },

  globalIgnores(['**/dist/**', '**/dist-ssr/**', '**/coverage/**']),

  {
    languageOptions: {
      globals: {
        ...globals.browser,
      },
    },
  },

  js.configs.recommended,
  ...pluginVue.configs['flat/essential'],

  {
    rules: {
      // Keep single-word page component names (Home/Login/Cart) in this project.
      'vue/multi-word-component-names': 'off',
      // Do not fail lint on unused catch variables while preserving other checks.
      'no-unused-vars': ['warn', { caughtErrors: 'none' }],
    },
  },

  skipFormatting,
])
