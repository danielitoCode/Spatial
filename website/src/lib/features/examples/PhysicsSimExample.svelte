<script lang="ts">
  import { Canvas } from '@threlte/core';
  import InternalPhysicsSimScene from './InternalPhysicsSimScene.svelte';
  import GlassPanel from '../../components/GlassPanel.svelte';

  let bounceSpeed = $state(1.5);
  let cubeCount = $state(3);
</script>

<GlassPanel class="p-6 rounded-2xl bg-[#0A0E17] border border-[#1C2638] flex flex-col gap-6">
  <div class="border-b border-[#1C2638] pb-4">
    <h3 class="text-xl font-bold text-[#e1e2ec]">3D Physics & Collision Simulator</h3>
    <p class="text-xs text-[#6F7A90]">Rigid body dynamics optimized for low-latency mobile rendering</p>
  </div>

  <div class="grid lg:grid-cols-12 gap-6">
    <div class="lg:col-span-8 h-80 relative bg-[#05070D] rounded-xl overflow-hidden border border-[#1C2638]">
      <Canvas>
        <InternalPhysicsSimScene {bounceSpeed} {cubeCount} />
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
