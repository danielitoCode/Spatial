<script lang="ts">
  import GlassPanel from '../../components/GlassPanel.svelte';
</script>

<GlassPanel class="p-6 rounded-xl border-[#19E6D2]/40 bg-[#0A0E17]/80" glow>
  <div class="flex justify-between items-center mb-4 border-b border-[#1C2638] pb-2">
    <span class="font-mono text-xs font-bold text-[#19E6D2]">SPATIAL DECLARATIVE</span>
    <span class="material-symbols-outlined text-[#19E6D2] text-lg">check</span>
  </div>

  <pre class="font-mono text-xs md:text-sm text-[#e1e2ec] overflow-x-auto leading-relaxed"><code>SpatialScene {'{'}
    OrbitalCamera(distance = 5f)
    
    Model(
        src = "assets/ship.glb",
        rotation = rotationState,
        scale = 1.5f
    )
    
    PointLight(color = Color.Cyan)
{'}'}</code></pre>
</GlassPanel>
