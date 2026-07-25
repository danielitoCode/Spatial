<script lang="ts">
  import GlassPanel from '../../components/GlassPanel.svelte';

  const codeSnippet = `GLSurfaceView view = new GLSurfaceView(ctx);
view.setRenderer(new Renderer() {
  @Override
  public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    // 50 lines of matrix setup
    // 100 lines of buffer loading
    // Global state everywhere
  }
});`;
</script>

<GlassPanel class="p-6 rounded-xl border-red-900/40 bg-[#0A0E17]/80">
  <div class="flex justify-between items-center mb-4 border-b border-[#1C2638] pb-2">
    <span class="font-mono text-xs font-bold text-red-400">LEGACY IMPERATIVE</span>
    <span class="material-symbols-outlined text-red-400 text-lg">close</span>
  </div>

  <pre class="font-mono text-xs md:text-sm text-[#bacac6] overflow-x-auto leading-relaxed"><code>{codeSnippet}</code></pre>
</GlassPanel>
