<script lang="ts">
  interface Props {
    shape: 'box' | 'sphere' | 'torus' | 'cylinder';
    color: string;
    emissive: string;
    metalness: number;
    roughness: number;
    wireframe: boolean;
    lightIntensity: number;
    lightColor: string;
    autoRotate: boolean;
    onApplyPreset: (preset: string) => void;
  }

  let {
    shape = $bindable('box'),
    color = $bindable('#19E6D2'),
    emissive = $bindable('#000000'),
    metalness = $bindable(0.6),
    roughness = $bindable(0.2),
    wireframe = $bindable(false),
    lightIntensity = $bindable(10),
    lightColor = $bindable('#19E6D2'),
    autoRotate = $bindable(true),
    onApplyPreset
  }: Props = $props();

  const presets = [
    { name: 'Cyberpunk Neon', shape: 'torus', color: '#19E6D2', emissive: '#8B5CF6', metalness: 0.8, roughness: 0.1 },
    { name: 'Chrome Metallic', shape: 'sphere', color: '#e1e2ec', emissive: '#000000', metalness: 0.95, roughness: 0.05 },
    { name: 'Space Emerald', shape: 'box', color: '#10B981', emissive: '#064E3B', metalness: 0.7, roughness: 0.3 },
    { name: 'Solar Flame', shape: 'cylinder', color: '#F25933', emissive: '#7C2D12', metalness: 0.4, roughness: 0.4 }
  ];

  function applyPreset(p: typeof presets[0]) {
    shape = p.shape as any;
    color = p.color;
    emissive = p.emissive;
    metalness = p.metalness;
    roughness = p.roughness;
  }
</script>

<div class="bg-[#0A0E17] p-6 rounded-2xl border border-[#1C2638] flex flex-col gap-6 text-xs text-[#e1e2ec]">
  <!-- Presets Bar -->
  <div>
    <span class="block text-[#6F7A90] font-mono mb-2">QUICK PRESETS</span>
    <div class="grid grid-cols-2 gap-2">
      {#each presets as p}
        <button
          onclick={() => applyPreset(p)}
          class="px-3 py-2 bg-[#101624] hover:bg-[#19E6D2]/10 border border-[#1C2638] hover:border-[#19E6D2]/40 rounded-lg text-left font-semibold transition-colors"
        >
          {p.name}
        </button>
      {/each}
    </div>
  </div>

  <!-- Shape Selector -->
  <div>
    <span class="block text-[#6F7A90] font-mono mb-2">3D GEOMETRY</span>
    <div class="grid grid-cols-4 gap-2">
      {#each ['box', 'sphere', 'torus', 'cylinder'] as s}
        <button
          onclick={() => (shape = s as any)}
          class="py-2 rounded-lg font-mono capitalize border transition-all {shape === s
            ? 'bg-[#19E6D2] text-[#00201c] font-bold border-[#19E6D2]'
            : 'bg-[#101624] text-[#bacac6] border-[#1C2638] hover:text-white'}"
        >
          {s}
        </button>
      {/each}
    </div>
  </div>

  <!-- Material Pickers -->
  <div class="space-y-4 pt-2 border-t border-[#1C2638]">
    <span class="block text-[#6F7A90] font-mono">MATERIAL & SHADING</span>

    <div>
      <div class="flex justify-between font-mono mb-1">
        <span>Primary Color</span>
        <span class="text-[#19E6D2]">{color}</span>
      </div>
      <div class="flex gap-2 items-center">
        <input type="color" bind:value={color} class="w-8 h-8 rounded bg-transparent cursor-pointer border-0" />
        <div class="flex gap-1.5">
          {#each ['#19E6D2', '#159FE8', '#8B5CF6', '#F25933', '#E1E2EC'] as c}
            <button
              onclick={() => (color = c)}
              aria-label="Set color {c}"
              class="w-5 h-5 rounded-full border border-white/20"
              style="background-color: {c}"
            ></button>
          {/each}
        </div>
      </div>
    </div>

    <div>
      <div class="flex justify-between font-mono mb-1">
        <span>Metalness</span>
        <span>{metalness.toFixed(2)}</span>
      </div>
      <input type="range" min="0" max="1" step="0.05" bind:value={metalness} class="w-full accent-[#19E6D2]" />
    </div>

    <div>
      <div class="flex justify-between font-mono mb-1">
        <span>Roughness</span>
        <span>{roughness.toFixed(2)}</span>
      </div>
      <input type="range" min="0" max="1" step="0.05" bind:value={roughness} class="w-full accent-[#19E6D2]" />
    </div>
  </div>

  <!-- Toggles -->
  <div class="space-y-3 pt-2 border-t border-[#1C2638]">
    <div class="flex justify-between items-center">
      <span class="font-mono">Wireframe Mode</span>
      <input type="checkbox" bind:checked={wireframe} class="accent-[#19E6D2] w-4 h-4 cursor-pointer" />
    </div>

    <div class="flex justify-between items-center">
      <span class="font-mono">Auto Rotation</span>
      <input type="checkbox" bind:checked={autoRotate} class="accent-[#19E6D2] w-4 h-4 cursor-pointer" />
    </div>
  </div>
</div>
