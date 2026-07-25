<script lang="ts">
  const techItems = [
    { icon: 'terminal', label: 'Kotlin', color: 'text-[#19E6D2]' },
    { icon: 'android', label: 'Android', color: 'text-[#159FE8]' },
    { icon: 'layers', label: 'Jetpack Compose', color: 'text-[#8B5CF6]' },
    { icon: 'view_in_ar', label: '3D Rendering', color: 'text-[#19E6D2]' }
  ];
</script>

<section class="border-y border-[#1C2638] bg-[#0A0E17]/60 backdrop-blur-sm py-6 overflow-hidden">
  <div class="max-w-7xl mx-auto px-6 flex flex-wrap justify-around items-center gap-6 opacity-80">
    {#each techItems as item}
      <div class="flex items-center gap-2.5 font-mono text-sm text-[#A6B0C3]">
        <span class="material-symbols-outlined {item.color}">{item.icon}</span>
        <span>{item.label}</span>
      </div>
    {/each}
  </div>
</section>
