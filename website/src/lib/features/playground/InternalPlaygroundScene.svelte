<script lang="ts">
  import { T, useTask } from '@threlte/core';
  import { Grid as ThrelteGrid, ContactShadows, Float } from '@threlte/extras';
  import * as THREE from 'three';

  interface Props {
    shape: 'box' | 'sphere' | 'torus' | 'cylinder';
    color: string;
    emissive: string;
    metalness: number;
    roughness: number;
    wireframe: boolean;
    lightIntensity: number;
    lightColor: string;
    autoRotate: boolean;
    rotationSpeed: number;
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
    rotationSpeed
  }: Props = $props();

  let meshRef = $state<THREE.Mesh | undefined>(undefined);

  useTask((delta) => {
    if (!meshRef || !autoRotate) return;
    meshRef.rotation.y += delta * 0.5 * rotationSpeed;
    meshRef.rotation.z += delta * 0.2 * rotationSpeed;
  });
</script>

<T.PerspectiveCamera makeDefault position={[0, 2, 5]} fov={45} />

<T.AmbientLight intensity={0.2} />
<T.PointLight position={[5, 5, 5]} intensity={lightIntensity} color={lightColor} />
<T.PointLight position={[-5, 5, 2]} intensity={lightIntensity * 0.5} color="#8B5CF6" />
<T.SpotLight position={[0, 10, 0]} intensity={2} penumbra={1} castShadow />

<ThrelteGrid
  sectionSize={1}
  sectionThickness={1}
  cellSize={0.5}
  cellThickness={0.5}
  infiniteGrid
  fadeDistance={15}
  cellColor="#1C2638"
  sectionColor="#19E6D2"
  opacity={0.2}
/>

<Float speed={2} rotationIntensity={0.5} floatIntensity={0.5}>
  <T.Mesh bind:ref={meshRef} position={[0, 1, 0]} castShadow>
    {#if shape === 'box'}
      <T.BoxGeometry args={[1.5, 1.5, 1.5]} />
    {:else if shape === 'sphere'}
      <T.SphereGeometry args={[1.1, 64, 64]} />
    {:else if shape === 'torus'}
      <T.TorusGeometry args={[1, 0.4, 32, 100]} />
    {:else if shape === 'cylinder'}
      <T.CylinderGeometry args={[0.8, 0.8, 1.8, 64]} />
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

<ContactShadows
  opacity={0.4}
  scale={10}
  blur={2}
  far={4.5}
/>
