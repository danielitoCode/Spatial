<script lang="ts">
  import { Canvas, T, useTask } from '@threlte/core';
  import * as THREE from 'three';
  import GlassPanel from '../../components/GlassPanel.svelte';

  let particleCount = $state(200);
  let speed = $state(1);

  let pointsRef = $state<THREE.Points | undefined>(undefined);

  // Generate particle positions
  const positions = $derived.by(() => {
    const arr = new Float32Array(particleCount * 3);
    for (let i = 0; i < particleCount * 3; i++) {
      arr[i] = (Math.random() - 0.5) * 8;
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
    pointsRef.rotation.y += delta * 0.15 * speed;
    pointsRef.rotation.x += delta * 0.08 * speed;
  });
</script>

<GlassPanel class="p-6 rounded-2xl bg-[#0A0E17] border border-[#1C2638] flex flex-col gap-6">
  <div class="border-b border-[#1C2638] pb-4">
    <h3 class="text-xl font-bold text-[#e1e2ec]">Particle Constellation Field</h3>
    <p class="text-xs text-[#6F7A90]">High-performance GPU instanced particle systems for Android UI backgrounds</p>
  </div>

  <div class="grid lg:grid-cols-12 gap-6">
    <div class="lg:col-span-8 h-80 relative bg-[#05070D] rounded-xl overflow-hidden border border-[#1C2638]">
      <Canvas>
        <T.PerspectiveCamera makeDefault position={[0, 0, 5]} fov={60} />
        <T.Points bind:ref={pointsRef} {geometry}>
          <T.PointsMaterial size={0.06} color="#19E6D2" transparent opacity={0.8} />
        </T.Points>
      </Canvas>

      <div class="absolute top-3 right-3 bg-[#0A0E17]/80 px-2.5 py-1 rounded text-[10px] font-mono text-[#8B5CF6]">
        PARTICLES: {particleCount}
      </div>
    </div>

    <div class="lg:col-span-4 flex flex-col justify-center gap-4 bg-[#101624] p-5 rounded-xl border border-[#1C2638] text-xs">
      <div>
        <div class="flex justify-between text-[#e1e2ec] font-mono mb-1">
          <span>Particle Count</span>
          <span>{particleCount}</span>
        </div>
        <input type="range" min="50" max="500" step="25" bind:value={particleCount} class="w-full accent-[#19E6D2]" />
      </div>

      <div>
        <div class="flex justify-between text-[#e1e2ec] font-mono mb-1">
          <span>Swirm Speed</span>
          <span>{speed.toFixed(1)}x</span>
        </div>
        <input type="range" min="0.2" max="3" step="0.2" bind:value={speed} class="w-full accent-[#19E6D2]" />
      </div>

      <div class="p-3 bg-[#0A0E17] rounded-lg border border-[#1C2638] font-mono text-[11px] text-[#6F7A90] mt-2">
        <span class="text-[#19E6D2]">SpatialParticles</span>(<br />
        &nbsp;&nbsp;count = {particleCount},<br />
        &nbsp;&nbsp;speed = {speed.toFixed(1)}f<br />
        )
      </div>
    </div>
  </div>
</GlassPanel>
