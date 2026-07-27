<script lang="ts">
  import CodeBlock from '../../components/CodeBlock.svelte';

  interface Props {
    shape: string;
    color: string;
    metalness: number;
    roughness: number;
    wireframe: boolean;
  }

  let { shape, color, metalness, roughness, wireframe }: Props = $props();

  const capitalize = (s: string) => s.charAt(0).toUpperCase() + s.slice(1);

  const kotlinCode = $derived(
`@Composable
fun Generated3DView() {
    val cameraState = rememberCameraState(
        yaw = 45f.deg,
        pitch = (-15f).deg,
        zoom = 1.0f
    )

    Scene(
        modifier = Modifier.fillMaxSize(),
        renderHostFactory = DefaultSceneRenderHostFactory,
        cameraState = cameraState,
        gestures = Gestures.orbitAndZoom()
    ) {
        Element.${capitalize(shape)}(
            modifier = Modifier3D.Default
                .size(1.5f.meters)
                .position(0f, 1f, 0f),
            material = Material.PBR(
                color = Color(parseColor("${color}")),
                metalness = ${metalness.toFixed(2)}f,
                roughness = ${roughness.toFixed(2)}f,
                wireframe = ${wireframe}
            )
        )
    }
}`
  );
</script>

<div class="space-y-4">
  <div class="flex items-center justify-between px-2">
    <div class="flex items-center gap-2">
        <div class="w-2 h-2 rounded-full bg-[#19E6D2] animate-pulse"></div>
        <span class="font-mono text-[10px] text-[#19E6D2] font-black tracking-[0.2em] uppercase">Export Source</span>
    </div>
    <div class="flex items-center gap-4">
        <span class="text-[9px] font-mono text-[#6F7A90] uppercase">Target: Jetpack Compose v1.7.0+</span>
        <button class="text-[10px] font-bold text-[#19E6D2] hover:underline cursor-pointer">COPY_RAW</button>
    </div>
  </div>

  <div class="group relative">
    <div class="absolute -inset-1 bg-gradient-to-r from-[#19E6D2]/20 to-[#8B5CF6]/20 rounded-3xl blur opacity-25 group-hover:opacity-50 transition duration-1000 group-hover:duration-200"></div>
    <div class="relative">
        <CodeBlock title="SpatialScene.kt" lang="kotlin">
            <pre class="text-[11px] md:text-xs"><code>{kotlinCode}</code></pre>
        </CodeBlock>
    </div>
  </div>
</div>
