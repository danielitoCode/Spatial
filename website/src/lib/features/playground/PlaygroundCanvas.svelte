<script lang="ts">
  import { Canvas } from '@threlte/core';
  import InternalPlaygroundScene from './InternalPlaygroundScene.svelte';

  interface Props {
    shape?: 'box' | 'sphere' | 'torus' | 'cylinder' | 'plane';
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
    lightColor = '#ffffff',
    autoRotate = true,
    rotationSpeed = 1
  }: Props = $props();
</script>

<div class="relative w-full h-full min-h-[400px] bg-[#05070D] rounded-[3rem] overflow-hidden border border-white/5 shadow-[0_0_50px_rgba(0,0,0,0.5)] group">
  <!-- Subtle Vignette -->
  <div class="absolute inset-0 pointer-events-none z-10 shadow-[inset_0_0_150px_rgba(0,0,0,0.9)]"></div>

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

  <!-- Overlay HUD - Top Left -->
  <div class="absolute top-8 left-8 flex flex-col gap-2 z-20 pointer-events-none">
    <div class="flex items-center gap-3">
        <div class="w-2 h-2 rounded-full bg-primary animate-pulse shadow-[0_0_10px_#19E6D2]"></div>
        <div class="bg-black/60 backdrop-blur-xl px-4 py-1.5 rounded-full border border-white/10 shadow-lg shadow-black/40 flex items-center gap-4">
            <span class="text-[10px] font-black text-white tracking-[0.3em] uppercase">Studio View</span>
            <div class="w-px h-3 bg-white/20"></div>
            <span class="text-[9px] font-mono text-primary uppercase font-bold tracking-widest">{shape}</span>
        </div>
    </div>
  </div>

  <!-- Diagnostic Info - Bottom Right -->
  <div class="absolute bottom-8 right-8 z-20 pointer-events-none opacity-40 group-hover:opacity-100 transition-opacity duration-700">
    <div class="bg-black/40 backdrop-blur-md p-4 rounded-3xl border border-white/5 text-[9px] font-mono text-silver/60 space-y-1 shadow-2xl">
      <div class="flex justify-between gap-8 uppercase tracking-widest">
        <span>Draw_Calls</span>
        <span class="text-white font-bold">12</span>
      </div>
      <div class="flex justify-between gap-8 uppercase tracking-widest">
        <span>Shader_Engine</span>
        <span class="text-white font-bold">GLES_3.0_PBR</span>
      </div>
      <div class="flex justify-between gap-8 uppercase tracking-widest">
        <span>Geometry_Ref</span>
        <span class="text-primary font-black">SPATIAL_GEN_{shape.toUpperCase()}</span>
      </div>
    </div>
  </div>

  <!-- Ambient Environment Reflection Overlay (Fake) -->
  <div class="absolute inset-0 pointer-events-none opacity-10 mix-blend-overlay bg-[radial-gradient(circle_at_50%_0%,rgba(255,255,255,0.2),transparent_50%)]"></div>
</div>
