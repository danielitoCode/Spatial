<script lang="ts">
  import { Canvas } from '@threlte/core';
  import InternalInstancedMeshScene from './InternalInstancedMeshScene.svelte';
  import GlassPanel from '../../components/GlassPanel.svelte';
  import GradientText from '../../components/GradientText.svelte';

  let count = 40;
</script>

<GlassPanel class="p-6 rounded-2xl bg-[#0A0E17] border border-[#1C2638] flex flex-col gap-6 overflow-hidden">
  <div class="flex justify-between items-start border-b border-[#1C2638] pb-4">
    <div>
      <h3 class="text-xl font-bold text-[#e1e2ec]">Wave Field <GradientText>(Web)</GradientText></h3>
      <p class="text-xs text-[#6F7A90]">Three.js preview — Spatial has no InstancedElement API yet</p>
    </div>
    <div class="px-2 py-1 bg-[#19E6D2]/10 rounded border border-[#19E6D2]/30 text-[10px] text-[#19E6D2] font-mono">
      PREVIEW: {count}
    </div>
  </div>

  <div class="h-96 relative bg-[#05070D] rounded-xl overflow-hidden border border-[#1C2638]">
    <Canvas>
      <InternalInstancedMeshScene {count} />
    </Canvas>

    <div class="absolute bottom-4 right-4 flex flex-col items-end gap-2">
       <div class="bg-[#0A0E17]/90 p-3 rounded-lg border border-[#1C2638] text-[10px] font-mono max-w-[220px]">
          <span class="text-[#6F7A90]">// Real API today</span><br/>
          <span class="text-[#8B5CF6]">Element.Cube</span>(<br/>
          &nbsp;&nbsp;modifier = Modifier3D…<br/>
          )
       </div>
    </div>
  </div>
</GlassPanel>
