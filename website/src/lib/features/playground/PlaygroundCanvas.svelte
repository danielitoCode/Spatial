<script lang="ts">
  import { Canvas } from '@threlte/core';
  import InternalPlaygroundScene from './InternalPlaygroundScene.svelte';

  interface Props {
    shape?: 'box' | 'sphere' | 'torus' | 'cylinder';
    color?: string;
    emissive?: string;
    metalness?: number;
    roughness?: number;
    wireframe?: boolean;
    lightIntensity?: number;
    lightColor?: string;
    autoRotate?: boolean;
    rotationSpeed?: number;
  }

  let {
    shape = 'box',
    color = '#19E6D2',
    emissive = '#000000',
    metalness = 0.5,
    roughness = 0.2,
    wireframe = false,
    lightIntensity = 10,
    lightColor = '#19E6D2',
    autoRotate = true,
    rotationSpeed = 1
  }: Props = $props();
</script>

<div class="relative w-full h-full min-h-[400px] bg-[#05070D] rounded-[2rem] overflow-hidden border border-[#1C2638] shadow-2xl">
  <Canvas>
    <InternalPlaygroundScene
      {shape}
      {color}
      {emissive}
      {metalness}
      {roughness}
      {wireframe}
      {lightIntensity}
      {lightColor}
      {autoRotate}
      {rotationSpeed}
    />
  </Canvas>

  <!-- Overlay HUD -->
  <div class="absolute top-6 left-6 flex flex-col gap-1">
    <div class="bg-[#19E6D2]/10 backdrop-blur-md px-3 py-1 rounded-full border border-[#19E6D2]/30 text-[10px] font-black text-[#19E6D2] tracking-widest uppercase">
      Spatial Studio v1.0
    </div>
    <div class="text-[9px] font-mono text-[#6F7A90] ml-1">
      RENDER_ENGINE: OPENGL_ES_3.0_EMULATED
    </div>
  </div>

  <div class="absolute bottom-6 left-6 right-6 flex justify-between items-end pointer-events-none">
    <div class="bg-[#0A0E17]/60 backdrop-blur-sm p-3 rounded-2xl border border-[#1C2638] pointer-events-auto">
       <div class="text-[9px] font-mono text-[#6F7A90] mb-1 uppercase tracking-tighter">Viewport Settings</div>
       <div class="flex gap-2">
          <div class="w-2 h-2 rounded-full bg-[#19E6D2] animate-pulse"></div>
          <div class="w-2 h-2 rounded-full bg-[#8B5CF6]"></div>
          <div class="w-2 h-2 rounded-full bg-[#F25933]"></div>
       </div>
    </div>
  </div>
</div>
