<script lang="ts">
  import GlassPanel from '../../components/GlassPanel.svelte';
  import GradientText from '../../components/GradientText.svelte';

  const roadmapItems = [
    {
      phase: 'Core #1',
      title: 'Stability & Pipeline',
      status: 'Functional',
      statusColor: 'text-primary border-primary/30 bg-primary/5',
      badgeIcon: 'verified',
      description:
        'Foundation 3D rendering pipeline for Android and Jetpack Compose. Includes VSYNC frame scheduling and procedural geometries.',
      features: [
        'OpenGL ES 3.0 multi-threaded engine',
        'ChoreographerFrameScheduler (VSync)',
        'Declarative Compose primitives',
        'OrbitalCamera with inertia',
        'Flat-color PBR Material shading'
      ]
    },
    {
      phase: 'Core #2',
      title: 'Asset & Layout Engine',
      status: 'Active',
      statusColor: 'text-amber-400 border-amber-500/30 bg-amber-500/5',
      badgeIcon: 'rocket_launch',
      description:
        'Expanding abstractions to support external models, Compose-like 3D layout containers, and camera motion timelines.',
      features: [
        'glTF 2.0 (.glb) binary loader',
        '3D Layout Engine (Box3D, Row3D)',
        'Intrinsic Bounding Box alignment',
        'CameraSequence keyframe DSL'
      ]
    },
    {
      phase: 'Core #3',
      title: 'XR & AI Display',
      status: 'Planned',
      statusColor: 'text-tertiary border-tertiary/30 bg-tertiary/5',
      badgeIcon: 'view_in_ar',
      description:
        'Projected application framework for Android XR and AI Display Glasses using Jetpack Compose Glimmer.',
      features: [
        'Stereoscopic spatial rendering',
        'Head-tracking anchor pipeline',
        'Low-latency HUD HUD overlays',
        'Spatial UI gesture integration'
      ]
    }
  ];
</script>

<div class="py-32 bg-[#05070D] min-h-screen">
  <div class="max-w-6xl mx-auto px-6 space-y-24">
    <!-- Header -->
    <div class="text-center max-w-3xl mx-auto space-y-6">
      <div class="inline-flex items-center gap-2 px-4 py-1 rounded-full bg-primary/10 border border-primary/20 text-primary font-black text-[10px] tracking-[0.3em] uppercase">
        Future Trajectory
      </div>
      <h1 class="text-4xl md:text-6xl lg:text-7xl font-black text-white tracking-tighter leading-tight">
        Spatial Engine <GradientText>Evolution</GradientText>
      </h1>
      <p class="text-lg text-silver/50 leading-relaxed max-w-2xl mx-auto font-medium">
        Tracking the transition from production stability to advanced asset pipelines and the next generation of XR interfaces.
      </p>
    </div>

    <!-- Timeline List -->
    <div class="space-y-4 relative before:absolute before:inset-0 before:left-4 md:before:left-1/2 before:w-px before:bg-white/5 before:-translate-x-1/2">
      {#each roadmapItems as item, index}
        <div class="relative flex flex-col md:flex-row items-center gap-12 group py-8">
          <!-- Timeline Marker -->
          <div class="absolute left-4 md:left-1/2 -translate-x-1/2 z-10 w-10 h-10 rounded-2xl bg-[#0D1117] border border-white/10 flex items-center justify-center text-primary group-hover:border-primary/50 group-hover:shadow-[0_0_20px_rgba(25,230,210,0.3)] transition-all duration-500 group-hover:rotate-[360deg]">
            <span class="material-symbols-outlined text-lg">{item.badgeIcon}</span>
          </div>

          <!-- Content Card -->
          <div class="w-full ml-12 md:ml-0 md:w-[calc(50%-3rem)] {index % 2 === 0 ? 'md:mr-auto' : 'md:ml-auto'} transition-all duration-700 group-hover:translate-x-2 md:group-hover:translate-x-0">
            <GlassPanel class="p-8 rounded-[2rem] border border-white/5 bg-[#0D1117]/50" glow>
                <div class="flex items-center justify-between gap-4 mb-6">
                    <span class="font-black text-[10px] text-silver/40 uppercase tracking-[0.2em] italic">{item.phase}</span>
                    <span class="px-3 py-1 rounded-xl text-[9px] font-black uppercase tracking-widest border {item.statusColor}">
                        {item.status}
                    </span>
                </div>

                <h3 class="text-2xl font-black text-white mb-3 tracking-tight group-hover:text-primary transition-colors">{item.title}</h3>
                <p class="text-sm text-silver/60 leading-relaxed mb-8 font-medium italic">"{item.description}"</p>

                <div class="grid grid-cols-1 gap-4 pt-6 border-t border-white/5">
                    {#each item.features as feat}
                        <div class="flex items-start gap-3 group/item">
                            <div class="w-1.5 h-1.5 rounded-full bg-primary/30 mt-1.5 group-hover/item:bg-primary group-hover/item:scale-125 transition-all"></div>
                            <span class="text-xs text-silver/80 font-bold tracking-tight">{feat}</span>
                        </div>
                    {/each}
                </div>
            </GlassPanel>
          </div>
        </div>
      {/each}
    </div>
  </div>
</div>
