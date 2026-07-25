<script lang="ts">
  import { T, useTask } from '@threlte/core';
  import * as THREE from 'three';

  interface Props {
    rotationSpeed?: number;
  }

  let { rotationSpeed = 1 }: Props = $props();

  let boxRef = $state<THREE.Mesh | undefined>(undefined);

  useTask((delta) => {
    if (!boxRef) return;
    boxRef.rotation.y += delta * 1.5 * rotationSpeed;
    boxRef.rotation.x += delta * 0.8 * rotationSpeed;
  });
</script>

<T.PerspectiveCamera makeDefault position={[0, 0, 3.5]} fov={60} />

<T.AmbientLight intensity={0.7} />
<T.PointLight position={[2, 2, 2]} intensity={10} color="#19E6D2" />
<T.PointLight position={[-2, -2, 2]} intensity={8} color="#F25933" />

<T.Mesh bind:ref={boxRef}>
  <T.BoxGeometry args={[1.2, 1.2, 1.2]} />
  <T.MeshStandardMaterial color="#0A0E17" roughness={0.2} metalness={0.8} />
  
  <T.LineSegments>
    <T.EdgesGeometry args={[new THREE.BoxGeometry(1.2, 1.2, 1.2)]} />
    <T.LineBasicMaterial color="#19E6D2" linewidth={2} />
  </T.LineSegments>
</T.Mesh>
