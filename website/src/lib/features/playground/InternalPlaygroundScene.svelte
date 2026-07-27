<script lang="ts">
  import { T, useTask } from '@threlte/core';
  import { Grid as ThrelteGrid, ContactShadows, Float } from '@threlte/extras';
  import * as THREE from 'three';

  interface Props {
    shape: 'box' | 'sphere' | 'torus' | 'cylinder' | 'plane';
    color: string;
    emissive: string;
    metalness: number;
    roughness: number;
    wireframe: boolean;
    lightIntensity: number;
    lightColor: string;
    autoRotate: boolean;
    rotationSpeed?: number;
  }

  let {
    shape,
    color,
    emissive,
    metalness,
    roughness,
    wireframe,
    lightIntensity,
    lightColor,
    autoRotate,
    rotationSpeed = 1 // Default value to prevent NaN in rotations
  }: Props = $props();

  let meshRef = $state<THREE.Mesh | undefined>(undefined);

  useTask((delta) => {
    // rotationSpeed is guaranteed to be a number here
    if (!meshRef || !autoRotate) return;
    meshRef.rotation.y += delta * 0.5 * rotationSpeed;
    meshRef.rotation.z += delta * 0.2 * rotationSpeed;
  });
</script>

<T.PerspectiveCamera makeDefault position={[0, 2.5, 6]} fov={45} />

<!-- Ambient light for base visibility -->
<T.AmbientLight intensity={0.4} />

<!-- Main Key Light -->
<T.DirectionalLight
  position={[5, 10, 5]}
  intensity={lightIntensity * 0.1}
  color={lightColor}
  castShadow
/>

<!-- Fill Light -->
<T.PointLight
  position={[-5, 5, 2]}
  intensity={lightIntensity * 0.5}
  color="#8B5CF6"
/>

<!-- Back Light -->
<T.PointLight
  position={[0, 2, -5]}
  intensity={lightIntensity * 0.3}
  color="#159FE8"
/>

<ThrelteGrid
  sectionSize={1}
  sectionThickness={1.5}
  cellSize={0.5}
  cellThickness={0.5}
  infiniteGrid
  fadeDistance={25}
  cellColor="#1C2638"
  sectionColor="#19E6D2"
  opacity={0.3}
/>

<!-- Using {#key shape} forces a complete re-mount of the component branch when shape changes.
     This is the most reliable way to handle geometry swaps in Three.js to avoid reference ghosting. -->
{#key shape}
  <Float speed={2} rotationIntensity={0.5} floatIntensity={0.5}>
    <T.Mesh bind:ref={meshRef} position={[0, 1.2, 0]} castShadow receiveShadow>
      {#if shape === 'box'}
        <T.BoxGeometry args={[1.5, 1.5, 1.5]} />
      {:else if shape === 'sphere'}
        <T.SphereGeometry args={[1.1, 64, 64]} />
      {:else if shape === 'torus'}
        <T.TorusGeometry args={[1, 0.4, 32, 100]} />
      {:else if shape === 'cylinder'}
        <T.CylinderGeometry args={[0.8, 0.8, 1.8, 64]} />
      {:else if shape === 'plane'}
        <T.PlaneGeometry args={[2, 2]} />
      {/if}

      <T.MeshStandardMaterial
        {color}
        {emissive}
        {metalness}
        {roughness}
        {wireframe}
      />
    </T.Mesh>
  </Float>
{/key}

<ContactShadows
  opacity={0.6}
  scale={10}
  blur={2.5}
  far={4.5}
  position={[0, 0, 0]}
/>
