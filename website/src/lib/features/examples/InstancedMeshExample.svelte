<script lang="ts">
  import { Canvas, T, useTask } from '@threlte/core';
  import * as THREE from 'three';
  import GlassPanel from '../../components/GlassPanel.svelte';
  import GradientText from '../../components/GradientText.svelte';

  let count = 40;
  let meshRef = $state<THREE.InstancedMesh | undefined>(undefined);

  const tempObject = new THREE.Object3D();
  const tempColor = new THREE.Color();

  // Create grid of positions
  const instances = $derived.by(() => {
    const data = [];
    const size = Math.sqrt(count);
    for (let i = 0; i < count; i++) {
      data.push({
        position: [
          (i % size - size / 2) * 2,
          (Math.floor(i / size) - size / 2) * 2,
          Math.sin(i * 0.5) * 2
        ],
        color: i % 2 === 0 ? '#19E6D2' : '#8B5CF6'
      });
    }
    return data;
  });

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

<GlassPanel class="p-6 rounded-2xl bg-[#0A0E17] border border-[#1C2638] flex flex-col gap-6 overflow-hidden">
  <div class="flex justify-between items-start border-b border-[#1C2638] pb-4">
    <div>
      <h3 class="text-xl font-bold text-[#e1e2ec]">GPU Instanced <GradientText>Wave Field</GradientText></h3>
      <p class="text-xs text-[#6F7A90]">Massive instance rendering with zero overhead on the main thread</p>
    </div>
    <div class="px-2 py-1 bg-[#19E6D2]/10 rounded border border-[#19E6D2]/30 text-[10px] text-[#19E6D2] font-mono">
      INSTANCES: {count}
    </div>
  </div>

  <div class="h-96 relative bg-[#05070D] rounded-xl overflow-hidden border border-[#1C2638]">
    <Canvas>
      <T.PerspectiveCamera makeDefault position={[0, 0, 12]} fov={50} />
      <T.AmbientLight intensity={0.5} />
      <T.DirectionalLight position={[10, 10, 10]} intensity={2} />

      <T.InstancedMesh bind:ref={meshRef} args={[undefined, undefined, count]}>
        <T.BoxGeometry args={[0.8, 0.8, 0.8]} />
        <T.MeshStandardMaterial color="#19E6D2" metalness={0.8} roughness={0.1} />
      </T.InstancedMesh>
    </Canvas>

    <div class="absolute bottom-4 right-4 flex flex-col items-end gap-2">
       <div class="bg-[#0A0E17]/90 p-3 rounded-lg border border-[#1C2638] text-[10px] font-mono max-w-[200px]">
          <span class="text-[#6F7A90]">// Spatial Instancing API</span><br/>
          <span class="text-[#8B5CF6]">InstancedElement</span>(<br/>
          &nbsp;&nbsp;count = <span class="text-[#19E6D2]">{count}</span>,<br/>
          &nbsp;&nbsp;mesh = <span class="text-[#159FE8]">Mesh.Cube</span><br/>
          )
       </div>
    </div>
  </div>
</GlassPanel>
