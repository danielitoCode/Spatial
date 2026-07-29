<script lang="ts">
  import ArchitectureVisual from './ArchitectureVisual.svelte';

  const benefits = [
    'Compose-first API: Scene, Element, Modifier3D, and CameraState.',
    'Modular pipeline: compose → scene graph → GLES renderer (no Vulkan yet).',
    'Typed units (meters, degrees) and orbit / pinch gestures on device.'
  ];
</script>

<section id="architecture-overview" class="py-24 bg-gradient-to-b from-[#05070D] to-[#11131a]">
  <div class="max-w-7xl mx-auto px-6 grid md:grid-cols-2 gap-16 items-center">
    <ArchitectureVisual />

    <div class="space-y-6">
      <div class="inline-block px-3.5 py-1 rounded-full bg-[#19E6D2]/10 border border-[#19E6D2]/20 text-[#19E6D2] font-mono text-xs font-semibold tracking-wider">
        ARCHITECTURE
      </div>

      <h2 class="text-3xl md:text-5xl font-bold text-[#e1e2ec]">What is Spatial?</h2>

      <p class="text-base md:text-lg text-[#bacac6] leading-relaxed">
        Spatial is a declarative 3D library for Android inspired by Jetpack Compose. It is not a full game engine: it focuses on scene composition, camera motion, and hiding GLES boilerplate behind a clean Kotlin API.
      </p>

      <ul class="space-y-3 pt-2">
        {#each benefits as benefit}
          <li class="flex items-start gap-3 text-[#bacac6]">
            <span class="material-symbols-outlined text-[#19E6D2] mt-0.5 text-xl">check_circle</span>
            <span class="text-sm md:text-base">{benefit}</span>
          </li>
        {/each}
      </ul>
    </div>
  </div>
</section>
