<script lang="ts">
  import { T, useTask } from '@threlte/core';
  import { Float, ContactShadows, Grid } from '@threlte/extras';
  import * as THREE from 'three';

  let rotation = $state(0);
  useTask((delta) => {
    rotation += delta * 0.5;
  });

  // Create hexagonal shape points (The "Icon" shape)
  const hexPoints: THREE.Vector3[] = [];
  for (let i = 0; i < 6; i++) {
    const angle = (i / 6) * Math.PI * 2;
    hexPoints.push(new THREE.Vector3(Math.cos(angle) * 2.2, Math.sin(angle) * 2.2, 0));
  }
  const hexGeometry = new THREE.BufferGeometry().setFromPoints(hexPoints);
</script>

<T.PerspectiveCamera makeDefault position={[5, 5, 8]} fov={35} on:create={({ ref }) => ref.lookAt(0, 0, 0)} />

<T.AmbientLight intensity={0.5} />
<T.DirectionalLight position={[10, 10, 5]} intensity={2} color="#19E6D2" castShadow />
<T.PointLight position={[-5, 5, 5]} intensity={1} color="#8B5CF6" />

<Grid
  sectionSize={1}
  sectionThickness={1.5}
  cellSize={0.5}
  infiniteGrid
  cellColor="#1C2638"
  sectionColor="#19E6D2"
  opacity={0.2}
/>

<Float speed={2.5} rotationIntensity={0.5} floatIntensity={0.8}>
  <T.Group rotation.y={rotation}>
    <!-- Central Architecture Stack (The 3 layers) -->
    <T.Mesh position={[0, 1, 0]}>
        <T.BoxGeometry args={[1.8, 0.1, 1.8]} />
        <T.MeshStandardMaterial color="#19E6D2" transparent opacity={0.9} metalness={0.8} />
    </T.Mesh>

    <T.Mesh position={[0, 0, 0]}>
        <T.BoxGeometry args={[2.2, 0.1, 2.2]} />
        <T.MeshStandardMaterial color="#159FE8" transparent opacity={0.7} metalness={0.5} />
    </T.Mesh>

    <T.Mesh position={[0, -1, 0]}>
        <T.BoxGeometry args={[2.6, 0.1, 2.6]} />
        <T.MeshStandardMaterial color="#8B5CF6" transparent opacity={0.6} metalness={0.3} />
    </T.Mesh>

    <!-- The Icon Outer Frame -->
    <T.LineLoop geometry={hexGeometry}>
        <T.LineBasicMaterial color="#19E6D2" transparent opacity={0.5} linewidth={2} />
    </T.LineLoop>

    <T.Mesh rotation.x={Math.PI / 2}>
        <T.TorusGeometry args={[2.2, 0.015, 16, 100]} />
        <T.MeshBasicMaterial color="#159FE8" transparent opacity={0.3} />
    </T.Mesh>
  </T.Group>
</Float>

<ContactShadows opacity={0.5} scale={15} blur={3} far={6} position={[0, -2.5, 0]} />
