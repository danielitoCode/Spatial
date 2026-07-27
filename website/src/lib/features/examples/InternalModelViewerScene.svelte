<script lang="ts">
  import { T, useTask } from '@threlte/core';
  import { ContactShadows, Float, Grid as ThrelteGrid } from '@threlte/extras';
  import * as THREE from 'three';

  interface Props {
    color: string;
    metalness: number;
    roughness: number;
    wireframe: boolean;
    autoRotate: boolean;
  }

  let { color, metalness, roughness, wireframe, autoRotate }: Props = $props();

  let meshRef = $state<THREE.Mesh | undefined>(undefined);

  useTask((delta) => {
    if (!meshRef || !autoRotate) return;
    meshRef.rotation.y += delta * 0.6;
    meshRef.rotation.x += delta * 0.3;
  });
</script>

<T.PerspectiveCamera makeDefault position={[0, 2, 5]} fov={50} />
<T.AmbientLight intensity={0.4} />
<T.PointLight position={[5, 5, 5]} intensity={15} color="#19E6D2" />
<T.PointLight position={[-5, 5, 2]} intensity={10} color="#8B5CF6" />
<T.DirectionalLight position={[0, 5, -5]} intensity={2} color="#159FE8" />

<ThrelteGrid
  sectionSize={1}
  sectionThickness={1.5}
  cellSize={0.5}
  infiniteGrid
  fadeDistance={20}
  cellColor="#1C2638"
  sectionColor="#19E6D2"
  opacity={0.15}
/>

<Float speed={1.5} rotationIntensity={0.4} floatIntensity={0.4}>
  <T.Mesh bind:ref={meshRef} position={[0, 1, 0]}>
    <T.TorusKnotGeometry args={[0.9, 0.3, 128, 32]} />
    <T.MeshStandardMaterial
      {color}
      {metalness}
      {roughness}
      {wireframe}
      emissive="#000000"
    />
  </T.Mesh>
</Float>

<ContactShadows opacity={0.5} scale={10} blur={2.5} far={4} />
