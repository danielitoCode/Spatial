<script lang="ts">
  interface Props {
    logs?: Array<{ type: 'info' | 'success' | 'error' | 'warn'; message: string; time: string }>;
  }

  let { logs = [] }: Props = $props();

  const defaultLogs = [
    { type: 'info' as const, message: 'Spatial Engine v1.2.0 initializing...', time: '00:00.001' },
    { type: 'success' as const, message: 'Vulkan backend detected. Switching to high-performance mode.', time: '00:00.012' },
    { type: 'info' as const, message: 'ChoreographerFrameScheduler ready. VSYNC locked at 60fps.', time: '00:00.018' },
    { type: 'success' as const, message: 'SpatialScene compiled and mounted.', time: '00:00.031' },
    { type: 'info' as const, message: 'OrbitalCamera: distance=4.0f, pitch=20°, yaw=45°', time: '00:00.035' },
    { type: 'info' as const, message: 'PointLight registered. Intensity=10f, Color=#19E6D2', time: '00:00.038' },
    { type: 'success' as const, message: '✓ Scene ready. Rendering 60 FPS.', time: '00:00.040' },
  ];

  const allLogs = $derived([...defaultLogs, ...logs]);

  const colorMap = {
    info: 'text-[#bacac6]',
    success: 'text-[#19E6D2]',
    error: 'text-red-400',
    warn: 'text-amber-400'
  };

  const prefixMap = {
    info: '●',
    success: '✓',
    error: '✗',
    warn: '⚠'
  };
</script>

<div class="h-full flex flex-col bg-[#05070D] rounded-xl overflow-hidden border border-[#1C2638]">
  <!-- Console Header -->
  <div class="flex items-center justify-between px-3 py-2 bg-[#101624] border-b border-[#1C2638]">
    <div class="flex items-center gap-2 text-xs font-mono text-[#6F7A90]">
      <span class="material-symbols-outlined text-sm text-[#19E6D2]">terminal</span>
      RENDER CONSOLE
    </div>
    <div class="flex items-center gap-3">
      <span class="w-2 h-2 rounded-full bg-[#19E6D2] animate-pulse"></span>
      <span class="font-mono text-[10px] text-[#19E6D2]">LIVE · 60FPS</span>
    </div>
  </div>

  <!-- Log Lines -->
  <div class="flex-1 overflow-y-auto p-3 space-y-1 font-mono text-[11px]">
    {#each allLogs as log}
      <div class="flex gap-3">
        <span class="text-[#6F7A90] shrink-0">[{log.time}]</span>
        <span class="{colorMap[log.type]} shrink-0">{prefixMap[log.type]}</span>
        <span class="{colorMap[log.type]}">{log.message}</span>
      </div>
    {/each}
  </div>
</div>
