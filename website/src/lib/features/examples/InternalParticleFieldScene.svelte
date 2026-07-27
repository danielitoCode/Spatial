<script lang="ts">
  import { T, useTask } from '@threlte/core';
  import * as THREE from 'three';

  interface Props {
    particleCount: number;
    speed: number;
  }

  let { particleCount, speed }: Props = $props();

  let pointsRef = $state<THREE.Points | undefined>(undefined);

  // Generate particle positions
  const positions = $derived.by(() => {
    const arr = new Float32Array(particleCount * 3);
    for (let i = 0; i < particleCount * 3; i++) {
      const r = 2 + Math.random() * 4;
      const theta = Math.random() * Math.PI * 2;
      const phi = Math.random() * Math.PI;

      arr[i * 3 + 0] = r * Math.sin(phi) * Math.cos(theta);
      arr[i * 3 + 1] = r * Math.sin(phi) * Math.sin(theta);
      arr[i * 3 + 2] = r * Math.cos(phi);
    }
    return arr;
  });

  const geometry = $derived.by(() => {
    const geo = new THREE.BufferGeometry();
    geo.setAttribute('position', new THREE.BufferAttribute(positions, 3));
    return geo;
  });

  useTask((delta) => {
    if (!pointsRef) return;
    pointsRef.rotation.y += delta * 0.1 * speed;
    pointsRef.rotation.z += delta * 0.05 * speed;
  });
</script>

<T.PerspectiveCamera makeDefault position={[0, 0, 8]} fov={60} />
<T.Points bind:ref={pointsRef} {geometry}>
  <T.PointsMaterial
    size={0.08}
    color="#19E6D2"
    transparent
    opacity={0.8}
    sizeAttenuation={true}
  />
</T.Points>

<T.Mesh rotation.y={Date.now() * 0.001}>
    <T.IcosahedronGeometry args={[1, 1]} />
    <T.MeshBasicMaterial color="#8B5CF6" wireframe />
</T.Mesh>
