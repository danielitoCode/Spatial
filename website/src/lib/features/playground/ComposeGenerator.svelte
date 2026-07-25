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
`SpatialScene(
    modifier = Modifier.fillMaxSize()
) {
    OrbitalCamera(distance = 4.0f)

    PointLight(
        color = Color(0xFF19E6D2),
        intensity = 10f,
        position = Vector3(3f, 3f, 3f)
    )

    SpatialNode(
        shape = Shape.${capitalize(shape)}(),
        material = Material.PBR(
            color = Color(parseColor("${color}")),
            metalness = ${metalness.toFixed(2)}f,
            roughness = ${roughness.toFixed(2)}f,
            wireframe = ${wireframe}
        )
    )
}`
  );
</script>

<div class="space-y-3">
  <div class="flex items-center justify-between">
    <span class="font-mono text-xs text-[#19E6D2] font-semibold tracking-wider">GENERATED KOTLIN JETPACK COMPOSE CODE</span>
    <span class="text-[10px] font-mono text-[#6F7A90]">LIVE EXPORT</span>
  </div>

  <CodeBlock title="GeneratedScene.kt" lang="kotlin">
    <pre><code>{kotlinCode}</code></pre>
  </CodeBlock>
</div>
