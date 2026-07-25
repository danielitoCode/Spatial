<script lang="ts">
  import { Canvas } from '@threlte/core';
  import Hero3DScene from './Hero3DScene.svelte';
</script>

<div class="absolute inset-0 w-full h-full opacity-60 pointer-events-none">
  <Canvas>
    <Hero3DScene />
  </Canvas>
</div>
