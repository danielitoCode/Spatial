<script lang="ts">
  import { Canvas } from '@threlte/core';
  import Viewport3DScene from './Viewport3DScene.svelte';
  import GlassPanel from '../../components/GlassPanel.svelte';

  let speed = $state(1);
</script>

<div class="lg:col-span-7 h-[420px] lg:h-auto relative bg-[#05070D] min-h-[380px] flex flex-col justify-between">
  <!-- Top FPS Badge -->
  <div class="absolute top-4 right-4 z-10 flex gap-2">
    <GlassPanel class="px-3 py-1 rounded-full text-[11px] font-mono text-[#19E6D2] flex items-center gap-2">
      <span class="w-2 h-2 rounded-full bg-[#19E6D2] animate-pulse"></span>
      60 FPS
    </GlassPanel>
  </div>

  <!-- Threlte 3D Viewport Canvas -->
  <div class="absolute inset-0 w-full h-full">
    <Canvas>
      <Viewport3DScene rotationSpeed={speed} />
    </Canvas>
  </div>

  <!-- Bottom HUD Elements -->
  <div class="absolute bottom-4 left-4 right-4 flex justify-between items-end pointer-events-none z-10">
    <GlassPanel class="p-2.5 rounded-lg pointer-events-auto flex items-center gap-3">
      <span class="material-symbols-outlined text-[#19E6D2] text-lg">view_in_ar</span>
      <input
        type="range"
        min="0.1"
        max="3"
        step="0.1"
        bind:value={speed}
        class="w-32 h-1 bg-[#1C2638] rounded-lg appearance-none cursor-pointer accent-[#19E6D2]"
      />
    </GlassPanel>

    <div class="text-right font-mono text-[10px] text-[#6F7A90] opacity-70 bg-[#0A0E17]/80 p-2 rounded border border-[#1C2638]">
      RENDER_PASS: PBR_HDR_01<br />
      TRIANGLES: 42,900
    </div>
  </div>
</div>
