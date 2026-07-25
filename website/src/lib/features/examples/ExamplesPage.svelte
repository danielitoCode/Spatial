<script lang="ts">
  import ModelViewerExample from './ModelViewerExample.svelte';
  import ParticleFieldExample from './ParticleFieldExample.svelte';
  import PhysicsSimExample from './PhysicsSimExample.svelte';
  import GradientText from '../../components/GradientText.svelte';
</script>

<div class="py-16 bg-[#05070D] min-h-[85vh]">
  <div class="max-w-7xl mx-auto px-6 space-y-12">
    <!-- Header -->
    <div class="text-center max-w-3xl mx-auto">
      <div class="inline-block px-3.5 py-1 rounded-full bg-[#19E6D2]/10 border border-[#19E6D2]/20 text-[#19E6D2] font-mono text-xs font-semibold tracking-wider mb-4">
        INTERACTIVE EXAMPLES
      </div>
      <h1 class="text-3xl md:text-5xl font-extrabold text-[#e1e2ec] mb-4">
        Explore <GradientText>Spatial Capabilities</GradientText>
      </h1>
      <p class="text-base text-[#bacac6] leading-relaxed">
        Test interactive 3D rendering samples built with Spatial components. Toggle settings, inspect shaders, and copy generated Jetpack Compose code snippets.
      </p>
    </div>

    <!-- Examples Stack -->
    <div class="space-y-10">
      <ModelViewerExample />
      <ParticleFieldExample />
      <PhysicsSimExample />
    </div>
  </div>
</div>
