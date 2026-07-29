<script lang="ts">
  import CodeBlock from '../../components/CodeBlock.svelte';
</script>

<div class="lg:col-span-5 p-8 md:p-10 bg-[#0A0E17] flex flex-col justify-center">
  <h3 class="text-2xl md:text-3xl font-bold text-[#e1e2ec] mb-4">Compose-Native Control</h3>
  <p class="text-sm md:text-base text-[#bacac6] mb-8 leading-relaxed">
    Describe the scene graph in Compose. Camera and nodes update as state changes — the runtime adapter hosts GLES for you.
  </p>

  <CodeBlock title="MainActivity.kt">
    <span class="text-[#8B5CF6]">val</span> cameraState = <span class="text-[#19E6D2]">rememberCameraState</span>()<br /><br />
    <span class="text-[#19E6D2]">Scene</span>(<br />
    &nbsp;&nbsp;modifier = Modifier.fillMaxSize(),<br />
    &nbsp;&nbsp;renderHostFactory = DefaultSceneRenderHostFactory,<br />
    &nbsp;&nbsp;cameraState = cameraState,<br />
    &nbsp;&nbsp;gestures = Gestures.orbitAndZoom(),<br />
    ) {'{'}<br />
    &nbsp;&nbsp;<span class="text-[#19E6D2]">Element.Cube</span>(<br />
    &nbsp;&nbsp;&nbsp;&nbsp;modifier = Modifier3D.Default.size(2f.meters)<br />
    &nbsp;&nbsp;)<br />
    {'}'}
  </CodeBlock>
</div>
