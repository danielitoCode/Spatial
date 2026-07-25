<script lang="ts">
  import { Canvas, T, useTask } from '@threlte/core';
  import * as THREE from 'three';

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

  let meshRef = $state<THREE.Mesh | undefined>(undefined);

  useTask((delta) => {
    if (!meshRef || !autoRotate) return;
    meshRef.rotation.y += delta * 1.2 * rotationSpeed;
    meshRef.rotation.x += delta * 0.6 * rotationSpeed;
  });
</script>

<div class="relative w-full h-full min-h-[400px] bg-[#05070D] rounded-2xl overflow-hidden border border-[#1C2638]">
  <Canvas>
    <T.PerspectiveCamera makeDefault position={[0, 0, 4]} fov={60} />
    <T.AmbientLight intensity={0.5} />
    <T.PointLight position={[3, 3, 3]} intensity={lightIntensity} color={lightColor} />
    <T.PointLight position={[-3, -3, 3]} intensity={lightIntensity * 0.6} color="#8B5CF6" />

    <T.Mesh bind:ref={meshRef}>
      {#if shape === 'box'}
        <T.BoxGeometry args={[1.3, 1.3, 1.3]} />
      {:else if shape === 'sphere'}
        <T.SphereGeometry args={[0.95, 32, 32]} />
      {:else if shape === 'torus'}
        <T.TorusGeometry args={[0.85, 0.35, 24, 64]} />
      {:else if shape === 'cylinder'}
        <T.CylinderGeometry args={[0.7, 0.7, 1.4, 32]} />
      {/if}

      <T.MeshStandardMaterial
        {color}
        {emissive}
        {metalness}
        {roughness}
        {wireframe}
      />
    </T.Mesh>
  </Canvas>

  <!-- Overlay HUD -->
  <div class="absolute top-4 left-4 bg-[#0A0E17]/80 px-3 py-1.5 rounded-lg border border-[#1C2638] text-[10px] font-mono text-[#19E6D2]">
    STUDIO_MODE: LIVE_VIEWPORT
  </div>
</div>
