<script lang="ts">
  import FeatureCard from './FeatureCard.svelte';

  const features = [
    {
      icon: 'deployed_code',
      title: 'PBR Rendering',
      description: 'Physically based rendering with advanced material support, including metallic, roughness, and normal mapping.',
      badgeColor: 'bg-[#19E6D2]/10 border-[#19E6D2]/20',
      iconColor: 'text-[#19E6D2]'
    },
    {
      icon: 'brightness_high',
      title: 'HDR Lighting',
      description: 'Global illumination with high dynamic range. Simulate realistic light environments with minimal performance hit.',
      badgeColor: 'bg-[#159FE8]/10 border-[#159FE8]/20',
      iconColor: 'text-[#159FE8]'
    },
    {
      icon: 'motion_mode',
      title: 'Dynamic Physics',
      description: 'Built-in collision detection and rigid body physics specifically optimized for mobile processor constraints.',
      badgeColor: 'bg-[#8B5CF6]/10 border-[#8B5CF6]/20',
      iconColor: 'text-[#8B5CF6]'
    },
    {
      icon: 'token',
      title: 'Asset Pipeline',
      description: 'Seamless GLTF/GLB importing with automated texture compression and mesh LOD generation.',
      badgeColor: 'bg-[#19E6D2]/10 border-[#19E6D2]/20',
      iconColor: 'text-[#19E6D2]'
    },
    {
      icon: 'architecture',
      title: 'Custom Shaders',
      description: 'Write your own AGSL shaders and apply them as materials to any geometry in the scene graph.',
      badgeColor: 'bg-[#159FE8]/10 border-[#159FE8]/20',
      iconColor: 'text-[#159FE8]'
    },
    {
      icon: 'speed',
      title: 'Low Latency',
      description: 'Minimal input-to-render latency, perfect for interactive AR experiences and highly responsive tools.',
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
