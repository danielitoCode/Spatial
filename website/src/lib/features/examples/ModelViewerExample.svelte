<script lang="ts">
  import { Canvas, T, useTask } from '@threlte/core';
  import * as THREE from 'three';
  import GlassPanel from '../../components/GlassPanel.svelte';
  import SpatialButton from '../../components/SpatialButton.svelte';

  let activeTab = $state<'3d' | 'code'>('3d');
  let metalness = $state(0.7);
  let roughness = $state(0.2);
  let wireframe = $state(false);
  let color = $state('#19E6D2');
  let autoRotate = $state(true);

  let meshRef = $state<THREE.Mesh | undefined>(undefined);

  useTask((delta) => {
    if (!meshRef || !autoRotate) return;
    meshRef.rotation.y += delta * 0.8;
    meshRef.rotation.x += delta * 0.4;
  });

  const generatedKotlinCode = $derived(
`SpatialScene {
    OrbitalCamera(distance = 4.0f)

    PointLight(
        color = Color(0xFF19E6D2),
        intensity = 10f,
        position = Vector3(2f, 2f, 2f)
    )

    SpatialNode(
        shape = Shape.TorusKnot(radius = 0.9f, tube = 0.3f),
        material = Material.PBR(
            color = Color(parseColor("${color}")),
            metalness = ${metalness.toFixed(2)}f,
            roughness = ${roughness.toFixed(2)}f,
            wireframe = ${wireframe}
        )
    )
}`
  );
</script>

<GlassPanel class="p-6 rounded-2xl bg-[#0A0E17] border border-[#1C2638] flex flex-col gap-6">
  <div class="flex flex-wrap justify-between items-center gap-4 border-b border-[#1C2638] pb-4">
    <div>
      <h3 class="text-xl font-bold text-[#e1e2ec]">3D PBR Model Viewer</h3>
      <p class="text-xs text-[#6F7A90]">Physically Based Rendering with dynamic materials and state binding</p>
    </div>

    <!-- Tab Switcher -->
    <div class="flex bg-[#101624] p-1 rounded-lg border border-[#1C2638]">
      <button
        onclick={() => (activeTab = '3d')}
        class="px-3 py-1 text-xs font-semibold rounded-md transition-colors {activeTab === '3d'
          ? 'bg-[#19E6D2] text-[#00201c]'
          : 'text-[#bacac6] hover:text-white'}"
      >
        3D Viewport
      </button>
      <button
        onclick={() => (activeTab = 'code')}
        class="px-3 py-1 text-xs font-semibold rounded-md transition-colors {activeTab === 'code'
          ? 'bg-[#19E6D2] text-[#00201c]'
          : 'text-[#bacac6] hover:text-white'}"
      >
        Kotlin Compose Code
      </button>
    </div>
  </div>

  {#if activeTab === '3d'}
    <div class="grid lg:grid-cols-12 gap-6">
      <!-- 3D Canvas Viewport -->
      <div class="lg:col-span-8 h-80 relative bg-[#05070D] rounded-xl overflow-hidden border border-[#1C2638]">
        <Canvas>
          <T.PerspectiveCamera makeDefault position={[0, 0, 4]} fov={60} />
          <T.AmbientLight intensity={0.6} />
          <T.PointLight position={[3, 3, 3]} intensity={12} color="#19E6D2" />
          <T.PointLight position={[-3, -3, 3]} intensity={8} color="#8B5CF6" />

          <T.Mesh bind:ref={meshRef}>
            <T.TorusKnotGeometry args={[0.85, 0.28, 100, 16]} />
            <T.MeshStandardMaterial
              {color}
              {metalness}
              {roughness}
              {wireframe}
            />
          </T.Mesh>
        </Canvas>

        <div class="absolute bottom-3 left-3 bg-[#0A0E17]/80 px-2.5 py-1 rounded text-[10px] font-mono text-[#19E6D2]">
          SHADER: PBR_STANDARD_v1.2
        </div>
      </div>

      <!-- Controls Panel -->
      <div class="lg:col-span-4 flex flex-col justify-center gap-4 bg-[#101624] p-5 rounded-xl border border-[#1C2638] text-xs">
        <div>
          <span class="block text-[#6F7A90] font-mono mb-1">Color Motif</span>
          <div class="flex gap-2">
            {#each ['#19E6D2', '#159FE8', '#8B5CF6', '#F25933', '#46fbe7'] as c}
              <button
                onclick={() => (color = c)}
                aria-label="Select color {c}"
                class="w-6 h-6 rounded-full border border-white/20 transition-transform hover:scale-110 {color === c ? 'ring-2 ring-white scale-110' : ''}"
                style="background-color: {c}"
              ></button>
            {/each}
          </div>
        </div>

        <div>
          <div class="flex justify-between text-[#e1e2ec] font-mono mb-1">
            <span>Metalness</span>
            <span>{metalness.toFixed(2)}</span>
          </div>
          <input type="range" min="0" max="1" step="0.05" bind:value={metalness} class="w-full accent-[#19E6D2]" />
        </div>

        <div>
          <div class="flex justify-between text-[#e1e2ec] font-mono mb-1">
            <span>Roughness</span>
            <span>{roughness.toFixed(2)}</span>
          </div>
          <input type="range" min="0" max="1" step="0.05" bind:value={roughness} class="w-full accent-[#19E6D2]" />
        </div>

        <div class="flex justify-between items-center pt-2 border-t border-[#1C2638]">
          <span class="text-[#e1e2ec] font-mono">Wireframe</span>
          <input type="checkbox" bind:checked={wireframe} class="accent-[#19E6D2] w-4 h-4 cursor-pointer" />
        </div>

        <div class="flex justify-between items-center">
          <span class="text-[#e1e2ec] font-mono">Auto Rotate</span>
          <input type="checkbox" bind:checked={autoRotate} class="accent-[#19E6D2] w-4 h-4 cursor-pointer" />
        </div>
      </div>
    </div>
  {:else}
    <div class="bg-[#05070D] p-5 rounded-xl border border-[#1C2638] font-mono text-xs text-[#e1e2ec] overflow-x-auto">
      <pre><code>{generatedKotlinCode}</code></pre>
    </div>
  {/if}
</GlassPanel>
