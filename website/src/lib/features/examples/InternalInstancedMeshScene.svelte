<script lang="ts">
  import { T, useTask } from '@threlte/core';
  import * as THREE from 'three';

  interface Props {
    count: number;
  }

  let { count }: Props = $props();

  let meshRef = $state<THREE.InstancedMesh | undefined>(undefined);
  const tempObject = new THREE.Object3D();

  useTask((delta) => {
    if (!meshRef) return;
    const time = Date.now() * 0.001;
    const size = Math.sqrt(count);

    for (let i = 0; i < count; i++) {
      const x = i % size - size / 2;
      const y = Math.floor(i / size) - size / 2;

      const wave = Math.sin(x * 0.5 + time) * Math.cos(y * 0.5 + time);

      tempObject.position.set(x * 1.5, y * 1.5, wave * 2);
      tempObject.rotation.set(time * 0.5, time * 0.3, 0);
      tempObject.updateMatrix();
      meshRef.setMatrixAt(i, tempObject.matrix);
    }
    meshRef.instanceMatrix.needsUpdate = true;
  });
</script>

<T.PerspectiveCamera makeDefault position={[0, 0, 12]} fov={50} />
<T.AmbientLight intensity={0.5} />
<T.DirectionalLight position={[10, 10, 10]} intensity={2} />

<T.InstancedMesh bind:ref={meshRef} args={[undefined, undefined, count]}>
  <T.BoxGeometry args={[0.8, 0.8, 0.8]} />
  <T.MeshStandardMaterial color="#19E6D2" metalness={0.8} roughness={0.1} />
</T.InstancedMesh>
