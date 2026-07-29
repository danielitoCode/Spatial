<script lang="ts">
  import SpatialButton from '../../components/SpatialButton.svelte';
  import GradientText from '../../components/GradientText.svelte';

  interface Props {
    onNavigate?: (route: string) => void;
  }

  let { onNavigate }: Props = $props();

  function navigate(e: MouseEvent, targetRoute: string) {
    if (onNavigate) {
      e.preventDefault();
      onNavigate(targetRoute);
    }
  }
</script>

<div class="relative z-10 max-w-7xl mx-auto px-6 text-center md:text-left pt-20 pb-32">
  <div class="max-w-3xl space-y-8">
    <div class="inline-flex items-center gap-3 bg-white/5 backdrop-blur-xl px-4 py-1.5 rounded-full border border-white/10 shadow-2xl animate-float">
       <span class="w-2 h-2 rounded-full bg-primary shadow-[0_0_10px_#19E6D2]"></span>
       <span class="text-[10px] font-black text-silver/80 tracking-[0.25em] uppercase">0.1.0-alpha01 · Core #1 in progress</span>
    </div>

    <h1 class="text-5xl md:text-7xl lg:text-8xl font-black tracking-tighter leading-[0.9] text-white">
      Build <GradientText class="italic">declarative</GradientText><br/>
      3D for Android.
    </h1>

    <p class="text-lg md:text-xl text-silver/60 max-w-xl leading-relaxed font-medium">
      A Compose-first 3D library for Kotlin: scene graph, orbit camera, gestures, and OpenGL ES 3.0 — without writing a render loop by hand.
    </p>

    <div class="flex flex-col sm:flex-row gap-5 justify-center md:justify-start pt-4">
      <SpatialButton
        variant="primary"
        size="lg"
        href="#playground"
        onclick={(e: MouseEvent) => navigate(e, 'playground')}
      >
        OPEN STUDIO
        <span class="material-symbols-outlined text-lg">rocket_launch</span>
      </SpatialButton>

      <SpatialButton
        variant="secondary"
        size="lg"
        href="#docs"
        onclick={(e: MouseEvent) => navigate(e, 'docs')}
      >
        READ DOCS
        <span class="material-symbols-outlined text-lg">menu_book</span>
      </SpatialButton>
    </div>

    <!-- Tech Badges -->
    <div class="flex flex-wrap items-center gap-6 pt-8 opacity-40">
        <div class="flex items-center gap-2 grayscale brightness-200">
            <img src="/spatial_icon_cleaned.svg" class="w-5 h-5 opacity-80" alt="GLES 3.0" />
            <span class="text-[10px] font-black tracking-widest uppercase text-white">OpenGL ES 3.0+</span>
        </div>
        <div class="w-px h-3 bg-white/20"></div>
        <span class="text-[10px] font-black tracking-widest uppercase text-white">Jetpack Compose</span>
        <div class="w-px h-3 bg-white/20"></div>
        <span class="text-[10px] font-black tracking-widest uppercase text-white">Kotlin 2.2</span>
    </div>
  </div>
</div>

<!-- Scroll Indicator -->
<div class="absolute bottom-12 left-1/2 -translate-x-1/2 flex flex-col items-center gap-2 text-silver/30 group cursor-pointer transition-colors hover:text-primary">
  <span class="text-[9px] uppercase tracking-[0.4em] font-black">Scroll</span>
  <div class="w-5 h-8 border-2 border-current rounded-full flex justify-center pt-1.5">
    <div class="w-1 h-1.5 bg-current rounded-full animate-bounce"></div>
  </div>
</div>
