<script lang="ts">
  import FeatureCard from './FeatureCard.svelte';

  const features = [
    {
      icon: 'layers',
      title: 'Declarative Scene',
      description: 'Compose Scene + Element.Cube / Sphere / Plane / Model with Modifier3D for size, position, and color.',
      badgeColor: 'bg-[#19E6D2]/10 border-[#19E6D2]/20',
      iconColor: 'text-[#19E6D2]'
    },
    {
      icon: '360',
      title: 'Orbit Camera',
      description: 'CameraState with yaw, pitch, zoom, auto-rotate, and natural turntable orbit gestures.',
      badgeColor: 'bg-[#159FE8]/10 border-[#159FE8]/20',
      iconColor: 'text-[#159FE8]'
    },
    {
      icon: 'touch_app',
      title: 'Gestures',
      description: 'Orbit and pinch-zoom wired through Gestures.orbitAndZoom() with sensitivity controls.',
      badgeColor: 'bg-[#8B5CF6]/10 border-[#8B5CF6]/20',
      iconColor: 'text-[#8B5CF6]'
    },
    {
      icon: 'deployed_code',
      title: 'GLB Pipeline',
      description: 'Element.Model + rememberModel load .glb assets (normals, UVs, multi-mesh in progress on Core #2).',
      badgeColor: 'bg-[#19E6D2]/10 border-[#19E6D2]/20',
      iconColor: 'text-[#19E6D2]'
    },
    {
      icon: 'straighten',
      title: 'Typed Units',
      description: 'spatial-units exposes meters, degrees, and related helpers so scene sizes stay readable.',
      badgeColor: 'bg-[#159FE8]/10 border-[#159FE8]/20',
      iconColor: 'text-[#159FE8]'
    },
    {
      icon: 'speed',
      title: 'GLES 3.0 Renderer',
      description: 'OpenGL ES 3.0 host via spatial-renderer and runtime adapter — flat-color materials in Core #1.',
      badgeColor: 'bg-[#8B5CF6]/10 border-[#8B5CF6]/20',
      iconColor: 'text-[#8B5CF6]'
    }
  ];
</script>

<section id="features-section" class="py-24 bg-[#05070D]">
  <div class="max-w-7xl mx-auto px-6">
    <h2 class="text-3xl md:text-5xl font-bold text-center mb-16 text-[#e1e2ec]">Built for Precision</h2>

    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
      {#each features as f}
        <FeatureCard
          icon={f.icon}
          title={f.title}
          description={f.description}
          badgeColor={f.badgeColor}
          iconColor={f.iconColor}
        />
      {/each}
    </div>
  </div>
</section>
