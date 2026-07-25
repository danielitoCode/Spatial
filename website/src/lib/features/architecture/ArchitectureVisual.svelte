<script lang="ts">
  import GlassPanel from '../../components/GlassPanel.svelte';
</script>

<GlassPanel class="relative aspect-square rounded-2xl border border-[#1C2638] bg-[#0A0E17] overflow-hidden group">
  <div class="absolute inset-0 bg-gradient-to-tr from-[#19E6D2]/10 via-transparent to-[#8B5CF6]/10"></div>

  <div class="absolute inset-0 flex items-center justify-center">
    <div class="w-64 h-64 border-2 border-dashed border-[#1C2638] rounded-full animate-spin-slow flex items-center justify-center">
      <div class="w-44 h-44 border border-[#19E6D2]/30 rounded-xl flex items-center justify-center bg-[#101624]/40 backdrop-blur-sm">
        <span class="material-symbols-outlined text-7xl text-[#19E6D2] group-hover:scale-110 transition-transform duration-500" style="font-variation-settings: 'FILL' 1;">
          hexagon
        </span>
      </div>
    </div>
  </div>
</GlassPanel>
