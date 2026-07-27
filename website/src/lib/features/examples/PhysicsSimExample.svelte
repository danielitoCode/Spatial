<script lang="ts">
  import { Canvas, T, useTask } from '@threlte/core';
  import * as THREE from 'three';
  import GlassPanel from '../../components/GlassPanel.svelte';

  let bounceSpeed = $state(1.5);
  let cubeCount = $state(3);

  let cubeGroupRef = $state<THREE.Group | undefined>(undefined);

  /*useTask(() => {
    if (!cubeGroupRef) return;
    const time = performance.now() * 0.001 * bounceSpeed;

    cubeGroupRef.children.forEach((child, index) => {
      child.position.y = Math.abs(Math.sin(time + index * 0.8)) * 1.5 - 0.75;
      child.rotation.x = time + index;
      child.rotation.y = time * 0.5;
    });
  });*/

  const colors = ['#19E6D2', '#159FE8', '#8B5CF6', '#F25933', '#46fbe7'];
</script>

<GlassPanel class="p-6 rounded-2xl bg-[#0A0E17] border border-[#1C2638] flex flex-col gap-6">
  <div class="border-b border-[#1C2638] pb-4">
    <h3 class="text-xl font-bold text-[#e1e2ec]">3D Physics & Collision Simulator</h3>
    <p class="text-xs text-[#6F7A90]">Rigid body dynamics optimized for low-latency mobile rendering</p>
  </div>

  <div class="grid lg:grid-cols-12 gap-6">
    <div class="lg:col-span-8 h-80 relative bg-[#05070D] rounded-xl overflow-hidden border border-[#1C2638]">
      <Canvas>
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
      </Canvas>
    </div>

    <div class="lg:col-span-4 flex flex-col justify-center gap-4 bg-[#101624] p-5 rounded-xl border border-[#1C2638] text-xs">
      <div>
        <div class="flex justify-between text-[#e1e2ec] font-mono mb-1">
          <span>Bounce Rate</span>
          <span>{bounceSpeed.toFixed(1)}x</span>
        </div>
        <input type="range" min="0.5" max="3" step="0.25" bind:value={bounceSpeed} class="w-full accent-[#19E6D2]" />
      </div>

      <div>
        <div class="flex justify-between text-[#e1e2ec] font-mono mb-1">
          <span>Active Bodies</span>
          <span>{cubeCount}</span>
        </div>
        <input type="range" min="1" max="5" step="1" bind:value={cubeCount} class="w-full accent-[#19E6D2]" />
      </div>

      <div class="p-3 bg-[#0A0E17] rounded-lg border border-[#1C2638] font-mono text-[11px] text-[#6F7A90] mt-2">
        <span class="text-[#8B5CF6]">SpatialPhysicsWorld</span> {'{'}<br />
        &nbsp;&nbsp;gravity = Vector3(0f, -9.8f, 0f)<br />
        &nbsp;&nbsp;restitution = 0.85f<br />
        {'}'}
      </div>
    </div>
  </div>
</GlassPanel>
