<script lang="ts">
  import ModelViewerExample from './ModelViewerExample.svelte';
  import ParticleFieldExample from './ParticleFieldExample.svelte';
  import PhysicsSimExample from './PhysicsSimExample.svelte';
  import InstancedMeshExample from './InstancedMeshExample.svelte';
  import GradientText from '../../components/GradientText.svelte';
</script>

<div class="py-32 bg-[#05070D] min-h-screen">
  <div class="max-w-7xl mx-auto px-6 space-y-24">
    <!-- Header -->
    <div class="text-center max-w-4xl mx-auto space-y-6">
      <div class="inline-flex items-center gap-2 px-4 py-1 rounded-full bg-primary/10 border border-primary/20 text-primary font-black text-[10px] tracking-[0.3em] uppercase">
        Advanced Showcases
      </div>
      <h1 class="text-4xl md:text-6xl lg:text-7xl font-black text-white tracking-tighter leading-tight">
        Mastering <GradientText>Visual State</GradientText>
      </h1>
      <p class="text-lg text-silver/50 leading-relaxed max-w-2xl mx-auto font-medium">
        Discover how Spatial bridges the gap between complex 3D rendering and reactive UI. Inspect live instances, toggle shaders, and export production source code.
      </p>
    </div>

    <!-- Examples Stack -->
    <div class="grid grid-cols-1 gap-16 relative">
      <!-- Connection Lines (Visual Polish) -->
      <div class="absolute left-1/2 top-0 bottom-0 w-px bg-gradient-to-b from-transparent via-white/5 to-transparent hidden xl:block"></div>

      <div class="relative z-10 space-y-16">
        <ModelViewerExample />
        <InstancedMeshExample />
        <ParticleFieldExample />
        <PhysicsSimExample />
      </div>
    </div>

    <!-- Bottom CTA -->
    <div class="text-center pt-12">
        <p class="text-silver/40 text-sm mb-6 uppercase tracking-widest font-bold">Ready to build your own?</p>
        <div class="flex justify-center gap-4">
            <a href="#playground" class="px-8 py-3 bg-white/5 hover:bg-white/10 border border-white/10 rounded-2xl text-white font-black text-xs uppercase tracking-widest transition-all">Launch Playground</a>
            <a href="#docs" class="px-8 py-3 text-primary font-black text-xs uppercase tracking-widest transition-all hover:underline">Read Documentation</a>
        </div>
    </div>
  </div>
</div>
