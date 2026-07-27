<script lang="ts">
  import SpatialButton from '../../components/SpatialButton.svelte';

  interface Props {
    shape: 'box' | 'sphere' | 'torus' | 'cylinder' | 'plane';
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
    { name: 'Cyber Neon', shape: 'torus', color: '#19E6D2', emissive: '#8B5CF6', metalness: 0.8, roughness: 0.1 },
    { name: 'Pure Chrome', shape: 'sphere', color: '#e1e2ec', emissive: '#000000', metalness: 0.95, roughness: 0.05 },
    { name: 'Emerald', shape: 'box', color: '#10B981', emissive: '#064E3B', metalness: 0.7, roughness: 0.3 },
    { name: 'Solar', shape: 'cylinder', color: '#F25933', emissive: '#7C2D12', metalness: 0.4, roughness: 0.4 }
  ];

  function applyPreset(p: typeof presets[0]) {
    shape = p.shape as any;
    color = p.color;
    emissive = p.emissive;
    metalness = p.metalness;
    roughness = p.roughness;
  }

  const shapes: Array<'box' | 'sphere' | 'torus' | 'cylinder' | 'plane'> = [
    'box', 'sphere', 'torus', 'cylinder', 'plane'
  ];
</script>

<div class="glass-panel p-6 rounded-3xl flex flex-col gap-8 text-xs">
  <!-- Presets -->
  <section class="space-y-3">
    <span class="text-[#6F7A90] font-mono tracking-tighter uppercase font-bold">Quick Presets</span>
    <div class="grid grid-cols-2 gap-3">
      {#each presets as p}
        <button
          onclick={() => applyPreset(p)}
          class="p-3 bg-[#101624] hover:bg-[#19E6D2]/5 border border-[#1C2638] hover:border-[#19E6D2]/30 rounded-2xl text-left transition-all group"
        >
          <div class="font-bold text-[#e1e2ec] group-hover:text-[#19E6D2]">{p.name}</div>
          <div class="text-[9px] text-[#6F7A90] font-mono mt-1 uppercase">{p.shape} • PBR</div>
        </button>
      {/each}
    </div>
  </section>

  <!-- Geometry -->
  <section class="space-y-3">
    <span class="text-[#6F7A90] font-mono tracking-tighter uppercase font-bold">Geometry</span>
    <div class="flex flex-wrap p-1 bg-[#101624] rounded-2xl border border-[#1C2638]">
      {#each shapes as s}
        <button
          onclick={() => (shape = s)}
          class="flex-1 min-w-[60px] py-2.5 rounded-xl font-mono capitalize transition-all {shape === s
            ? 'bg-[#19E6D2] text-[#00201c] font-black shadow-lg shadow-[#19E6D2]/20'
            : 'text-[#6F7A90] hover:text-[#e1e2ec]'}"
        >
          {s}
        </button>
      {/each}
    </div>
  </section>

  <!-- Material -->
  <section class="space-y-4 pt-2">
    <span class="text-[#6F7A90] font-mono tracking-tighter uppercase font-bold">Material Properties</span>

    <div class="space-y-2">
      <div class="flex justify-between font-mono mb-1">
        <span class="text-[#A6B0C3]">Albedo Color</span>
        <span class="text-[#19E6D2] font-bold">{color}</span>
      </div>
      <div class="flex gap-3 items-center bg-[#101624] p-3 rounded-2xl border border-[#1C2638]">
        <div class="relative w-10 h-10 rounded-full overflow-hidden border-2 border-[#1C2638]">
          <input type="color" bind:value={color} class="absolute -inset-2 w-[200%] h-[200%] cursor-pointer bg-transparent border-0" />
        </div>
        <div class="flex gap-2 flex-1">
          {#each ['#19E6D2', '#159FE8', '#8B5CF6', '#F25933'] as c}
            <button
              onclick={() => (color = c)}
              aria-label="Set color to {c}"
              class="w-6 h-6 rounded-full border border-white/10 transition-transform active:scale-90"
              style="background-color: {c}"
            ></button>
          {/each}
        </div>
      </div>
    </div>

    <div class="grid grid-cols-2 gap-4">
      <div class="space-y-2">
        <div class="flex justify-between font-mono text-[10px]">
          <span class="text-[#A6B0C3]">Metalness</span>
          <span>{metalness.toFixed(2)}</span>
        </div>
        <input type="range" min="0" max="1" step="0.01" bind:value={metalness} class="w-full accent-[#19E6D2]" aria-label="Metalness" />
      </div>
      <div class="space-y-2">
        <div class="flex justify-between font-mono text-[10px]">
          <span class="text-[#A6B0C3]">Roughness</span>
          <span>{roughness.toFixed(2)}</span>
        </div>
        <input type="range" min="0" max="1" step="0.01" bind:value={roughness} class="w-full accent-[#19E6D2]" aria-label="Roughness" />
      </div>
    </div>
  </section>

  <!-- Rendering Toggles -->
  <section class="grid grid-cols-2 gap-4 pt-4 border-t border-[#1C2638]">
    <label class="flex flex-col gap-2 p-4 bg-[#101624] rounded-2xl border border-[#1C2638] cursor-pointer hover:border-[#19E6D2]/40 transition-colors">
      <div class="flex justify-between items-center">
        <span class="font-bold text-[#e1e2ec]">Wireframe</span>
        <input type="checkbox" bind:checked={wireframe} class="accent-[#19E6D2] w-4 h-4 rounded" />
      </div>
      <span class="text-[9px] text-[#6F7A90] font-mono leading-none">DEBUG_MESH_GRID</span>
    </label>

    <label class="flex flex-col gap-2 p-4 bg-[#101624] rounded-2xl border border-[#1C2638] cursor-pointer hover:border-[#19E6D2]/40 transition-colors">
      <div class="flex justify-between items-center">
        <span class="font-bold text-[#e1e2ec]">Auto Orbit</span>
        <input type="checkbox" bind:checked={autoRotate} class="accent-[#19E6D2] w-4 h-4 rounded" />
      </div>
      <span class="text-[9px] text-[#6F7A90] font-mono leading-none">CINEMATIC_VIEW</span>
    </label>
  </section>
</div>

<style>
  input[type="range"] {
    appearance: none;
    -webkit-appearance: none;
    background: #1C2638;
    height: 4px;
    border-radius: 2px;
  }

  input[type="range"]::-webkit-slider-thumb {
    -webkit-appearance: none;
    height: 14px;
    width: 14px;
    border-radius: 50%;
    background: #19E6D2;
    cursor: pointer;
    box-shadow: 0 0 10px rgba(25, 230, 210, 0.4);
  }

  input[type="range"]::-moz-range-thumb {
    height: 14px;
    width: 14px;
    border-radius: 50%;
    background: #19E6D2;
    cursor: pointer;
    border: none;
    box-shadow: 0 0 10px rgba(25, 230, 210, 0.4);
  }
</style>
