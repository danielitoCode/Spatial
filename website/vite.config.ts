import { defineConfig } from 'vite'
import { svelte } from '@sveltejs/vite-plugin-svelte'
import tailwindcss from '@tailwindcss/vite'

// https://vite.dev/config/
// GitHub Pages project site: https://<user>.github.io/Spatial/
// Do NOT rely on process.env.NODE_ENV here — it is often unset when the
// config module is evaluated in CI, which left base as '/' and broke public assets.
export default defineConfig(({ command }) => ({
  plugins: [
    tailwindcss(),
    svelte()
  ],
  base: command === 'build' ? '/Spatial/' : '/',
}))
