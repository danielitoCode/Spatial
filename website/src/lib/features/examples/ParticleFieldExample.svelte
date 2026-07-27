<script lang="ts">
  import { Canvas } from '@threlte/core';
  import InternalParticleFieldScene from './InternalParticleFieldScene.svelte';
  import GlassPanel from '../../components/GlassPanel.svelte';
  import GradientText from '../../components/GradientText.svelte';

  let particleCount = $state(400);
  let speed = $state(1.5);
</script>

<GlassPanel class="p-6 rounded-3xl bg-[#0A0E17]/80 border border-[#1C2638] flex flex-col gap-6 overflow-hidden">
  <div class="border-b border-[#1C2638] pb-6">
    <div class="flex justify-between items-end">
        <div class="space-y-1">
            <h3 class="text-2xl font-black text-[#e1e2ec]">Particle <GradientText>Constellations</GradientText></h3>
            <p class="text-xs text-[#6F7A90] font-medium tracking-tight">Massive GPU-accelerated point clouds for immersive app backgrounds</p>
        </div>
        <div class="px-3 py-1 bg-[#8B5CF6]/10 rounded-full border border-[#8B5CF6]/30 text-[10px] text-[#8B5CF6] font-mono font-bold tracking-widest">
            VBO_INSTANCING: ACTIVE
        </div>
    </div>
  </div>

  <div class="grid lg:grid-cols-12 gap-8">
    <div class="lg:col-span-8 h-80 relative bg-[#05070D] rounded-3xl overflow-hidden border border-[#1C2638] group shadow-2xl">
      <Canvas>
        <InternalParticleFieldScene {particleCount} {speed} />
      </Canvas>

      <div class="absolute top-4 right-4 bg-[#0A0E17]/90 px-3 py-1.5 rounded-xl text-[10px] font-mono text-[#19E6D2] border border-[#1C2638] backdrop-blur-md">
        ACTIVE_VERTICES: <span class="text-white">{particleCount}</span>
      </div>
    </div>

    <div class="lg:col-span-4 flex flex-col justify-center gap-6 bg-[#0A0E17] p-6 rounded-3xl border border-[#1C2638]">
      <div class="space-y-4">
        <div class="space-y-2">
            <div class="flex justify-between text-[#e1e2ec] font-mono text-[11px] font-bold">
                <span class="text-[#A6B0C3]">Density</span>
                <span class="text-[#8B5CF6]">{particleCount} pts</span>
            </div>
            <input type="range" min="100" max="2000" step="50" bind:value={particleCount} class="w-full h-1.5 bg-[#1C2638] rounded-full appearance-none accent-[#8B5CF6] cursor-pointer" />
        </div>

        <div class="space-y-2">
            <div class="flex justify-between text-[#e1e2ec] font-mono text-[11px] font-bold">
                <span class="text-[#A6B0C3]">Rotation Speed</span>
                <span class="text-[#19E6D2]">{speed.toFixed(1)}x</span>
            </div>
            <input type="range" min="0.1" max="5" step="0.1" bind:value={speed} class="w-full h-1.5 bg-[#1C2638] rounded-full appearance-none accent-[#19E6D2] cursor-pointer" />
        </div>
      </div>

      <div class="p-4 bg-[#101624] rounded-2xl border border-[#1C2638] font-mono text-[11px] space-y-1 shadow-inner relative overflow-hidden">
        <div class="absolute -right-2 -bottom-2 opacity-5 text-4xl font-black italic">API</div>
        <div class="text-[#19E6D2]">SpatialParticles</div>(<br />
        &nbsp;&nbsp;count = <span class="text-[#8B5CF6] font-bold">{particleCount}</span>,<br />
        &nbsp;&nbsp;speed = <span class="text-[#159FE8] font-bold">{speed.toFixed(1)}f</span>,<br />
        &nbsp;&nbsp;mesh = <span class="text-white">Point.Circle</span><br />
        )
      </div>
    </div>
  </div>
</GlassPanel>
