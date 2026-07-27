<script lang="ts">
  import { Canvas } from '@threlte/core';
  import InternalArchitectureScene from './InternalArchitectureScene.svelte';
</script>

<div class="relative aspect-square w-full rounded-[3rem] border border-white/5 bg-[#0D1117] overflow-hidden group shadow-2xl min-h-[400px]">
  <!-- Technical HUD Overlays -->
  <div class="absolute top-8 left-8 z-20 pointer-events-none opacity-60">
      <div class="flex items-center gap-2 mb-1">
          <div class="w-1 h-3 bg-primary rounded-full"></div>
          <span class="text-[10px] font-black text-white uppercase tracking-widest leading-none">Architecture_Stack</span>
      </div>
      <div class="text-[8px] font-mono text-silver/40 ml-3">MODEL_TYPE: LAYERED_HIERARCHY</div>
  </div>

  <div class="absolute bottom-8 right-8 z-20 pointer-events-none opacity-40">
      <div class="text-[8px] font-mono text-primary text-right uppercase tracking-[0.4em]">system_active</div>
      <div class="flex gap-1 justify-end mt-1">
          <div class="w-1 h-1 bg-white/20 rounded-full animate-pulse"></div>
          <div class="w-1 h-1 bg-white/20 rounded-full animate-pulse [animation-delay:0.2s]"></div>
          <div class="w-1 h-1 bg-white/20 rounded-full animate-pulse [animation-delay:0.4s]"></div>
      </div>
  </div>

  <!-- 3D Canvas -->
  <div class="absolute inset-0 w-full h-full">
    <Canvas>
      <InternalArchitectureScene />
    </Canvas>
  </div>

  <!-- Vignette -->
  <div class="absolute inset-0 pointer-events-none z-10 shadow-[inset_0_0_100px_rgba(0,0,0,0.6)]"></div>
</div>
