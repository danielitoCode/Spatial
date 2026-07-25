<script lang="ts">
  import CodeBlock from '../../components/CodeBlock.svelte';
  import GradientText from '../../components/GradientText.svelte';
  import GlassPanel from '../../components/GlassPanel.svelte';
</script>

<div class="py-16 bg-[#05070D] min-h-[85vh]">
  <div class="max-w-5xl mx-auto px-6 space-y-12">
    <!-- Title Header -->
    <div class="text-center max-w-3xl mx-auto">
      <div class="inline-block px-3.5 py-1 rounded-full bg-[#159FE8]/10 border border-[#159FE8]/20 text-[#159FE8] font-mono text-xs font-semibold tracking-wider mb-4">
        DOCUMENTATION
      </div>
      <h1 class="text-3xl md:text-5xl font-extrabold text-[#e1e2ec] mb-4">
        Spatial <GradientText>Developer Guide</GradientText>
      </h1>
      <p class="text-base text-[#bacac6] leading-relaxed">
        Everything you need to integrate cinematic 3D graphics into your Jetpack Compose Android apps.
      </p>
    </div>

    <!-- Quick Start -->
    <div class="space-y-6">
      <h2 class="text-2xl font-bold text-[#e1e2ec] flex items-center gap-2">
        <span class="material-symbols-outlined text-[#19E6D2]">rocket_launch</span>
        1. Dependency Setup
      </h2>
      <CodeBlock title="build.gradle.kts" lang="kotlin">
        dependencies {'{'}<br />
        &nbsp;&nbsp;implementation(<span class="text-[#19E6D2]">"io.spatial:rendering-core:1.2.0"</span>)<br />
        &nbsp;&nbsp;implementation(<span class="text-[#19E6D2]">"io.spatial:compose-ui:1.2.0"</span>)<br />
        &nbsp;&nbsp;implementation(<span class="text-[#19E6D2]">"io.spatial:assets-gltf:1.2.0"</span>)<br />
        {'}'}
      </CodeBlock>
    </div>

    <!-- First Scene -->
    <div class="space-y-6">
      <h2 class="text-2xl font-bold text-[#e1e2ec] flex items-center gap-2">
        <span class="material-symbols-outlined text-[#19E6D2]">layers</span>
        2. Create Your First Spatial Scene
      </h2>
      <p class="text-sm text-[#bacac6]">
        Spatial seamlessly integrates with Jetpack Compose views. Wrap your 3D elements inside a <code class="text-[#19E6D2] font-mono">SpatialScene</code> composable:
      </p>
      <CodeBlock title="MainActivity.kt" lang="kotlin">
        @Composable<br />
        fun SpatialScreen() {'{'}<br />
        &nbsp;&nbsp;SpatialScene(<br />
        &nbsp;&nbsp;&nbsp;&nbsp;modifier = Modifier.fillMaxSize()<br />
        &nbsp;&nbsp;) {'{'}<br />
        &nbsp;&nbsp;&nbsp;&nbsp;OrbitalCamera(distance = 5f)<br />
        &nbsp;&nbsp;&nbsp;&nbsp;PointLight(color = Color.Cyan, intensity = 10f)<br />
        &nbsp;&nbsp;&nbsp;&nbsp;Model(src = <span class="text-[#19E6D2]">"models/robot.glb"</span>, scale = 1.5f)<br />
        &nbsp;&nbsp;{'}'}<br />
        {'}'}
      </CodeBlock>
    </div>

    <!-- API Reference Grid -->
    <div class="space-y-6">
      <h2 class="text-2xl font-bold text-[#e1e2ec] flex items-center gap-2">
        <span class="material-symbols-outlined text-[#19E6D2]">code</span>
        3. Core Composables Reference
      </h2>
      <div class="grid md:grid-cols-2 gap-6">
        <GlassPanel class="p-6 rounded-xl border border-[#1C2638] bg-[#0A0E17]">
          <h3 class="font-mono text-sm font-bold text-[#19E6D2] mb-2">SpatialScene</h3>
          <p class="text-xs text-[#bacac6] leading-relaxed">
            The root composable container hosting the Vulkan/OpenGL WebGL rendering surface and managing life-cycle ticks.
          </p>
        </GlassPanel>

        <GlassPanel class="p-6 rounded-xl border border-[#1C2638] bg-[#0A0E17]">
          <h3 class="font-mono text-sm font-bold text-[#159FE8] mb-2">OrbitalCamera</h3>
          <p class="text-xs text-[#bacac6] leading-relaxed">
            Touch-interactive camera controller supporting pinch-to-zoom, orbital drag rotation, and focus target physics.
          </p>
        </GlassPanel>

        <GlassPanel class="p-6 rounded-xl border border-[#1C2638] bg-[#0A0E17]">
          <h3 class="font-mono text-sm font-bold text-[#8B5CF6] mb-2">SpatialNode</h3>
          <p class="text-xs text-[#bacac6] leading-relaxed">
            Atomic element node representing geometries, PBR materials, custom AGSL shaders, and position transforms.
          </p>
        </GlassPanel>

        <GlassPanel class="p-6 rounded-xl border border-[#1C2638] bg-[#0A0E17]">
          <h3 class="font-mono text-sm font-bold text-[#F25933] mb-2">PointLight / DirectionalLight</h3>
          <p class="text-xs text-[#bacac6] leading-relaxed">
            Dynamic light sources with real-time shadow casting, specular highlights, and color temperature controls.
          </p>
        </GlassPanel>
      </div>
    </div>
  </div>
</div>
