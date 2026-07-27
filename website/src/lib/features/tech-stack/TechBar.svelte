<script lang="ts">
  const techItems = [
    { icon: 'terminal', label: 'Kotlin 2.2', color: 'text-primary' },
    { icon: 'android', label: 'Android GLES', color: 'text-secondary' },
    { icon: 'layers', label: 'Compose 1.7', color: 'text-tertiary' },
    { icon: 'view_in_ar', label: 'Vulkan Ready', color: 'text-primary' }
  ];
</script>

<section class="border-y border-white/5 bg-black/40 backdrop-blur-3xl py-10 relative overflow-hidden">
  <!-- Moving gradient flare -->
  <div class="absolute inset-0 bg-gradient-to-r from-transparent via-primary/5 to-transparent -translate-x-full animate-[shimmer_8s_infinite]"></div>

  <div class="max-w-7xl mx-auto px-6 flex flex-wrap justify-around items-center gap-12 relative z-10">
    {#each techItems as item}
      <div class="flex items-center gap-3 group">
        <div class="w-10 h-10 flex items-center justify-center rounded-xl bg-white/[0.03] border border-white/[0.05] group-hover:border-white/20 transition-all duration-500">
           <span class="material-symbols-outlined {item.color} text-lg group-hover:scale-110 transition-transform">{item.icon}</span>
        </div>
        <div class="flex flex-col">
            <span class="text-[9px] font-black text-silver/40 uppercase tracking-widest leading-none mb-1">Module</span>
            <span class="text-xs font-bold text-silver/80 group-hover:text-white transition-colors tracking-tight">{item.label}</span>
        </div>
      </div>
    {/each}
  </div>
</section>

<style>
  @keyframes shimmer {
    100% {
      transform: translateX(100%);
    }
  }
</style>
