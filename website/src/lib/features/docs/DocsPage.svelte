<script lang="ts">
  import CodeBlock from '../../components/CodeBlock.svelte';
  import GradientText from '../../components/GradientText.svelte';
  import GlassPanel from '../../components/GlassPanel.svelte';
</script>

<div class="py-32 bg-[#05070D] min-h-screen">
  <div class="max-w-6xl mx-auto px-6 space-y-24">
    <!-- Title Header -->
    <div class="text-center max-w-4xl mx-auto space-y-6">
      <div class="inline-flex items-center gap-2 px-4 py-1 rounded-full bg-secondary/10 border border-secondary/20 text-secondary font-black text-[10px] tracking-[0.3em] uppercase">
        Implementation Guide
      </div>
      <h1 class="text-4xl md:text-6xl lg:text-7xl font-black text-white tracking-tighter leading-tight">
        Spatial <GradientText>Developer Docs</GradientText>
      </h1>
      <p class="text-lg text-silver/50 leading-relaxed max-w-2xl mx-auto font-medium">
        Master the art of 3D state management in Android. From basic primitives to advanced cinematic camera orchestrations.
      </p>
    </div>

    <!-- Content Sections -->
    <div class="grid grid-cols-1 gap-20">
      <!-- Quick Start -->
      <section class="space-y-8">
        <div class="flex items-center gap-4">
          <div class="w-12 h-12 rounded-2xl bg-primary/10 border border-primary/20 flex items-center justify-center text-primary shadow-[0_0_20px_rgba(25,230,210,0.1)]">
            <span class="material-symbols-outlined text-2xl">rocket_launch</span>
          </div>
          <h2 class="text-3xl font-black text-white tracking-tight">Dependency Setup</h2>
        </div>

        <div class="glass-panel p-1 rounded-[2.5rem] bg-white/[0.02] border-white/5">
            <div class="bg-[#0D1117] rounded-[2.2rem] p-2 shadow-2xl">
                <CodeBlock title="build.gradle.kts" lang="kotlin">
                    dependencies {'{'}<br />
                    &nbsp;&nbsp;implementation(<span class="text-primary">"io.spatial:rendering-core:1.2.0"</span>)<br />
                    &nbsp;&nbsp;implementation(<span class="text-primary">"io.spatial:compose-ui:1.2.0"</span>)<br />
                    &nbsp;&nbsp;implementation(<span class="text-primary">"io.spatial:assets-gltf:1.2.0"</span>)<br />
                    {'}'}
                </CodeBlock>
            </div>
        </div>
      </section>

      <!-- First Scene -->
      <section class="space-y-8">
        <div class="flex items-center gap-4">
          <div class="w-12 h-12 rounded-2xl bg-secondary/10 border border-secondary/20 flex items-center justify-center text-secondary shadow-[0_0_20px_rgba(21,159,232,0.1)]">
            <span class="material-symbols-outlined text-2xl">layers</span>
          </div>
          <h2 class="text-3xl font-black text-white tracking-tight">Declarative Scenes</h2>
        </div>

        <p class="text-base text-silver/60 max-w-2xl font-medium leading-relaxed">
          Spatial elements are native Compose components. They respect the same lifecycle and state observation patterns you already know.
        </p>

        <div class="glass-panel p-1 rounded-[2.5rem] bg-white/[0.02] border-white/5">
            <div class="bg-[#0D1117] rounded-[2.2rem] p-2 shadow-2xl">
                <CodeBlock title="MainActivity.kt" lang="kotlin">
                    @Composable<br />
                    fun SpatialScreen() {'{'}<br />
                    &nbsp;&nbsp;SpatialScene(<br />
                    &nbsp;&nbsp;&nbsp;&nbsp;modifier = Modifier.fillMaxSize()<br />
                    &nbsp;&nbsp;) {'{'}<br />
                    &nbsp;&nbsp;&nbsp;&nbsp;OrbitalCamera(distance = 5f)<br />
                    &nbsp;&nbsp;&nbsp;&nbsp;PointLight(color = Color.Cyan, intensity = 10f)<br />
                    &nbsp;&nbsp;&nbsp;&nbsp;Model(src = <span class="text-primary">"models/robot.glb"</span>, scale = 1.5f)<br />
                    &nbsp;&nbsp;{'}'}<br />
                    {'}'}
                </CodeBlock>
            </div>
        </div>
      </section>

      <!-- API Reference Grid -->
      <section class="space-y-12">
        <div class="flex items-center gap-4">
          <div class="w-12 h-12 rounded-2xl bg-tertiary/10 border border-tertiary/20 flex items-center justify-center text-tertiary shadow-[0_0_20px_rgba(139,92,246,0.1)]">
            <span class="material-symbols-outlined text-2xl">terminal</span>
          </div>
          <h2 class="text-3xl font-black text-white tracking-tight">Core API Reference</h2>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          {#each [
            { id: 'SpatialScene', color: 'text-primary', desc: 'The root container orchestrating the GPU context and VSync synchronization.' },
            { id: 'OrbitalCamera', color: 'text-secondary', desc: 'Touch-enabled view controller with cinematic inertia and pitch safety clamps.' },
            { id: 'SpatialNode', color: 'text-tertiary', desc: 'The base unit for 3D state, supporting meshes, PBR materials, and local transforms.' },
            { id: 'LightSystem', color: 'text-accent', desc: 'High-dynamic range point and directional light sources for realistic scene shading.' }
          ] as item}
            <GlassPanel class="p-8 rounded-[2rem] border border-white/5 bg-[#0D1117]/50 group" glow>
              <h3 class="font-mono text-xs font-black {item.color} mb-3 uppercase tracking-[0.2em] italic">{item.id}</h3>
              <p class="text-sm text-silver/60 leading-relaxed font-medium group-hover:text-white transition-colors">
                {item.desc}
              </p>
            </GlassPanel>
          {/each}
        </div>
      </section>

      <!-- AI Assistant -->
      <section class="space-y-8 pt-12">
        <div class="flex items-center justify-between">
            <div class="flex items-center gap-4">
                <div class="w-12 h-12 rounded-2xl bg-primary/10 border border-primary/20 flex items-center justify-center text-primary shadow-[0_0_20px_rgba(25,230,210,0.1)] animate-pulse">
                    <span class="material-symbols-outlined text-2xl">smart_toy</span>
                </div>
                <h2 class="text-3xl font-black text-white tracking-tight">Neural Guide</h2>
            </div>
            <span class="px-4 py-1 bg-primary/10 rounded-full border border-primary/20 text-[10px] text-primary font-black tracking-widest uppercase">Expert Agent Beta</span>
        </div>

        <p class="text-base text-silver/60 max-w-2xl font-medium leading-relaxed italic">
          "Have complex questions about VBO instancing or shader interpolation? Our specialized AI has indexed the entire Spatial codebase to help you in real-time."
        </p>

        <div class="glass-panel p-1 rounded-[3rem] bg-white/[0.02] border-white/5 overflow-hidden shadow-2xl">
          <iframe
            src="https://udify.app/chatbot/XZWJsDVSqxQtNsHH"
            style="width: 100%; height: 100%; min-height: 800px"
            class="rounded-[2.8rem] bg-[#05070D]"
            frameborder="0"
            allow="microphone">
          </iframe>
        </div>
      </section>
    </div>
  </div>
</div>
