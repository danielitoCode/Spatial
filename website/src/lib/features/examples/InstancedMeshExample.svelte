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
      <h3 class="text-xl font-bold text-[#e1e2ec]">GPU Instanced <GradientText>Wave Field</GradientText></h3>
      <p class="text-xs text-[#6F7A90]">Massive instance rendering with zero overhead on the main thread</p>
    </div>
    <div class="px-2 py-1 bg-[#19E6D2]/10 rounded border border-[#19E6D2]/30 text-[10px] text-[#19E6D2] font-mono">
      INSTANCES: {count}
    </div>
  </div>

  <div class="h-96 relative bg-[#05070D] rounded-xl overflow-hidden border border-[#1C2638]">
    <Canvas>
      <InternalInstancedMeshScene {count} />
    </Canvas>

    <div class="absolute bottom-4 right-4 flex flex-col items-end gap-2">
       <div class="bg-[#0A0E17]/90 p-3 rounded-lg border border-[#1C2638] text-[10px] font-mono max-w-[200px]">
          <span class="text-[#6F7A90]">// Spatial Instancing API</span><br/>
          <span class="text-[#8B5CF6]">InstancedElement</span>(<br/>
          &nbsp;&nbsp;count = <span class="text-[#19E6D2]">{count}</span>,<br/>
          &nbsp;&nbsp;mesh = <span class="text-[#159FE8]">Mesh.Cube</span><br/>
          )
       </div>
    </div>
  </div>
</GlassPanel>
