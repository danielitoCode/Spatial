<script lang="ts">
  import ImperativeCard from './ImperativeCard.svelte';
  import DeclarativeCard from './DeclarativeCard.svelte';
</script>

<section class="py-24 bg-[#101624]">
  <div class="max-w-7xl mx-auto px-6 text-center mb-16">
    <h2 class="text-3xl md:text-5xl font-bold text-[#e1e2ec] mb-4">Declarative 3D</h2>
    <p class="text-base md:text-lg text-[#bacac6] max-w-2xl mx-auto">
      Stop writing boilerplate. Define your scene exactly like your UI: through state, not instructions.
    </p>
  </div>

  <div class="max-w-5xl mx-auto grid md:grid-cols-2 gap-8 px-4">
    <ImperativeCard />
    <DeclarativeCard />
  </div>
</section>
