<script lang="ts">
  import { Grid, Col, Row } from 'svelte-layouts';
  import PlaygroundCanvas from './PlaygroundCanvas.svelte';
  import PlaygroundControls from './PlaygroundControls.svelte';
  import ComposeGenerator from './ComposeGenerator.svelte';
  import GradientText from '../../components/GradientText.svelte';

  let shape = $state<'box' | 'sphere' | 'torus' | 'cylinder'>('torus');
  let color = $state('#19E6D2');
  let emissive = $state('#000000');
  let metalness = $state(0.8);
  let roughness = $state(0.15);
  let wireframe = $state(false);
  let lightIntensity = $state(10);
  let lightColor = $state('#19E6D2');
  let autoRotate = $state(true);

  function handleApplyPreset(p: any) {
    // Already handled via bindings, but could add global effects here
  }
</script>

<div class="py-16 bg-[#05070D] min-h-[85vh]">
  <div class="max-w-7xl mx-auto px-6">
    <!-- Header -->
    <div class="text-center max-w-3xl mx-auto mb-16">
      <div class="inline-block px-3.5 py-1 rounded-full bg-[#8B5CF6]/10 border border-[#8B5CF6]/20 text-[#8B5CF6] font-mono text-xs font-semibold tracking-wider mb-4">
        3D SCENE STUDIO
      </div>
      <h1 class="text-3xl md:text-5xl font-extrabold text-[#e1e2ec] mb-4">
        Spatial <GradientText>Interactive Playground</GradientText>
      </h1>
      <p class="text-base text-[#bacac6] leading-relaxed">
        Design and tweak 3D scenes in real time. Adjust geometries, materials, lighting, and instantly export production-ready Kotlin Compose code.
      </p>
    </div>

    <!-- Main Studio Layout using svelte-layouts -->
    <Grid cols={12} gap="2rem">
      <!-- 3D Canvas Viewport -->
      <Col span={12} spanLg={7}>
        <div class="h-[500px]">
          <PlaygroundCanvas
            {shape}
            {color}
            {emissive}
            {metalness}
            {roughness}
            {wireframe}
            {lightIntensity}
            {lightColor}
            {autoRotate}
          />
        </div>
      </Col>

      <!-- Controls Panel -->
      <Col span={12} spanLg={5}>
        <PlaygroundControls
          bind:shape
          bind:color
          bind:emissive
          bind:metalness
          bind:roughness
          bind:wireframe
          bind:lightIntensity
          bind:lightColor
          bind:autoRotate
          onApplyPreset={handleApplyPreset}
        />
      </Col>

      <!-- Generated Code Export -->
      <Col span={12}>
        <div class="mt-8">
          <ComposeGenerator
            {shape}
            {color}
            {metalness}
            {roughness}
            {wireframe}
          />
        </div>
      </Col>
    </Grid>
  </div>
</div>
