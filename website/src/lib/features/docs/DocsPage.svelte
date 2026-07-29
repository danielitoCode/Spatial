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
        Implementation Guide · 0.1.0-alpha01
      </div>
      <h1 class="text-4xl md:text-6xl lg:text-7xl font-black text-white tracking-tighter leading-tight">
        Spatial <GradientText>Developer Docs</GradientText>
      </h1>
      <p class="text-lg text-silver/50 leading-relaxed max-w-2xl mx-auto font-medium">
        Public Compose API from <span class="text-silver/80 font-mono text-sm">com.elitec.spatial_compose</span>. Core #1 is still stabilizing on device; treat the surface as alpha.
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
                    &nbsp;&nbsp;implementation(<span class="text-primary">"io.github.danielitocode:spatial:0.1.0-alpha01"</span>)<br />
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
          Host GLES with <span class="font-mono text-sm text-silver/80">DefaultSceneRenderHostFactory</span>. Primitives and models are Compose children of <span class="font-mono text-sm text-silver/80">Scene</span>.
        </p>

        <div class="glass-panel p-1 rounded-[2.5rem] bg-white/[0.02] border-white/5">
            <div class="bg-[#0D1117] rounded-[2.2rem] p-2 shadow-2xl">
                <CodeBlock title="CoreOneScene.kt" lang="kotlin">
                    @Composable<br />
                    fun CoreOneScene() {'{'}<br />
                    &nbsp;&nbsp;val cameraState = rememberCameraState()<br />
                    &nbsp;&nbsp;Scene(<br />
                    &nbsp;&nbsp;&nbsp;&nbsp;modifier = Modifier.fillMaxSize(),<br />
                    &nbsp;&nbsp;&nbsp;&nbsp;renderHostFactory = DefaultSceneRenderHostFactory,<br />
                    &nbsp;&nbsp;&nbsp;&nbsp;cameraState = cameraState,<br />
                    &nbsp;&nbsp;&nbsp;&nbsp;gestures = Gestures.orbitAndZoom(),<br />
                    &nbsp;&nbsp;) {'{'}<br />
                    &nbsp;&nbsp;&nbsp;&nbsp;Element.Cube(<br />
                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;modifier = Modifier3D.Default<br />
                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.size(2f.meters)<br />
                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.position(0f, 0f, -5f),<br />
                    &nbsp;&nbsp;&nbsp;&nbsp;)<br />
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
            { id: 'Scene', color: 'text-primary', desc: 'Root Compose host: scene graph, gestures, camera snapshot, and frame submission to the render host.' },
            { id: 'Element', color: 'text-secondary', desc: 'Cube, Sphere, Plane, and Model entry points. Models use ModelResource + rememberModel.' },
            { id: 'CameraState', color: 'text-tertiary', desc: 'Yaw, pitch, zoom, auto-rotate, and gesture interaction epochs for orbit / pinch.' },
            { id: 'Modifier3D', color: 'text-accent', desc: 'Size, position, color, and optional material override applied when building RenderableNode.' }
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
          "Questions about the public API or module boundaries? Prefer the GitHub README and roadmap trackers for source-of-truth status."
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
