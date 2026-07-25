<script lang="ts">
  import { T, useTask } from '@threlte/core';
  import * as THREE from 'three';

  let spatialGroupRef = $state<THREE.Group | undefined>(undefined);
  let sphereRef = $state<THREE.Mesh | undefined>(undefined);

  // Mouse interaction state
  let mouseX = $state(0);
  let mouseY = $state(0);
  let targetRotationX = $state(0);
  let targetRotationY = $state(0);

  function handleMouseMove(e: MouseEvent) {
    mouseX = (e.clientX / window.innerWidth) * 2 - 1;
    mouseY = -(e.clientY / window.innerHeight) * 2 + 1;
  }

  if (typeof window !== 'undefined') {
    window.addEventListener('mousemove', handleMouseMove);
  }

  // Create hexagonal shape points
  const hexPoints: THREE.Vector3[] = [];
  for (let i = 0; i < 6; i++) {
    const angle = (i / 6) * Math.PI * 2;
    hexPoints.push(new THREE.Vector3(Math.cos(angle) * 3, Math.sin(angle) * 3, 0));
  }
  const hexGeometry = new THREE.BufferGeometry().setFromPoints(hexPoints);

  // Animation task loop using Threlte's useTask
  useTask((delta) => {
    if (!spatialGroupRef) return;

    const time = performance.now() * 0.001;

    // Slow auto rotation
    spatialGroupRef.rotation.y += delta * 0.2;
    spatialGroupRef.rotation.x += delta * 0.08;

    // Follow mouse lerp
    targetRotationY = mouseX * 0.5;
    targetRotationX = -mouseY * 0.5;

    spatialGroupRef.rotation.y += (targetRotationY - spatialGroupRef.rotation.y) * 0.05;
    spatialGroupRef.rotation.x += (targetRotationX - spatialGroupRef.rotation.x) * 0.05;

    // Sphere orbit
    if (sphereRef) {
      const orbitRadius = 2.2;
      sphereRef.position.x = Math.cos(time * 1.2) * orbitRadius;
      sphereRef.position.z = Math.sin(time * 1.2) * orbitRadius;
      sphereRef.position.y = Math.sin(time * 0.6) * 0.4;
    }
  });
</script>

<!-- Camera -->
<T.PerspectiveCamera makeDefault position={[0, 0, 5]} fov={75} />

<!-- Lights -->
<T.AmbientLight intensity={0.6} color="#ffffff" />
<T.PointLight position={[3, 3, 3]} intensity={12} color="#19E6D2" distance={15} />
<T.PointLight position={[-3, -3, 3]} intensity={12} color="#8B5CF6" distance={15} />

<!-- Main 3D Spatial Group -->
<T.Group bind:ref={spatialGroupRef}>
  <!-- Central Cube -->
  <T.Mesh>
    <T.BoxGeometry args={[1.3, 1.3, 1.3]} />
    <T.MeshPhongMaterial
      color="#11131a"
      emissive="#159FE8"
      emissiveIntensity={0.25}
      shininess={100}
      transparent
      opacity={0.9}
    />
    <!-- Wireframe overlay -->
    <T.LineSegments>
      <T.EdgesGeometry args={[new THREE.BoxGeometry(1.3, 1.3, 1.3)]} />
      <T.LineBasicMaterial color="#19E6D2" linewidth={2} />
    </T.LineSegments>
  </T.Mesh>

  <!-- Orbital Ring (Torus) -->
  <T.Mesh rotation.x={Math.PI / 2}>
    <T.TorusGeometry args={[2.2, 0.025, 16, 100]} />
    <T.MeshBasicMaterial color="#159FE8" />
  </T.Mesh>

  <!-- Hexagonal Frame -->
  <T.LineLoop geometry={hexGeometry}>
    <T.LineBasicMaterial color="#8B5CF6" transparent opacity={0.45} />
  </T.LineLoop>
</T.Group>

<!-- Orbiting Sphere -->
<T.Mesh bind:ref={sphereRef}>
  <T.SphereGeometry args={[0.16, 32, 32]} />
  <T.MeshBasicMaterial color="#8B5CF6" />
</T.Mesh>
