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
    { name: 'Neon Void', shape: 'torus', color: '#19E6D2', emissive: '#8B5CF6', metalness: 0.8, roughness: 0.1 },
    { name: 'Liquid Metal', shape: 'sphere', color: '#E6EDF3', emissive: '#000000', metalness: 0.98, roughness: 0.02 },
    { name: 'Jade Glass', shape: 'box', color: '#10B981', emissive: '#064E3B', metalness: 0.5, roughness: 0.1 },
    { name: 'Magma Core', shape: 'cylinder', color: '#F25933', emissive: '#7C2D12', metalness: 0.4, roughness: 0.4 }
  ];

  function applyPreset(p: typeof presets[0]) {
    shape = p.shape as any;
    color = p.color;
    emissive = p.emissive;
    metalness = p.metalness;
    roughness = p.roughness;
    onApplyPreset(p.name);
  }

  const shapes: Array<'box' | 'sphere' | 'torus' | 'cylinder' | 'plane'> = [
    'box', 'sphere', 'torus', 'cylinder', 'plane'
  ];
</script>

<div class="glass-panel p-1 rounded-[2.5rem] bg-white/[0.02] border-white/5">
  <div class="bg-[#0D1117] rounded-[2.2rem] p-6 space-y-8 shadow-2xl">
    <!-- Presets Section -->
    <section class="space-y-4">
      <div class="flex items-center gap-2 px-1">
        <div class="w-1 h-3 bg-primary rounded-full"></div>
        <span class="text-[10px] font-black text-white uppercase tracking-widest">Global Presets</span>
      </div>
      <div class="grid grid-cols-2 gap-2.5">
        {#each presets as p}
          <button
            onclick={() => applyPreset(p)}
            class="group p-3 bg-white/[0.03] hover:bg-primary/[0.08] border border-white/[0.05] hover:border-primary/30 rounded-2xl text-left transition-all duration-300"
          >
            <div class="font-bold text-silver group-hover:text-primary text-[11px] transition-colors">{p.name}</div>
            <div class="text-[8px] text-silver/40 font-mono mt-0.5 uppercase tracking-tighter">{p.shape} · Cinematic</div>
          </button>
        {/each}
      </div>
    </section>

    <!-- Geometry Selector -->
    <section class="space-y-4">
       <div class="flex items-center gap-2 px-1">
        <div class="w-1 h-3 bg-secondary rounded-full"></div>
        <span class="text-[10px] font-black text-white uppercase tracking-widest">Base Geometry</span>
      </div>
      <div class="flex p-1.5 bg-black/40 rounded-2xl border border-white/5">
        {#each shapes as s}
          <button
            onclick={() => (shape = s)}
            class="flex-1 py-2 rounded-xl text-[10px] font-bold capitalize transition-all duration-500 {shape === s
              ? 'bg-primary text-black shadow-lg shadow-primary/30 scale-[1.02]'
              : 'text-silver/50 hover:text-silver/80'}"
          >
            {s}
          </button>
        {/each}
      </div>
    </section>

    <!-- Material Props -->
    <section class="space-y-6 pt-2">
      <div class="flex items-center gap-2 px-1">
        <div class="w-1 h-3 bg-tertiary rounded-full"></div>
        <span class="text-[10px] font-black text-white uppercase tracking-widest">Material Engine</span>
      </div>

      <div class="space-y-4">
        <!-- Color -->
        <div class="bg-white/[0.02] p-4 rounded-3xl border border-white/[0.05]">
          <div class="flex justify-between items-center mb-4 px-1">
            <span class="text-[11px] font-bold text-silver">Albedo Base</span>
            <span class="text-[10px] font-mono text-primary bg-primary/10 px-2 py-0.5 rounded-lg border border-primary/20 tracking-tighter">{color}</span>
          </div>
          <div class="flex gap-3 items-center">
            <div class="relative w-12 h-12 rounded-2xl overflow-hidden border-2 border-white/10 shadow-lg shadow-black/40">
              <input type="color" bind:value={color} class="absolute -inset-4 w-[200%] h-[200%] cursor-pointer bg-transparent border-0" />
            </div>
            <div class="flex-1 grid grid-cols-5 gap-2">
              {#each ['#19E6D2', '#159FE8', '#8B5CF6', '#F25933', '#E6EDF3'] as c}
                <button
                  onclick={() => (color = c)}
                  class="aspect-square rounded-xl border border-white/5 transition-all hover:scale-110 active:scale-90 {color === c ? 'ring-2 ring-primary ring-offset-4 ring-offset-[#0D1117] scale-105 shadow-xl' : 'opacity-60 hover:opacity-100'}"
                  style="background-color: {c}"
                ></button>
              {/each}
            </div>
          </div>
        </div>

        <!-- Sliders -->
        <div class="grid grid-cols-1 gap-4">
          <div class="bg-white/[0.02] p-4 rounded-3xl border border-white/[0.05] space-y-3">
            <div class="flex justify-between font-mono text-[10px] font-black uppercase tracking-widest">
              <span class="text-silver/50 italic">Metallicity</span>
              <span class="text-primary">{Math.round(metalness * 100)}%</span>
            </div>
            <input type="range" min="0" max="1" step="0.01" bind:value={metalness} class="custom-range" aria-label="Metalness" />
          </div>

          <div class="bg-white/[0.02] p-4 rounded-3xl border border-white/[0.05] space-y-3">
            <div class="flex justify-between font-mono text-[10px] font-black uppercase tracking-widest">
              <span class="text-silver/50 italic">Surface Roughness</span>
              <span class="text-primary">{Math.round(roughness * 100)}%</span>
            </div>
            <input type="range" min="0" max="1" step="0.01" bind:value={roughness} class="custom-range" aria-label="Roughness" />
          </div>
        </div>
      </div>
    </section>

    <!-- Toggles Grid -->
    <section class="grid grid-cols-2 gap-3">
      <label class="flex flex-col gap-2 p-4 bg-white/[0.02] hover:bg-white/[0.05] rounded-3xl border border-white/[0.05] cursor-pointer group transition-all duration-300">
        <div class="flex justify-between items-center">
          <span class="text-[11px] font-black text-silver group-hover:text-white uppercase tracking-tighter">Wireframe</span>
          <input type="checkbox" bind:checked={wireframe} class="custom-checkbox" />
        </div>
        <div class="h-0.5 w-6 bg-primary/20 rounded-full group-hover:w-full transition-all duration-500"></div>
      </label>

      <label class="flex flex-col gap-2 p-4 bg-white/[0.02] hover:bg-white/[0.05] rounded-3xl border border-white/[0.05] cursor-pointer group transition-all duration-300">
        <div class="flex justify-between items-center">
          <span class="text-[11px] font-black text-silver group-hover:text-white uppercase tracking-tighter">Auto Orbit</span>
          <input type="checkbox" bind:checked={autoRotate} class="custom-checkbox" />
        </div>
        <div class="h-0.5 w-6 bg-primary/20 rounded-full group-hover:w-full transition-all duration-500"></div>
      </label>
    </section>
  </div>
</div>

<style>
  .custom-range {
    appearance: none;
    -webkit-appearance: none;
    background: #1D2128;
    height: 4px;
    border-radius: 99px;
    width: 100%;
    cursor: pointer;
  }

  .custom-range::-webkit-slider-thumb {
    -webkit-appearance: none;
    height: 18px;
    width: 18px;
    border-radius: 50%;
    background: #E6EDF3;
    border: 4px solid #19E6D2;
    box-shadow: 0 0 15px rgba(25, 230, 210, 0.5);
    transition: transform 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  }

  .custom-range::-webkit-slider-thumb:hover {
    transform: scale(1.1);
  }

  .custom-checkbox {
    appearance: none;
    width: 18px;
    height: 18px;
    background: #1D2128;
    border: 2px solid #30363D;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    transition: all 0.3s;
  }

  .custom-checkbox:checked {
    background: #19E6D2;
    border-color: #19E6D2;
    box-shadow: 0 0 10px rgba(25, 230, 210, 0.4);
  }

  .custom-checkbox:checked::after {
    content: '✓';
    position: absolute;
    inset: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    color: black;
    font-weight: 900;
    font-size: 10px;
  }
</style>
