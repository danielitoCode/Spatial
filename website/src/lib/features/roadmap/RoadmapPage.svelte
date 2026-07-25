<script lang="ts">
  import GlassPanel from '../../components/GlassPanel.svelte';
  import GradientText from '../../components/GradientText.svelte';

  const roadmapItems = [
    {
      phase: 'Core #1',
      title: 'Production Engine Stability & VSYNC Pipeline',
      status: 'Completed / Functional',
      statusColor: 'bg-emerald-500/10 text-emerald-400 border-emerald-500/20',
      badgeIcon: 'check_circle',
      description:
        'Foundation 3D rendering pipeline for Android and Jetpack Compose. Includes VSYNC frame scheduling, thematic clear colors, and procedural geometries.',
      features: [
        'Vulkan & OpenGL ES 3.0 multi-threaded rendering engine',
        'VSync-synchronized ChoreographerFrameScheduler with frame pacing',
        'FrameSnapshot.clearColor dynamic theme propagation',
        'Declarative Compose primitives: Box (Cube), Sphere, Plane, Torus',
        'Touch-enabled OrbitalCamera with smooth inertia lerping',
        'PointLight & AmbientLight PBR materials'
      ]
    },
    {
      phase: 'Core #2',
      title: 'Asset Pipeline & 3D Layout Engine',
      status: 'Active Development',
      statusColor: 'bg-amber-500/10 text-amber-400 border-amber-500/20',
      badgeIcon: 'rocket_launch',
      description:
        'Expanding Spatial abstractions to support external GLTF/GLB models, Compose-like 3D layout containers, and camera motion timelines.',
      features: [
        'glTF 2.0 (.glb) binary model importer via Element.Model(resource)',
        '3D Spatial Box Layout System (Box3D, Row3D, Column3D, Stack3D)',
        'Intrinsic 3D Bounding Box alignment & auto-spacing algorithms',
        'CameraSequence DSL with keyframe timelines & easing curves (FastInFastOut, CubicBezier)'
      ]
    },
    {
      phase: 'Core #3',
      title: 'XR & AI Display Glasses Integration',
      status: 'Planned',
      statusColor: 'bg-[#8B5CF6]/10 text-[#8B5CF6] border-[#8B5CF6]/20',
      badgeIcon: 'view_in_ar',
      description:
        'Projected application framework for Android XR and AI Display Glasses using Jetpack Compose Glimmer.',
      features: [
        'Jetpack Compose Glimmer UI toolkit integration',
        'Optimized stereoscopic spatial rendering & head-tracking anchor pipeline',
        'Ultra low-latency HUD overlays for smart glasses form factors'
      ]
    }
  ];
</script>

<div class="py-16 bg-[#05070D] min-h-[85vh]">
  <div class="max-w-5xl mx-auto px-6 space-y-12">
    <!-- Header -->
    <div class="text-center max-w-3xl mx-auto">
      <div class="inline-block px-3.5 py-1 rounded-full bg-[#19E6D2]/10 border border-[#19E6D2]/20 text-[#19E6D2] font-mono text-xs font-semibold tracking-wider mb-4">
        PROJECT ROADMAP
      </div>
      <h1 class="text-3xl md:text-5xl font-extrabold text-[#e1e2ec] mb-4">
        Spatial Engine <GradientText>Evolution & Roadmap</GradientText>
      </h1>
      <p class="text-base text-[#bacac6] leading-relaxed">
        Track the milestones of Spatial from Core #1 production stability to glTF model pipelines and XR AI Glasses integration.
      </p>
    </div>

    <!-- Timeline List -->
    <div class="space-y-8 relative before:absolute before:inset-0 before:left-4 md:before:left-1/2 before:w-0.5 before:bg-[#1C2638] before:-translate-x-1/2">
      {#each roadmapItems as item, index}
        <div class="relative flex flex-col md:flex-row items-center gap-8 group">
          <!-- Timeline Marker -->
          <div class="absolute left-4 md:left-1/2 -translate-x-1/2 z-10 w-8 h-8 rounded-full bg-[#0A0E17] border-2 border-[#19E6D2] flex items-center justify-center text-[#19E6D2] shadow-lg shadow-[#19E6D2]/20">
            <span class="material-symbols-outlined text-sm">{item.badgeIcon}</span>
          </div>

          <!-- Content Card -->
          <GlassPanel class="w-full ml-10 md:ml-0 md:w-[calc(50%-2rem)] {index % 2 === 0 ? 'md:mr-auto' : 'md:ml-auto'} p-6 rounded-2xl border border-[#1C2638] bg-[#0A0E17]" glow>
            <div class="flex items-center justify-between gap-2 mb-3">
              <span class="font-mono text-xs font-bold text-[#19E6D2] uppercase tracking-wider">{item.phase}</span>
              <span class="px-2.5 py-0.5 rounded-full text-[11px] font-mono font-semibold border {item.statusColor}">
                {item.status}
              </span>
            </div>

            <h3 class="text-xl font-bold text-[#e1e2ec] mb-2">{item.title}</h3>
            <p class="text-xs text-[#6F7A90] leading-relaxed mb-4">{item.description}</p>

            <ul class="space-y-2 pt-2 border-t border-[#1C2638]">
              {#each item.features as feat}
                <li class="flex items-start gap-2 text-xs text-[#bacac6]">
                  <span class="material-symbols-outlined text-[#19E6D2] text-sm mt-0.5">check</span>
                  <span>{feat}</span>
                </li>
              {/each}
            </ul>
          </GlassPanel>
        </div>
      {/each}
    </div>
  </div>
</div>
