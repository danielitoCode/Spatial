<script lang="ts">
  import CodeBlock from '../../components/CodeBlock.svelte';
</script>

<div class="lg:col-span-5 p-8 md:p-10 bg-[#0A0E17] flex flex-col justify-center">
  <h3 class="text-2xl md:text-3xl font-bold text-[#e1e2ec] mb-4">Compose-Native Control</h3>
  <p class="text-sm md:text-base text-[#bacac6] mb-8 leading-relaxed">
    Bind your 3D properties directly to your UI state. Animations are handled via standard Compose animatables.
  </p>

  <CodeBlock title="MainActivity.kt">
    <span class="text-[#8B5CF6]">val</span> rotation <span class="text-[#159FE8]">by</span> animateFloatAsState(...)<br /><br />
    <span class="text-[#19E6D2]">SpatialSurface</span>(<br />
    &nbsp;&nbsp;modifier = Modifier.fillMaxSize()<br />
    ) {'{'}<br />
    &nbsp;&nbsp;<span class="text-[#19E6D2]">Node</span>(<br />
    &nbsp;&nbsp;&nbsp;&nbsp;shape = <span class="text-[#F25933]">Box</span>(1f),<br />
    &nbsp;&nbsp;&nbsp;&nbsp;material = <span class="text-[#19E6D2]">Neon</span>,<br />
    &nbsp;&nbsp;&nbsp;&nbsp;rotation = rotation<br />
    &nbsp;&nbsp;)<br />
    {'}'}
  </CodeBlock>
</div>
