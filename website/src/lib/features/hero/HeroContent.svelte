<script lang="ts">
  import SpatialButton from '../../components/SpatialButton.svelte';
  import GradientText from '../../components/GradientText.svelte';

  interface Props {
    onNavigate?: (route: string) => void;
  }

  let { onNavigate }: Props = $props();

  function navigate(e: MouseEvent, targetRoute: string) {
    e.preventDefault();
    if (onNavigate) {
      onNavigate(targetRoute);
    } else {
      window.location.hash = targetRoute;
    }
  }
</script>

<div class="relative z-10 max-w-7xl mx-auto px-6 text-center md:text-left pt-12 pb-24">
  <div class="max-w-3xl">
    <h1 class="text-4xl md:text-6xl lg:text-7xl font-extrabold tracking-tight leading-tight mb-6 text-[#e1e2ec]">
      Build <GradientText>3D experiences</GradientText> for Android.
    </h1>

    <p class="text-lg md:text-xl text-[#bacac6] mb-10 leading-relaxed font-normal">
      A technical premium 3D rendering library designed for Kotlin and Jetpack Compose. Performance-first, declarative by nature.
    </p>

    <div class="flex flex-col sm:flex-row gap-4 justify-center md:justify-start">
      <SpatialButton
        variant="primary"
        size="lg"
        href="#playground"
        onclick={(e: MouseEvent) => navigate(e, 'playground')}
      >
        Start Building (Playground)
        <span class="material-symbols-outlined text-xl">rocket_launch</span>
      </SpatialButton>

      <SpatialButton
        variant="secondary"
        size="lg"
        href="#examples"
        onclick={(e: MouseEvent) => navigate(e, 'examples')}
      >
        View 3D Examples
        <span class="material-symbols-outlined text-xl">code</span>
      </SpatialButton>
    </div>
  </div>
</div>

<!-- Scroll Indicator -->
<div class="absolute bottom-8 left-1/2 -translate-x-1/2 animate-bounce flex flex-col items-center gap-1 text-[#6F7A90]">
  <span class="text-[10px] uppercase tracking-widest font-mono font-semibold">Discover</span>
  <span class="material-symbols-outlined text-sm">expand_more</span>
</div>
