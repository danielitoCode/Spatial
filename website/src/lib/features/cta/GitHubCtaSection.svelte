<script lang="ts">
  import SpatialButton from '../../components/SpatialButton.svelte';
</script>

<section class="py-24 relative overflow-hidden bg-[#05070D]">
  <div class="absolute inset-0 bg-gradient-to-r from-[#19E6D2]/5 via-[#8B5CF6]/5 to-[#159FE8]/5 pointer-events-none"></div>

  <div class="max-w-7xl mx-auto px-6 relative z-10 flex flex-col items-center text-center">
    <div class="w-16 h-16 rounded-2xl bg-[#e1e2ec] text-[#0A0E17] flex items-center justify-center mb-6 shadow-xl shadow-[#19E6D2]/10">
      <span class="material-symbols-outlined text-4xl" style="font-variation-settings: 'FILL' 1;">
        code_blocks
      </span>
    </div>

    <h2 class="text-3xl md:text-5xl font-bold mb-4 text-[#e1e2ec]">Open Source. High Performance.</h2>

    <p class="text-base md:text-lg text-[#bacac6] max-w-2xl mb-10 leading-relaxed">
      Join our community of graphics engineers. Help us build the future of 3D on Android. Contributions, issues, and stars are welcome.
    </p>

    <SpatialButton
      variant="secondary"
      size="lg"
      href="https://github.com/spatial/rendering"
      target="_blank"
      class="bg-[#e1e2ec] text-[#0A0E17] hover:bg-white border-none font-bold"
    >
      <span class="material-symbols-outlined text-xl">star</span>
      Star on GitHub
    </SpatialButton>
  </div>
</section>
