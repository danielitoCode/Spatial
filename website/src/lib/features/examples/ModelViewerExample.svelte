<script lang="ts">
  import { Canvas } from '@threlte/core';
  import InternalModelViewerScene from './InternalModelViewerScene.svelte';
  import GlassPanel from '../../components/GlassPanel.svelte';
  import GradientText from '../../components/GradientText.svelte';

  let activeTab = $state<'3d' | 'code'>('3d');
  let metalness = $state(0.85);
  let roughness = $state(0.1);
  let wireframe = $state(false);
  let color = $state('#19E6D2');
  let autoRotate = $state(true);

  const generatedKotlinCode = $derived(
`// Real Spatial API (Android) — Core #1 flat material
Scene(
    modifier = Modifier.fillMaxSize(),
    renderHostFactory = DefaultSceneRenderHostFactory,
    cameraState = rememberCameraState(),
    gestures = Gestures.orbitAndZoom(),
) {
    Element.Cube(
        modifier = Modifier3D.Default
            .size(2f.meters)
            .color(/* parse "${color}" to RGB */)
    )
}

// Note: metalness/roughness UI above is a WebGL preview only.
// Full PBR shading is not the Core #1 renderer path.`
  );
</script>

<GlassPanel class="p-6 rounded-3xl bg-[#0A0E17]/80 border border-[#1C2638] flex flex-col gap-6 overflow-hidden">
  <div class="flex flex-wrap justify-between items-center gap-4 border-b border-[#1C2638] pb-6">
    <div class="space-y-1">
      <h3 class="text-2xl font-black text-[#e1e2ec]">Material <GradientText>Preview</GradientText></h3>
      <p class="text-xs text-[#6F7A90] font-medium tracking-tight">WebGL illustration — Android Core #1 uses flat color materials</p>
    </div>

    <!-- Tab Switcher -->
    <div class="flex bg-[#101624] p-1.5 rounded-2xl border border-[#1C2638] shadow-inner">
      <button
        onclick={() => (activeTab = '3d')}
        class="px-4 py-2 text-xs font-bold rounded-xl transition-all {activeTab === '3d'
          ? 'bg-[#19E6D2] text-[#00201c] shadow-md'
          : 'text-[#6F7A90] hover:text-[#e1e2ec]'}"
      >
        Live 3D
      </button>
      <button
        onclick={() => (activeTab = 'code')}
        class="px-4 py-2 text-xs font-bold rounded-xl transition-all {activeTab === 'code'
          ? 'bg-[#19E6D2] text-[#00201c] shadow-md'
          : 'text-[#6F7A90] hover:text-[#e1e2ec]'}"
      >
        Compose API
      </button>
    </div>
  </div>

  {#if activeTab === '3d'}
    <div class="grid lg:grid-cols-12 gap-8">
      <!-- 3D Canvas Viewport -->
      <div class="lg:col-span-8 h-[400px] relative bg-[#05070D] rounded-3xl overflow-hidden border border-[#1C2638] group">
        <Canvas>
          <InternalModelViewerScene
            {color}
            {metalness}
            {roughness}
            {wireframe}
            {autoRotate}
          />
        </Canvas>

        <div class="absolute bottom-4 left-4 flex gap-2">
            <div class="bg-[#0A0E17]/90 px-3 py-1.5 rounded-xl text-[10px] font-mono text-[#19E6D2] border border-[#1C2638] backdrop-blur-md">
                PREVIEW: <span class="text-white">Three.js</span>
            </div>
            <div class="bg-[#0A0E17]/90 px-3 py-1.5 rounded-xl text-[10px] font-mono text-[#8B5CF6] border border-[#1C2638] backdrop-blur-md">
                ENGINE: <span class="text-white">spatial-*</span>
            </div>
        </div>
      </div>

      <!-- Controls Panel -->
      <div class="lg:col-span-4 flex flex-col justify-between gap-6 bg-[#0A0E17] p-6 rounded-3xl border border-[#1C2638]">
        <div class="space-y-4">
            <div>
              <span class="block text-[#6F7A90] font-mono text-[10px] uppercase font-bold mb-3 tracking-widest">Albedo Palette</span>
              <div class="flex gap-2.5">
                {#each ['#19E6D2', '#159FE8', '#8B5CF6', '#F25933', '#FFFFFF'] as c}
                  <button
                    onclick={() => (color = c)}
                    aria-label="Select color {c}"
                    class="w-8 h-8 rounded-2xl border border-white/10 transition-all hover:scale-110 active:scale-95 {color === c ? 'ring-2 ring-[#19E6D2] ring-offset-2 ring-offset-[#0A0E17] scale-110' : ''}"
                    style="background-color: {c}"
                  ></button>
                {/each}
              </div>
            </div>

            <div class="space-y-3 pt-2">
              <div class="space-y-1">
                <div class="flex justify-between text-[#e1e2ec] font-mono text-[11px] font-bold">
                    <span class="text-[#A6B0C3]">Metalness (web only)</span>
                    <span class="text-[#19E6D2]">{Math.round(metalness * 100)}%</span>
                </div>
                <input type="range" min="0" max="1" step="0.01" bind:value={metalness} class="w-full h-1.5 bg-[#1C2638] rounded-full appearance-none accent-[#19E6D2] cursor-pointer" />
              </div>

              <div class="space-y-1">
                <div class="flex justify-between text-[#e1e2ec] font-mono text-[11px] font-bold">
                    <span class="text-[#A6B0C3]">Roughness (web only)</span>
                    <span class="text-[#19E6D2]">{Math.round(roughness * 100)}%</span>
                </div>
                <input type="range" min="0" max="1" step="0.01" bind:value={roughness} class="w-full h-1.5 bg-[#1C2638] rounded-full appearance-none accent-[#19E6D2] cursor-pointer" />
              </div>
            </div>
        </div>

        <div class="grid grid-cols-1 gap-3">
          <label class="flex justify-between items-center p-3 bg-[#101624] rounded-2xl border border-[#1C2638] cursor-pointer hover:bg-[#19E6D2]/5 group transition-all">
            <div class="flex flex-col">
                <span class="text-[11px] font-bold text-[#e1e2ec] group-hover:text-[#19E6D2]">Wireframe Mode</span>
                <span class="text-[9px] text-[#6F7A90] font-mono">WEB_PREVIEW</span>
            </div>
            <input type="checkbox" bind:checked={wireframe} class="accent-[#19E6D2] w-4 h-4 rounded" />
          </label>

          <label class="flex justify-between items-center p-3 bg-[#101624] rounded-2xl border border-[#1C2638] cursor-pointer hover:bg-[#19E6D2]/5 group transition-all">
            <div class="flex flex-col">
                <span class="text-[11px] font-bold text-[#e1e2ec] group-hover:text-[#19E6D2]">Auto Rotate</span>
                <span class="text-[9px] text-[#6F7A90] font-mono">CAMERA_IDLE</span>
            </div>
            <input type="checkbox" bind:checked={autoRotate} class="accent-[#19E6D2] w-4 h-4 rounded" />
          </label>
        </div>
      </div>
    </div>
  {:else}
    <div class="bg-[#05070D] p-6 rounded-3xl border border-[#1C2638] font-mono text-xs text-[#19E6D2] overflow-x-auto shadow-inner">
      <div class="flex justify-between items-center mb-4 pb-4 border-b border-white/5">
          <span class="text-[#6F7A90] text-[10px] tracking-widest">SPATIAL_ANDROID_API</span>
          <button class="text-[10px] font-bold bg-[#19E6D2]/10 px-2 py-1 rounded border border-[#19E6D2]/30 hover:bg-[#19E6D2]/20 transition-all">COPY_TO_CLIPBOARD</button>
      </div>
      <pre class="text-[#e1e2ec]"><code>{generatedKotlinCode}</code></pre>
    </div>
  {/if}
</GlassPanel>

<style>
  input[type="range"]::-webkit-slider-thumb {
    -webkit-appearance: none;
    height: 16px;
    width: 16px;
    border-radius: 50%;
    background: #19E6D2;
    box-shadow: 0 0 12px rgba(25, 230, 210, 0.4);
    border: 2px solid #0A0E17;
  }
</style>
