<script lang="ts">
  import { T, useTask } from '@threlte/core';
  import * as THREE from 'three';

  interface Props {
    bounceSpeed: number;
    cubeCount: number;
  }

  let { bounceSpeed, cubeCount }: Props = $props();

  let cubeGroupRef = $state<THREE.Group | undefined>(undefined);
  const colors = ['#19E6D2', '#159FE8', '#8B5CF6', '#F25933', '#46fbe7'];

  useTask(() => {
    if (!cubeGroupRef) return;
    const time = performance.now() * 0.001 * bounceSpeed;

    cubeGroupRef.children.forEach((child, index) => {
      child.position.y = Math.abs(Math.sin(time + index * 0.8)) * 1.5 - 0.75;
      child.rotation.x = time + index;
      child.rotation.y = time * 0.5;
    });
  });
</script>

<T.PerspectiveCamera makeDefault position={[0, 0, 5]} fov={60} />
<T.AmbientLight intensity={0.7} />
<T.PointLight position={[2, 4, 2]} intensity={10} color="#19E6D2" />

<T.Group bind:ref={cubeGroupRef}>
  {#each Array(cubeCount) as _, i}
    <T.Mesh position={[(i - (cubeCount - 1) / 2) * 1.5, 0, 0]}>
      <T.BoxGeometry args={[0.9, 0.9, 0.9]} />
      <T.MeshStandardMaterial color={colors[i % colors.length]} roughness={0.2} metalness={0.7} />
    </T.Mesh>
  {/each}
</T.Group>

<!-- Floor plane -->
<T.Mesh position={[0, -1.3, 0]} rotation.x={-Math.PI / 2}>
  <T.PlaneGeometry args={[10, 10]} />
  <T.MeshStandardMaterial color="#101624" roughness={0.8} />
</T.Mesh>
